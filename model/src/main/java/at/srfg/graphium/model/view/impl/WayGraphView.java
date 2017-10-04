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
package at.srfg.graphium.model.view.impl;

import java.util.Map;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.view.IWayGraphView;

public class WayGraphView implements IWayGraphView {

	private String viewName;
	private IWayGraph graph;
	private String dbViewName;
	private boolean waySegmentsIncluded;
	private String filter;
	private Polygon coveredArea;
	private int segmentsCount;
	private int connectionsCount;
	private Map<String, String> tags;
	
	public WayGraphView() {}
	
	public WayGraphView(String viewName, IWayGraph graph, String dbViewName, boolean waySegmentsIncluded, Polygon coveredArea, 
			int segmentsCount, int connectionsCount, Map<String, String> tags) {
		super();
		this.viewName = viewName;
		this.graph = graph;
		this.dbViewName = dbViewName;
		this.waySegmentsIncluded = waySegmentsIncluded;
		this.coveredArea = coveredArea;
		this.segmentsCount = segmentsCount;
		this.connectionsCount = connectionsCount;
		this.tags = tags;
	}
	
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public IWayGraph getGraph() {
		return graph;
	}
	public void setGraph(IWayGraph graph) {
		this.graph = graph;
	}
	public String getFilter() {
		return filter;
	}
	public String getDbViewName() {
		return dbViewName;
	}
	public void setDbViewName(String dbViewName) {
		this.dbViewName = dbViewName;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public boolean isWaySegmentsIncluded() {
		return waySegmentsIncluded;
	}
	public void setWaySegmentsIncluded(boolean waySegmentsIncluded) {
		this.waySegmentsIncluded = waySegmentsIncluded;
	}
	public Polygon getCoveredArea() {
		return coveredArea;
	}
	public void setCoveredArea(Polygon coveredArea) {
		this.coveredArea = coveredArea;
	}
	public int getSegmentsCount() {
		return segmentsCount;
	}
	public void setSegmentsCount(int segmentsCount) {
		this.segmentsCount = segmentsCount;
	}
	public int getConnectionsCount() {
		return connectionsCount;
	}
	public void setConnectionsCount(int connectionsCount) {
		this.connectionsCount = connectionsCount;
	}
	public Map<String, String> getTags() {
		return tags;
	}
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "WayGraphView [viewName=" + viewName + ", graph=" + graph.toString() + ", dbViewName=" + dbViewName + ", coveredArea="
				+ coveredArea + ", waySegmentsIncluded=" + waySegmentsIncluded + ", segmentsCount=" + segmentsCount + ", connectionsCount=" 
				+ connectionsCount + ", tags=" + tags + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (graph.getId() ^ (graph.getId() >>> 32));
		result = prime * result + ((viewName == null) ? 0 : viewName.hashCode());
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
		WayGraphView other = (WayGraphView) obj;
		if (!graph.equals(other.graph))
			return false;
		if (viewName == null) {
			if (other.viewName != null)
				return false;
		} else if (!viewName.equals(other.viewName))
			return false;
		return true;
	}

}