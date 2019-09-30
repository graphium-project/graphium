/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.api.client.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import at.srfg.graphium.api.client.ICurrentGraphVersionCache;
import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.impl.GraphVersionMetadataDTO2GraphVersionMetadataAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 */
public class CurrentGraphVersionCacheImpl extends AbstractGraphiumApiClient<IGraphVersionMetadataDTO> implements ICurrentGraphVersionCache {

	protected static final String GRAPH_READ_CURRENT_VERSION_METADATA = "metadata/graphs/{graph}/versions/current";
	
	protected Map<String, CachedWayGraphVersionMetadataWrapper> currentGraphVersions = new HashMap<String, CachedWayGraphVersionMetadataWrapper>();

	// refresh every minute if required
	protected int refreshMetadataIfOlderThenSec = 60;
	
	protected IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> adapter;
	
	@PostConstruct
	public void setup() {
		super.setup();
		if(adapter == null) {
			adapter = new GraphVersionMetadataDTO2GraphVersionMetadataAdapter();
		}
	}
	
	/**
	 * should be triggered periodically
	 */
	@Override
	public void readCurrentGraphVersions() {
		if (currentGraphVersions != null) {
			for (String graphName : currentGraphVersions.keySet()) {
				CachedWayGraphVersionMetadataWrapper metadata = currentGraphVersions.get(graphName);
				try {
					readCurrentGraphVersion(graphName, metadata);
				} catch (GraphiumServerAccessException | GraphNotFoundException e) {
					log.error("error accessing graphium apis", e);
				}
			}
		}
	}
	
	private CachedWayGraphVersionMetadataWrapper readCurrentGraphVersion(String graphName, CachedWayGraphVersionMetadataWrapper metadataCurrentWrapped) 
			throws GraphNotFoundException, GraphiumServerAccessException {
		String uri = externalGraphserverApiUrl +
				resolveUrlTemplates(GRAPH_READ_CURRENT_VERSION_METADATA, Collections.singletonMap("{graph}", graphName));
		
		IGraphVersionMetadataDTO metadataDTO = doHttpRequest(uri, graphName,
				httpEntity -> {
					try {
						return objectMapper.readValue(httpEntity.getContent(), GraphVersionMetadataDTOImpl.class);
					} catch (JsonParseException e) {
						log.error("error parsing json stream", e);
					} catch (JsonMappingException e) {
						log.error("error mapping json to DTO", e);
					}
					// TODO: throw
					return null;
				});
    	IWayGraphVersionMetadata metadataNew = adapter.adapt(metadataDTO);
    	CachedWayGraphVersionMetadataWrapper metadataNewWrapped = null;
    	// check if metadata is newer then cached one and store if required
    	if (metadataNew != null) {    		
    		metadataNewWrapped = new CachedWayGraphVersionMetadataWrapper(metadataNew, new Date());
    		// update in cache to get new cached timestamp
			currentGraphVersions.put(graphName, metadataNewWrapped);
    		if(metadataCurrentWrapped == null || !metadataNew.getVersion().equals(metadataCurrentWrapped.metadata.getVersion())) {            		
				log.info("new current version with ID " + metadataNew.getId() + " for graph " + graphName + " found");				
				setChanged();
				notifyObservers(metadataNew);					
    		}
		} 
    	return metadataNewWrapped;      
	}
	
	@Override
	public synchronized IWayGraphVersionMetadata getCurrentGraphVersion(String graphName)
			throws IllegalArgumentException, GraphiumServerAccessException, GraphNotFoundException {
		if(graphName == null || graphName.isEmpty()) {
			throw new IllegalArgumentException("graphName is required!");
		}
		if (currentGraphVersions == null) {
			currentGraphVersions = new HashMap<String, CachedWayGraphVersionMetadataWrapper>();
		}
		CachedWayGraphVersionMetadataWrapper metadata = currentGraphVersions.get(graphName);
		if (metadata == null  || checkRefreshRequired(metadata.cachedAt)) {
			metadata = readCurrentGraphVersion(graphName, metadata);
		}
		return metadata.metadata;
	}
	
	boolean checkRefreshRequired(Date cachedAt) {
		if (cachedAt == null) {
			return true;
		}
		Date now = new Date();
		Date notOlderThen = new Date(now.getTime() - refreshMetadataIfOlderThenSec
				* 1000);

		if (cachedAt.before(notOlderThen)) {
			return true;
		}
		return false;
	}
	
	public void setNewGraphVersionObserver(List<Observer> observers) {
		if (observers != null) {
			for (Observer observer : observers) {
				addObserver(observer);
			}
		}
	}

	public Map<String, IWayGraphVersionMetadata> getCurrentGraphVersions() {
		Map<String, IWayGraphVersionMetadata> unwrapped =
		    currentGraphVersions.entrySet().stream()
		        .collect(Collectors.toMap(
		        	Map.Entry::getKey,
		            e -> e.getValue().metadata
		        ));
		return unwrapped;
	}
	
	public int getRefreshMetadataIfOlderThenSec() {
		return refreshMetadataIfOlderThenSec;
	}

	public void setRefreshMetadataIfOlderThenSec(int refreshMetadataIfOlderThenSec) {
		this.refreshMetadataIfOlderThenSec = refreshMetadataIfOlderThenSec;
	}

	public IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> getAdapter() {
		return adapter;
	}

	public void setAdapter(IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> adapter) {
		this.adapter = adapter;
	}
	
	class CachedWayGraphVersionMetadataWrapper {
		IWayGraphVersionMetadata metadata;
		Date cachedAt;
		
		CachedWayGraphVersionMetadataWrapper(IWayGraphVersionMetadata metadata, Date cachedAt) {
			this.metadata = metadata;
			this.cachedAt = cachedAt;
		}
	}
}