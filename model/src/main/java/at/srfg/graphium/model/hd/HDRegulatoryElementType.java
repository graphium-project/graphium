/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model.hd;

public enum HDRegulatoryElementType {

	TRAFFIC_LIGHT("traffic_light"),
	TRAFFIC_SIGN("traffic_sign"),
	SPEED_LIMIT("speed_limit"),
	RIGHT_OF_WAY("right_of_way"),
	ALL_WAY_STOP("all_way_stop");

	private String value;

	HDRegulatoryElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static HDRegulatoryElementType fromValue(String value) {
		for (HDRegulatoryElementType type : HDRegulatoryElementType.values()) {
			if (type.value.equals(value.toLowerCase())) {
				return type;
			}
		}
		return null;
	}
}
