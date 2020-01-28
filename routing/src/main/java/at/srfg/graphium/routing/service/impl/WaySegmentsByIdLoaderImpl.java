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
		List<T> graphSegments = graphDao.getSegmentsById(graphName, graphVersion, new ArrayList<>(segmentIds), false);
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
	
}
