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
package at.srfg.graphium.routing.service;

import java.util.List;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.exception.UnkownRoutingAlgoException;
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
	 * @throws UnkownRoutingAlgoException 
	 */
	IRoute<T, W> route(O options) throws UnkownRoutingAlgoException;
	

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