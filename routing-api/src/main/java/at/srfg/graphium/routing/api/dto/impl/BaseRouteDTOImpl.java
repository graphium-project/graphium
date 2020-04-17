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
package at.srfg.graphium.routing.api.dto.impl;

import at.srfg.graphium.routing.api.dto.IRouteDTO;

public class BaseRouteDTOImpl<W extends Object> implements IRouteDTO<W> {

	private W weight;
	private float length;
	private int duration;
	private int runtimeInMs;
	private String graphName;
	private String graphVersion;
	// TODO: change to Polyline
	private String geometry;

	public BaseRouteDTOImpl(W weight, float length, int duration, int runtimeInMs, String graphName,
			String graphVersion, String geometry) {
		this.weight = weight;
		this.length = length;
		this.duration = duration;
		this.runtimeInMs = runtimeInMs;
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.geometry = geometry;
	}
	
	public W getWeight() {
		return weight;
	}

	public void setWeight(W weight) {
		this.weight = weight;
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

}
