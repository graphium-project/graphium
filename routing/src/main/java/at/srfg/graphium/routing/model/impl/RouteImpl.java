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

import java.io.Serializable;
import java.util.List;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IRoute;

public class RouteImpl<T extends IBaseWaySegment, W extends Object> implements IRoute<T, W>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private W weight;
	private List<IDirectedSegment> path;
	private float length;
	private int duration;
	private List<T> segments;
	private LineString lineString;
	private int runtimeInMs  = -1;
	private String graphName;
	private String graphVersion;

	public RouteImpl() {}
	public RouteImpl(W weight, List<IDirectedSegment> path, float length, int duration, List<T> segments, LineString lineString, 
			int runtimeInMs, String graphName, String graphVersion) {
		this.weight = weight;
		this.path = path;
		this.length = length;
		this.duration = duration;
		this.segments = segments;
		this.lineString = lineString;
		this.runtimeInMs = runtimeInMs;
		this.graphName = graphName;
		this.graphVersion = graphVersion;
	}
	
	@Override
	public W getWeight() {
		return weight;
	}
	@Override
	public void setWeight(W weight) {
		this.weight = weight;
	}
	
	@Override
	public List<IDirectedSegment> getPath() {
		return path;
	}

	@Override
	public float getLength() {
		return length;
	}
	
	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public void setPath(List<IDirectedSegment> path) {
		this.path = path;
	}

	@Override
	public void setLength(float length) {
		// TODO: use DecimalFormat for reducing decimal points
		this.length = length;
	}
	
	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	@Override
	public List<T> getSegments() {
		return segments;
	}
	
	@Override
	public void setSegments(List<T> segments) {
		this.segments = segments;
	}
	
	@Override
	public LineString getGeometry() {
		return lineString;
	}

	@Override
	public void setGeometry(LineString lineString) {
		this.lineString = lineString;
	}
	
	@Override
	public int getRuntimeInMs() {
		return runtimeInMs;
	}
	
	@Override
	public void setRuntimeInMs(int runtimeInMs) {
		this.runtimeInMs = runtimeInMs;
	}
	
	@Override
	public String getGraphName() {
		return graphName;
	}
	
	@Override
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	
	@Override
	public String getGraphVersion() {
		return graphVersion;
	}
	
	@Override
	public void setGraphVersion(String graphVersion) {
		this.graphVersion = graphVersion;
	}

}
