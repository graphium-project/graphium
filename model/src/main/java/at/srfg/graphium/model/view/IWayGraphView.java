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
package at.srfg.graphium.model.view;

import java.util.Map;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.IWayGraph;

public interface IWayGraphView {

	public String getViewName();
	
	public void setViewName(String viewName);
	
	public IWayGraph getGraph();
	
	public void setGraph(IWayGraph graph);
	
	public String getDbViewName();
	
	public void setDbViewName(String dbViewName);
	
	public boolean isWaySegmentsIncluded();
	
	public void setWaySegmentsIncluded(boolean waySegmentsIncluded);
	
	public Polygon getCoveredArea();
	
	public void setCoveredArea(Polygon coveredArea);
	
	public int getSegmentsCount();

	public void setSegmentsCount(int segmentsCount);

	public int getConnectionsCount();

	public void setConnectionsCount(int connectionsCount);
	
	public abstract Map<String, String> getTags();

	public abstract void setTags(Map<String, String> tags);
	
}