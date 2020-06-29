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
package at.srfg.graphium.routing.service.impl;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.service.IRoutingService;

/**
 * @author mwimmer
 *
 */
public class MockRoutingServiceImpl//<T extends IBaseWaySegment, N extends LineString, W extends Double, O extends IRoutingOptions> 
	extends GenericRoutingServiceImpl<IWaySegment, LineString, Double, IRoutingOptions>
	implements IRoutingService<IWaySegment, Double, IRoutingOptions> {

	@Override
	protected LineString getNodeGeometry(LineString node) {
		return node;
	}

//	@SuppressWarnings("unchecked")
	@Override
	protected Double sumWeights(Double weight1, Double weight2) {
		return weight1 + weight2;
	}

}
