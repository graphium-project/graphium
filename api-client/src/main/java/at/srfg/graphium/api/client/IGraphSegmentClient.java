package at.srfg.graphium.api.client;

import java.util.List;
import java.util.Set;

import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.model.IBaseWaySegment;

public interface IGraphSegmentClient<T extends IBaseWaySegment> {

	public List<T> getSegments(String graphName, String graphVersion, Set<Long> ids)
			throws GraphNotFoundException, GraphiumServerAccessException;

}
