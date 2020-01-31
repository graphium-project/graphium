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
import at.srfg.graphium.routing.model.IRoutingMode;

// TODO: Erweitern!

public enum RoutingMode implements IRoutingMode {
	CAR("car"), BIKE("bike"), PEDESTRIAN("pedestrian"), PEDESTRIAN_BARRIERFREE("pedestrian_barrierfree");

	private String value;

	RoutingMode(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static RoutingMode fromValue(String value) throws RoutingParameterException {
		for (RoutingMode mode : RoutingMode.values()) {
			if (mode.value.equals(value.toLowerCase())) {
				return mode;
			}
		}
		throw new RoutingParameterException(value + " is not a valid value of " + RoutingMode.class);
	}
}
