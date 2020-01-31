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
package at.srfg.graphium.routing.model.impl;

import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoutingAlgorithm;

public enum RoutingAlgorithms implements IRoutingAlgorithm {

	DIJKSTRA("dijkstra"), BIDIRECTIONAL_DIJKSTRA("bidirectional_dijkstra"), ASTAR("astar"),
	BIDIRECTIONAL_ASTAR("bidirectional_astar"), BELLMAN_FORD("bellman_ford"), KSHORTEST("kshortest"), YEN("yen"),
	MARTIN("martin");

	private String value;

	RoutingAlgorithms(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	public static RoutingAlgorithms fromValue(String value) throws RoutingParameterException {
		for (RoutingAlgorithms mode : RoutingAlgorithms.values()) {
			if (mode.value.equals(value.toLowerCase())) {
				return mode;
			}
		}
		throw new RoutingParameterException(value + " is not a valid value of " + RoutingAlgorithms.class);
	}
}
