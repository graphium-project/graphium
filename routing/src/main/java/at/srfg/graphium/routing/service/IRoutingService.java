package at.srfg.graphium.routing.service;

import java.util.List;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;

/**
 *  Interface for a general purpose routing service.
 *  
 *  @author mwimmer & anwagner
 */
public interface IRoutingService<T extends IBaseWaySegment, W extends Object, O extends IRoutingOptions> {

	/**
	 * TODO: überarbeiten
	 * Route in the given graph and return a found route between the coordinates given in startX/Y and endX/Y. 
	 * Routing mode can be specified using the criteria to use, which graph the router should work on can be defined in 
	 * graphName within IRoutingOptions.
	 * 
	 * If a route is found the service will cut the start and endsegment on the coordinates of start and enpoint and 
	 * update the routing summery (length, time, ...)
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @return the found route or null if no route could be found.
	 */
	IRoute<T, W> route(O options);
	

	/**
	 * TODO: überarbeiten
	 * Route in the given graph and return a found route between the given in start and end segment 
	 * Routing mode can be specified using the criteria to use, which graph the router should work on can be defined in 
	 * graphName within IRoutingOptions.
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @param segments the segment in the graph the route has to pass (in order given in list)
	 * @return the found route or null if no route could be found.
	 *
	 */
	IRoute<T, W> route(O options,
			List<T> segments);


}