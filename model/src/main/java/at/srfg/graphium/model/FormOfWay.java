/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model;

/**
 * @author mwimmer
 *
 */
public enum FormOfWay {
	
	NOT_APPLICABLE ((short)-1),
	PART_OF_MOTORWAY ((short)1),
	PART_OF_MULTI_CARRIAGEWAY_WHICH_IS_NOT_A_MOTORWAY ((short)2),
	PART_OF_SINGLE_CARRIAGEWAY ((short)3),
	PART_OF_ROUNDABOUT ((short)4),
	PART_OF_AN_ETA_PARKING_PLACE ((short)6),
	PART_OF_AN_ETA_PARKING_GARAGE ((short)7),
	PART_OF_AN_ETA_UNSTRUCTURED_TRAFFIC_SQUARE ((short)8),
	PART_OF_A_SLIP_ROAD ((short)10),
	PART_OF_A_SERVICE_ROAD ((short)11),
	ENTRANCE_OR_EXIT_TO_OR_FROM_A_CAR_PARK ((short)12),
	PART_OF_A_PEDESTRIAN_ZONE ((short)14),
	PART_OF_A_WALKWAY_OR_BICYCLE_WAY ((short)15),
	SPECIAL_TRAFFIC_FIGURES ((short)17),
	ROAD_FOR_AUTHORITIES ((short)20),
	TRAM ((short)101),
	SUBWAY ((short)102),
	RAILWAY ((short)103);

	private short value;
	
	FormOfWay (short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}
	
	public static FormOfWay getFormOfWayForValue(short value) {
		for (FormOfWay frc : values()) {
			if (frc.getValue() == value) {
				return frc;
			}
		}
		return PART_OF_SINGLE_CARRIAGEWAY; // Default
	}

}
