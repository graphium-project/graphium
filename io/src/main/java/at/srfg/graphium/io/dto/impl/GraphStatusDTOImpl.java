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

import at.srfg.graphium.io.dto.IGraphStatusDTO;

/**
 * @author mwimmer
 *
 */
public class GraphStatusDTOImpl implements IGraphStatusDTO {

	private String graph;
	private String originalGraph;
	private String versionLastImported;
	private String versionCurrentlyActive;

	public GraphStatusDTOImpl() {
	}

	public GraphStatusDTOImpl(String graph, String originalGraph,
							  String versionLastImported, String versionCurrentlyActive) {
		super();
		this.graph = graph;
		this.originalGraph = originalGraph;
		this.versionLastImported = versionLastImported;
		this.versionCurrentlyActive = versionCurrentlyActive;
	}
		
	public String getGraph() {
		return graph;
	}
	public void setGraph(String graph) {
		this.graph = graph;
	}
	public String getOriginalGraph() {
		return originalGraph;
	}
	public void setOriginalGraph(String originalGraph) {
		this.originalGraph = originalGraph;
	}
	public String getVersionLastImported() {
		return versionLastImported;
	}
	public void setVersionLastImported(String versionLastImported) {
		this.versionLastImported = versionLastImported;
	}
	public String getVersionCurrentlyActive() {
		return versionCurrentlyActive;
	}
	public void setVersionCurrentlyActive(String versionCurrentlyActive) {
		this.versionCurrentlyActive = versionCurrentlyActive;
	}

	@Override
	public String toString() {
		return "GraphStatusDTOImpl{" +
				"graph='" + graph + '\'' +
				", originalGraph='" + originalGraph + '\'' +
				", versionLastImported='" + versionLastImported + '\'' +
				", versionCurrentlyActive='" + versionCurrentlyActive + '\'' +
				'}';
	}
}
