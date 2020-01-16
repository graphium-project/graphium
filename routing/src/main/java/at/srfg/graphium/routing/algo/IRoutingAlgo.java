package at.srfg.graphium.routing.algo;

import java.util.List;

import at.srfg.graphium.routing.model.IRoutingOptions;

/**
 * interface for routing algorithm implementation indep. Allows routing from one sourceNode in Graph to one targetNode.
 * define methods to return optimal route or n optimal routes. 
 * 
 * @author anwagner
 *
 * @param <O> Options for router based on IRoutingOptions
 * @param <N> Native interface / object representing a graph node
 * @param <W> Weight of the route (e.g. Double)
 */
public interface IRoutingAlgo<O extends IRoutingOptions, N, W> {

	/**
	 * find optimal route between source and target node. 
	 * 
	 * @param routeOptions options for router
	 * @param sourceNode source of route
	 * @param precentageStartWeight weight factor to use on source node (in digitalisation direction)
	 * @param targetNode target of route
	 * @param percentageEndWeight  weight factor to use on target node (in digitalisation direction)
	 * @return best found route between source and target node
	 */
	public IRoutedPath<W> bestRoute(O routeOptions, N sourceNode, float precentageStartWeight, N targetNode,
			float percentageEndWeight);

	/**
	 * find n given amount best routes between source and target node
	 * 
	* @param routeOptions options for router
	 * @param sourceNode source of route
	 * @param precentageStartWeight weight factor to use on source node (in digitalisation direction)
	 * @param targetNode target of route
	 * @param percentageEndWeight  weight factor to use on target node (in digitalisation direction)
	 * @param amount amount of best routes to return
	 * @return N (amount) best found route between source and target node, can be less then amount if fewer routes are found
	 */
	public List<IRoutedPath<W>> bestRoutes(O routeOptions, N sourceNode, float precentageStartWeight, N targetNode,
			float percentageEndWeight, short amount);

	/**
	 * Requests a cancellation of the algorithm execution
	 */
	public void cancel();
	
}
