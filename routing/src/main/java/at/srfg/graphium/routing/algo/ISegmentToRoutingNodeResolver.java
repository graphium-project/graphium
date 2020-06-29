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

import at.srfg.graphium.model.IBaseWaySegment;

/**
 * converter interface from graphium based segments to native object used in routing graph  
 * 
 * @author anwagner
 *
 * @param <T> graphium based WaySegments
 * @param <N> object in routing graph representing the given graphium segment
 */
public interface ISegmentToRoutingNodeResolver<T extends IBaseWaySegment, N> {

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
