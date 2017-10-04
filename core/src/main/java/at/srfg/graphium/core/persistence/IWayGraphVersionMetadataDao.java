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

import java.util.Date;
import java.util.List;
import java.util.Set;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphMetadataFactory;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

public interface IWayGraphVersionMetadataDao extends IWayGraphMetadataFactory {

	IWayGraphVersionMetadata getWayGraphVersionMetadata(long id);
	
	IWayGraphVersionMetadata getWayGraphVersionMetadata(String graphName, String version);
	
	List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(String graphName);

	List<IWayGraphVersionMetadata> getWayGraphVersionMetadataListForOriginGraphname(String originGraphName);

	/**
	 * 
	 * @param graphName		required
	 * @param active		optional; if not set flag will be ignored for filtering
	 * @param deleted		optional; if not set flag will be ignored for filtering
	 * @param validFrom		optional
	 * @param validTo		optional
	 * @param access		optional; if not set flag will be ignored for filtering
	 * @return
	 */
	List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(String graphName, State state,
			Date validFrom, Date validTo, Set<Access> accessTypes);

	void saveGraphVersion(IWayGraphVersionMetadata graphMetadata);

	void updateGraphVersion(IWayGraphVersionMetadata graphMetadata);

	void setGraphVersionState(String graphName, String version, State state);
	
	void setValidToTimestampOfPredecessorGraphVersion(IWayGraphVersionMetadata graphMetadata);
	
	boolean checkIfGraphExists(String graphName);
	
	IWayGraph getGraph(String graphName);
	
	IWayGraph getGraph(long graphId);
	
	long saveGraph(String graphName);
	
	List<String> getGraphs();
	
	String checkNewerVersionAvailable(String viewName, String version);
	
	IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName);

	IWayGraphVersionMetadata getCurrentWayGraphVersionMetadataForView(String viewName);

	IWayGraphVersionMetadata getWayGraphVersionMetadataForView(String viewName, String version);

	IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName, Set<State> states);
	
	/**
	 *  @param graphName		required
	 *  @param version			required
	 *  @param keepMetadata		if true metadata entry will not be deleted, attribute "state" will switch to DELETED; default = true
	 */
	void deleteWayGraphVersionMetadata(String graphName, String version, boolean keepMetadata);
}
