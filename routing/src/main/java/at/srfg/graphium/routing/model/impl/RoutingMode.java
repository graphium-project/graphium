/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.model.impl;

// TODO: Erweitern!

public enum RoutingMode {
	CAR("car"),
	BIKE("bike"),
	PEDESTRIAN("pedestrian"),
	PEDESTRIAN_BARRIERFREE("pedestrian_barrierfree");
			
	private String value;
	
	RoutingMode(String value) {
		this.value = value;
	}
	
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
	
	public static RoutingMode fromValue(String value) {	
		for (RoutingMode mode: RoutingMode.values()) {
			if (mode.value.equals(value.toLowerCase())) {
				return mode;
			}
		}
		return null;
	}
}
