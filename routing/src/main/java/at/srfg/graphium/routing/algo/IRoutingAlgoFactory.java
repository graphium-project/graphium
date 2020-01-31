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

import at.srfg.graphium.routing.exception.UnkownRoutingAlgoException;
import at.srfg.graphium.routing.model.IRoutingOptions;

/**
 * interface for factory creating instances of routing algorithms based on passed options
 * 
 * @author anwagner
 *
 * @param <O> type of options object
 * @param <N> type native graph representation object
 * @param <W> type of weight
 */
public interface IRoutingAlgoFactory<O extends IRoutingOptions, N, W> {

	/**
	 * creates instance of the algorithm
	 * 
	 * @param routeOptions options for algorithm
	 * @param startNode start node in native graph representation
	 * @param percentageStartWeight % of weight to use on start node (in digit. direction of waysegment)
	 * @param endNode end node in native graph representation
	 * @param percentageEndWeight  % of weight to use on end node (in digit. direction of waysegment)
	 * @return instance of configured routing algorithm
	 * 
	 * @throws UnkownRoutingAlgoException in case the asked algo implementation can not be supplied by factory instance
	 */
	public IRoutingAlgo<O, N, W> createInstance(O routeOptions, N startNode, 
			Float percentageStartWeight, N endNode, Float percentageEndWeight) throws UnkownRoutingAlgoException;

}
