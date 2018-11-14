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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.core.type.TypeReference;

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
public class GraphVersionCacheImpl extends CurrentGraphVersionCacheImpl implements IGraphVersionCache {

	protected static final String GRAPH_READ_VERSION_METADATA_LIST = "metadata/graphs/{graph}/versions";
	
	protected Map<String, CachedWayGraphVersionMetadataListWrapper> graphVersions = new HashMap<>();

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
				checkRefreshRequired(graphVersions.get(graphName).cachedAt)) {
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
		String uri = getGraphResolvedUrl(GRAPH_READ_VERSION_METADATA_LIST, graphName);
		
		CloseableHttpResponse response = null;
		try {
        	
            HttpGet httpget = new HttpGet(uri);
            
            response = this.httpClient.execute(httpget);
            HttpEntity resEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
            	List<IGraphVersionMetadataDTO> dtoList = objectMapper.readValue(resEntity.getContent(), new TypeReference<List<GraphVersionMetadataDTOImpl>>(){});
            	if (dtoList != null && !dtoList.isEmpty()) {
            		List<IWayGraphVersionMetadata> metadataList = new ArrayList<>();
            		CachedWayGraphVersionMetadataListWrapper metadatas = new CachedWayGraphVersionMetadataListWrapper(metadataList, new Date());
	            	for (IGraphVersionMetadataDTO dto : dtoList) {
	            		metadataList.add(adapter.adapt(dto));
	            	}
	            	graphVersions.put(graphName, metadatas);
            	} else {
            		graphVersions.remove(graphName);
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
	}
	
	class CachedWayGraphVersionMetadataListWrapper {
		List<IWayGraphVersionMetadata> metadataList;
		Date cachedAt;
		
		CachedWayGraphVersionMetadataListWrapper(List<IWayGraphVersionMetadata>	metadataList, Date cachedAt) {
			this.metadataList = metadataList;
			this.cachedAt = cachedAt;
		}
	}

}
