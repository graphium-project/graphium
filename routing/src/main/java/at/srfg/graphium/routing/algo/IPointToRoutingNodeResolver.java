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
