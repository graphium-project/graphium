package at.srfg.graphium.routing.algo;

import com.vividsolutions.jts.geom.Point;

/**
 * converter interface from JTS points to native object used in routing graph  
 * 
 * @author anwagner
 *
 * @param <N> object in routing graph representing the given graphium segment
 */
public interface IPointToRoutingNodeResolver<N> {

	/**
	 * look up method to find the closest native graph object to a JTS Point of given graphName and graphVersion
	 * 
	 * @param point the point the closest element should be looked up 
	 * @param searchDistance search distance in km wg984 // TODO: correct?
	 * @param graphName graphs name
	 * @param graphVersion version string of the graph
	 * @return the native graph object closest to given point
	 */
	N resolveSegment(Point point, double searchDistance, String graphName, String graphVersion);
	
}
