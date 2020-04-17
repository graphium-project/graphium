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

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;

public class DirectedSegmentSetImpl implements IDirectedSegmentSet {

	private long id;
	private Coordinate startCoord;
	private Coordinate endCoord;
	private List<IDirectedSegment> segments = new ArrayList<>();
	
	public DirectedSegmentSetImpl() {}
	
	public DirectedSegmentSetImpl(long id, List<IDirectedSegment> segments) {
		this.id = id;
		this.segments = segments;
	}
	
	public DirectedSegmentSetImpl(long id, Coordinate startCoord, Coordinate endCoord, List<IDirectedSegment> segments) {
		this.id = id;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.segments = segments;
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
	public Coordinate getStartCoord() {
		return startCoord;
	}

	@Override
	public void setStartCoord(Coordinate startCoord) {
		this.startCoord = startCoord;
	}

	@Override
	public Coordinate getEndCoord() {
		return endCoord;
	}

	@Override
	public void setEndCoord(Coordinate endCoord) {
		this.endCoord = endCoord;
	}
	
	@Override
	public List<IDirectedSegment> getSegments() {
		return segments;
	}

	@Override
	public void setSegments(List<IDirectedSegment> segments) {
		this.segments = segments;
	}

}
