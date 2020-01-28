package at.srfg.graphium.routing.service;

import java.util.Map;
import java.util.Set;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.model.IBaseWaySegment;

public interface IWaySegmentsByIdLoader<T extends IBaseWaySegment> {

	Map<Long, T> loadSegments(Set<Long> segmentIds, String graphName, String graphVersion)
			throws GraphNotExistsException;

	
	
}
