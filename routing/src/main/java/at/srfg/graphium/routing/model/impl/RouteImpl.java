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

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IPathSegment;
import at.srfg.graphium.routing.model.IRoute;

public class RouteImpl<T extends IWaySegment> implements IRoute<T>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty(value="Path")
	private List<IPathSegment> path;
	@JsonProperty(value="Length")
	private float length;
	@JsonProperty(value="Duration")
	private int duration;
	@JsonProperty(value="Error")
	private String error;
	@JsonProperty(value="Segments")
	private List<T> segments;
//	private List<IActivity> activities;
	private int runtimeInMs  = -1;
	@JsonProperty(value="GraphName")
	private String graphName;
	@JsonProperty(value="GraphVersion")
	private String graphVersion;

	public RouteImpl() {}
	public RouteImpl(List<IPathSegment> path, float length, int duration, String error) {
		this.path = path;
		this.length = length;
		this.duration = duration;
		this.error = error;
	}
	
	@Override
	public List<IPathSegment> getPath() {
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
	public void setPath(List<IPathSegment> path) {
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
	
	@JsonIgnore
	@Override
	public List<T> getSegments() {
		return segments;
	}
	
	@JsonIgnore
	@Override
	public void setSegments(List<T> segments) {
		this.segments = segments;
	}
	
	@JsonIgnore
	@Override
	public int getRuntimeInMs() {
		return runtimeInMs;
	}
	
	@JsonIgnore
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
	
	public void setError(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	

}
