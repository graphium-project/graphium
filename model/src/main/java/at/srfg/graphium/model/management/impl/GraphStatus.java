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
package at.srfg.graphium.model.management.impl;

import at.srfg.graphium.model.management.IGraphStatus;

/**
 * @author mwimmer
 *
 */
public class GraphStatus implements IGraphStatus {
	
	private String graph;
	private String originalGraph;
	private String versionLastImported;
	private String versionCurrentlyActive;
	
	public GraphStatus(String graph, String originalGraph,
			String versionLastImported, String versionCurrentlyActive) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result
				+ ((originalGraph == null) ? 0 : originalGraph.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphStatus other = (GraphStatus) obj;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		if (originalGraph == null) {
			if (other.originalGraph != null)
				return false;
		} else if (!originalGraph.equals(other.originalGraph))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GraphStatus [graph=" + graph + ", originalGraph="
				+ originalGraph + ", versionLastImported="
				+ versionLastImported + ", versionCurrentlyActive="
				+ versionCurrentlyActive + "]";
	}
	
}
