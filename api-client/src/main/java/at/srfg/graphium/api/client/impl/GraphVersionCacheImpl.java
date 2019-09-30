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
/**
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import at.srfg.graphium.api.client.IGraphVersionCache;
import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 */
public class GraphVersionCacheImpl extends AbstractGraphiumApiClient<List<IGraphVersionMetadataDTO>> implements IGraphVersionCache {

	protected static final String GRAPH_READ_VERSION_METADATA_LIST = "metadata/graphs/{graph}/versions";
	
	protected Map<String, CachedWayGraphVersionMetadataListWrapper> graphVersions = new HashMap<>();

	protected CurrentGraphVersionCacheImpl currentGraphVersionCache;
	
	public GraphVersionCacheImpl(CurrentGraphVersionCacheImpl currentGraphVersionCache) {
		this.currentGraphVersionCache = currentGraphVersionCache;
	}
	
	@PostConstruct
	public void setup() {
		super.setup();
	}
		
	@Override
	public synchronized IWayGraphVersionMetadata getGraphVersion(String graphName, Date timestamp)
			throws IllegalArgumentException, GraphiumServerAccessException, GraphNotFoundException {
		if (graphName == null) {
			log.error("graphname was null!");
			return null;
		}
		if (!graphVersions.containsKey(graphName) ||
				currentGraphVersionCache.checkRefreshRequired(graphVersions.get(graphName).cachedAt)) {
			readGraphVersionMetadataList(graphName);
		}
		if (!graphVersions.containsKey(graphName)) {
			throw new GraphNotFoundException(graphName);
		}
		
		return getGraphVersionForTimestamp(graphName, timestamp);
	}

	private IWayGraphVersionMetadata getGraphVersionForTimestamp(String graphName, Date timestamp) {
		IWayGraphVersionMetadata metadata = null;
		Iterator<IWayGraphVersionMetadata> it = graphVersions.get(graphName).metadataList.iterator();
		while (it.hasNext() && metadata == null) {
			IWayGraphVersionMetadata md = it.next();
			if (md.getState().equals(State.ACTIVE) &&
					md.getValidFrom().getTime() <= timestamp.getTime() &&
					(md.getValidTo() == null || md.getValidTo().getTime() >= timestamp.getTime())) {
				metadata = md;
			}
		}
		return metadata;
	}

	private void readGraphVersionMetadataList(String graphName) throws GraphiumServerAccessException, GraphNotFoundException {
		String uri = externalGraphserverApiUrl + resolveUrlTemplates(GRAPH_READ_VERSION_METADATA_LIST, Collections.singletonMap("{graph}", graphName));
		List<IGraphVersionMetadataDTO> metadataDTOs = doHttpRequest(uri, graphName, httpEntity -> {
					try {
						return objectMapper.readValue(httpEntity.getContent(), new TypeReference<List<GraphVersionMetadataDTOImpl>>(){});
					} catch (JsonParseException e) {
						log.error("error parsing json stream", e);
					} catch (JsonMappingException e) {
						log.error("error mapping json to DTO", e);
					}
					// TODO: throw
					return null;
				});
		if (metadataDTOs != null && !metadataDTOs.isEmpty()) {
    		List<IWayGraphVersionMetadata> metadataList = new ArrayList<>();
    		CachedWayGraphVersionMetadataListWrapper metadatas = new CachedWayGraphVersionMetadataListWrapper(metadataList, new Date());
        	for (IGraphVersionMetadataDTO dto : metadataDTOs) {
        		metadataList.add(currentGraphVersionCache.getAdapter().adapt(dto));
        	}
        	graphVersions.put(graphName, metadatas);
    	} else {
    		graphVersions.remove(graphName);
    	}
	}
	
	class CachedWayGraphVersionMetadataListWrapper {
		List<IWayGraphVersionMetadata> metadataList;
		Date cachedAt;
		
		CachedWayGraphVersionMetadataListWrapper(List<IWayGraphVersionMetadata>	metadataList, Date cachedAt) {
			this.metadataList = metadataList;
			this.cachedAt = cachedAt;
		}
	}

	// delegate ICurrentGraphVersionCache to injected client
	@Override
	public void readCurrentGraphVersions() {
		currentGraphVersionCache.readCurrentGraphVersions();
	}

	@Override
	public IWayGraphVersionMetadata getCurrentGraphVersion(String graphName)
			throws IllegalArgumentException, GraphiumServerAccessException, GraphNotFoundException {
		return currentGraphVersionCache.getCurrentGraphVersion(graphName);
	}

}
