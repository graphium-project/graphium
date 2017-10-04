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
package at.srfg.graphium.io.dto.impl;

import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.io.dto.IGraphDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;

/**
 * @author mwimmer
 *
 */
public class GraphDTOImpl implements IGraphDTO {

	private String graph;
	private String version;
	private List<IWaySegmentDTO> segments;

	public GraphDTOImpl(String graph, String version,
			List<IWaySegmentDTO> segments) {
		super();
		this.graph = graph;
		this.version = version;
		this.segments = segments;
	}
	
	public GraphDTOImpl(String graph, String version) {
		super();
		this.graph = graph;
		this.version = version;
		this.segments = new ArrayList<IWaySegmentDTO>();
	}

	public String getGraph() {
		return graph;
	}
	public void setGraph(String graph) {
		this.graph = graph;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<IWaySegmentDTO> getSegments() {
		return segments;
	}
	public void setSegments(List<IWaySegmentDTO> segments) {
		this.segments = segments;
	}
	
}