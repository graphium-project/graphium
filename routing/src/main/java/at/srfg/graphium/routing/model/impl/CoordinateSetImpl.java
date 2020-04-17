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

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.model.ICoordinateSet;

public class CoordinateSetImpl implements ICoordinateSet {

	private long id;
	private List<Coordinate> coordinates;
	
	public CoordinateSetImpl() {}
	
	public CoordinateSetImpl(long id, List<Coordinate> coordinates) {
		this.id = id;
		this.coordinates = coordinates;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	@Override
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

}
