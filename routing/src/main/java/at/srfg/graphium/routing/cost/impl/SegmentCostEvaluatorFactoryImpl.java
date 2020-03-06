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
package at.srfg.graphium.routing.cost.impl;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluator;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluatorFactory;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public class SegmentCostEvaluatorFactoryImpl<T extends IBaseWaySegment, C> implements ISegmentCostEvaluatorFactory<T, C> {

	private ISegmentCostEvaluator<T, C> lengthEvaluator;
	private ISegmentCostEvaluator<T, C> durationEvaluator;
	
	public SegmentCostEvaluatorFactoryImpl( ISegmentCostEvaluator<T, C> lengthEvaluator,
			ISegmentCostEvaluator<T, C> durationEvaluator) {
		this.lengthEvaluator = lengthEvaluator;
		this.durationEvaluator = durationEvaluator;
	}
	
	@Override
	public ISegmentCostEvaluator<T, C> getCostEvaluator(RoutingCriteria costAttribute) {
		switch (costAttribute) {
		case LENGTH:
			return lengthEvaluator;
		case MIN_DURATION:
			return durationEvaluator;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
