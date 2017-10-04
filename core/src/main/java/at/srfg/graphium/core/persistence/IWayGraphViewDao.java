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
package at.srfg.graphium.core.persistence;

import java.util.List;

import at.srfg.graphium.core.exception.GraphViewNotExistsException;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.view.IWayGraphView;

public interface IWayGraphViewDao {
	
	public List<IWayGraphView> getViewsForGraph(String graphName);

	public void saveView(IWayGraphView view);
	
	public void saveDefaultView(IWayGraph wayGraph);

	public boolean viewExists(String viewName);
	
	public IWayGraphView getView(String viewName) throws GraphViewNotExistsException;

	public int getSegmentsCount(IWayGraphView view, String graphVersion);

	public boolean isDefaultView(String viewName);
	
	public String getViewDefinition(IWayGraphView view);
}
