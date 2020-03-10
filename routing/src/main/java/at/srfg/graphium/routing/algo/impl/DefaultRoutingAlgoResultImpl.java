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
package at.srfg.graphium.routing.algo.impl;

import at.srfg.graphium.routing.algo.IRoutedPath;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;

/**
 * @author mwimmer
 *
 */
public class DefaultRoutingAlgoResultImpl implements IRoutedPath<Double> {

	private IDirectedSegmentSet segments;
	private Double weight;

	public DefaultRoutingAlgoResultImpl(IDirectedSegmentSet segments, Double weight) {
		this.segments = segments;
		this.weight = weight;
	}

	@Override
	public IDirectedSegmentSet getSegments() {
		return segments;
	}

	@Override
	public Double getWeight() {
		return weight;
	}

}
