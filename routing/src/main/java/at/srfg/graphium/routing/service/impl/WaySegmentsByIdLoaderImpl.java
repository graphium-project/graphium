/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.routing.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.service.IWaySegmentsByIdLoader;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public class WaySegmentsByIdLoaderImpl<T extends IBaseWaySegment> implements IWaySegmentsByIdLoader<T>{

	private static Logger log = LoggerFactory.getLogger(WaySegmentsByIdLoaderImpl.class);
	private IWayGraphReadDao<T> graphDao;
	
	public WaySegmentsByIdLoaderImpl(IWayGraphReadDao<T> graphDao) {
		this.graphDao = graphDao;
	}
	
	@Override
	public Map<Long, T> loadSegments(Set<Long> segmentIds, String graphName, String graphVersion) throws GraphNotExistsException {
		if (log.isDebugEnabled()) {
			log.debug("route contains " + segmentIds.size() + " segment ids: " + segmentIds);
		}
		StopWatch timer = new StopWatch();
		timer.start();
		List<T> graphSegments = getSegments(graphName, graphVersion, segmentIds);
		timer.stop();
		
		Map<Long, T> segmentHash = new Long2ObjectLinkedOpenHashMap<T>(graphSegments.size());
		// TODO: check if all required segments are found, otherwise throw exception up. We can not supply output of
		// "routes" which have segmentIds not in graph
		if (log.isDebugEnabled()) {
			log.debug("loaded " + graphSegments.size() + " segments for graph/version:" 
				+ graphName + "/" + graphVersion + " from db. loading took " + timer.getTime() + " ms");
		}
		for(T segment : graphSegments) {
			segmentHash.put(segment.getId(), segment);
		}
		return segmentHash;
	}

	// child classes could use a cache
	protected List<T> getSegments(String graphName, String graphVersion, Set<Long> segmentIds) throws GraphNotExistsException {
		return graphDao.getSegmentsById(graphName, graphVersion, new ArrayList<>(segmentIds), false);
	}
	
}
