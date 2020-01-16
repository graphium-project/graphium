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
package at.srfg.graphium.routing.api.dto.impl;

import java.util.List;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IPathSegment;

public class RouteDtoImpl<T extends IWaySegment>  {

	private float length;
	private int duration;
	private List<IPathSegment> paths;
	private int runtimeInMs;
	private String graphName;
	private String graphVersion;
	private String geometry;

	public RouteDtoImpl(float length, int duration, List<IPathSegment> paths, int runtimeInMs, String graphName,
			String graphVersion, String geometry) {
		super();
		this.length = length;
		this.duration = duration;
		this.paths = paths;
		this.runtimeInMs = runtimeInMs;
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.geometry = geometry;
	}

	public List<IPathSegment> getPaths() {
		return paths;
	}

	public void setPaths(List<IPathSegment> paths) {
		this.paths = paths;
	}

	public int getRuntimeInMs() {
		return runtimeInMs;
	}

	public void setRuntimeInMs(int runtimeInMs) {
		this.runtimeInMs = runtimeInMs;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public String getGraphVersion() {
		return graphVersion;
	}

	public void setGraphVersion(String graphVersion) {
		this.graphVersion = graphVersion;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public float getLength() {
		return length;
	}
	
	public void setLength(float length) {
		this.length = length;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
