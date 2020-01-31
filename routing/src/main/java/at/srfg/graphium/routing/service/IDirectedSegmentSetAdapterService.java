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

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;

public interface IDirectedSegmentSetAdapterService<T extends IRoute<N, W>, N extends IBaseWaySegment, W extends Object> {

	T enrichAndAdapt(IDirectedSegmentSet directedSegments, W calculatedCost,
			IRoutingOptions options, Float percentageStartWeight, Float percentageEndWeight);

	T createEmptyRoute();

}
