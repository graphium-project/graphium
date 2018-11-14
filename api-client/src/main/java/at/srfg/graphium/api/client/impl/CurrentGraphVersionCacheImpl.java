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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

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
public class CurrentGraphVersionCacheImpl extends AbstractGraphiumApiClient
//extends Observable 
implements ICurrentGraphVersionCache {

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
		CachedWayGraphVersionMetadataWrapper metadataNewWrapped = null;
		if(graphName == null) {
			log.error("error, graphname was null, can not create api connection string...");
			return null;
		}

		String uri = getGraphResolvedUrl(GRAPH_READ_CURRENT_VERSION_METADATA, graphName);
		
		CloseableHttpResponse response = null;
		try {
        	
            HttpGet httpget = new HttpGet(uri);
            
            response = this.httpClient.execute(httpget);
            HttpEntity resEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
            	IGraphVersionMetadataDTO dto = objectMapper.readValue(resEntity.getContent(), GraphVersionMetadataDTOImpl.class);
            	IWayGraphVersionMetadata metadataNew = adapter.adapt(dto);
            	// check if metadata is newer then cached one and store if required
            	if (metadataNew != null) {
            		metadataNewWrapped = new CachedWayGraphVersionMetadataWrapper(metadataNew, new Date());
            		if(metadataCurrentWrapped == null || !metadataNew.getVersion().equals(metadataCurrentWrapped.metadata.getVersion())) {            		
            			currentGraphVersions.put(graphName, metadataNewWrapped);
    					log.info("new current version with ID " + metadataNew.getId() + " for graph " + graphName + " found");
    					
    					setChanged();
    					notifyObservers(metadataNew);					
            		}
				}            	            	
            }
            else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            	throw new GraphNotFoundException(graphName);
            }
            else {
            	throw new GraphiumServerAccessException(response.getStatusLine().getStatusCode());
            }
        } catch (IOException | UnsupportedOperationException e) {
			log.error("Error while reading current graph version ID", e);
			throw new GraphiumServerAccessException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				log.error("Error while reading current graph version ID", e);
				throw new GraphiumServerAccessException(e);
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
	
	protected boolean checkRefreshRequired(Date cachedAt) {
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