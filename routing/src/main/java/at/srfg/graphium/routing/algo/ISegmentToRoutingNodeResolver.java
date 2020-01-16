package at.srfg.graphium.routing.algo;

import at.srfg.graphium.model.IWaySegment;

/**
 * converter interface from graphium based segments to native object used in routing graph  
 * 
 * @author anwagner
 *
 * @param <T> graphium based WaySegments
 * @param <N> object in routing graph representing the given graphium segment
 */
public interface ISegmentToRoutingNodeResolver<T extends IWaySegment, N> {

	/**
	 * look up method to convert graphium based segment object of given graphName and graphVersion to 
	 * a native objected used in routing graph
	 * 
	 * @param segment the graphium segment
	 * @param graphName graphs name
	 * @param graphVersion version string of the graph
	 * @return the native graph object representing the graphium segment
	 */
	public N resolveSegment(T segment, String graphName, String graphVersion);
}
