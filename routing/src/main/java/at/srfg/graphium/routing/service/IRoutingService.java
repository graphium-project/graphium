/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.routing.service;

import java.util.List;


import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;

/**
 *  Interface for a general purpose routing service.
 *  
 *  @author mwimmer & anwagner
 */
public interface IRoutingService<T extends IBaseWaySegment> {

	/**
	 * Route in the given graph and return a found route between the coordinates given in startX/Y and endX/Y. 
	 * Routing mode can be specified using the criteria to use, which graph the router should work on can be defined in 
	 * graphName within IRoutingOptions.
	 * 
	 * If a route is found the service will cut the start and endsegment on the coordinates of start and enpoint and 
	 * update the routing summery (length, time, ...)
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @param startX x coordinate of starting point
	 * @param startY y coordinate of starting point
	 * @param endX x coordinate of ending point
	 * @param endY < coordinate of ending point
	 * @return the found route or null if no route could be found.
	 */
	IRoute<T> route(IRoutingOptions options, double startX, double startY,
			double endX, double endY);
	
	/**
	 * Like route but with boolean flag to enable a cutting of start and endpoints using linear referencing
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @param startX x coordinate of starting point
	 * @param startY y coordinate of starting point
	 * @param endX x coordinate of ending point
	 * @param endY < coordinate of ending point	
	 * @param cutStartAndEndSegments true to cut the start and end segment on the input coordinates
	 * @return the found route or null if no route could be found.
	 */
	IRoute<T> route(IRoutingOptions options, double startX, double startY,
			double endX, double endY, boolean cutStartAndEndSegments);
	

	/**
	 * Route in the given graph and return a found route between the given in start and end segment 
	 * Routing mode can be specified using the criteria to use, which graph the router should work on can be defined in 
	 * graphName within IRoutingOptions.
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @param startSegment the segment in the graph the route starts
	 * @param endSegment the segment in the graph the route ends
	 * @return the found route or null if no route could be found.
	 *
	 */
	IRoute<T> route(IRoutingOptions options,
			T startSegment, T endSegment);
	
	
	/**
	 * Route in the given graph and return a found route between the given in start and end nodes (representing segments) 
	 * Routing mode can be specified using the criteria to use, which graph the router should work on can be defined in 
	 * graphName within IRoutingOptions.
	 * 
	 * @param options options for algorithm (e.q. cost criteria used to calcualate the best route (e.g. time or length))
	 * @param startCoord coordinate of starting point
	 * @param endNodes coordinate of ending point
	 * @return the found route or null if no route could be found.
	 *
	 */
	IRoute<T> doRoute(IRoutingOptions options, Coordinate startCoord, Coordinate endCoord);

}