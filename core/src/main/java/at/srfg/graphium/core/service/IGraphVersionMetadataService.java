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
package at.srfg.graphium.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public interface IGraphVersionMetadataService {

	public abstract IWayGraphVersionMetadata getWayGraphVersionMetadata(long id);

	public abstract IWayGraphVersionMetadata newWayGraphVersionMetadata();

	public abstract IWayGraphVersionMetadata getWayGraphVersionMetadata(
			String graphName, String version);

	public abstract List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName);

	public abstract IWayGraphVersionMetadata newWayGraphVersionMetadata(
			long id, long graphId, String graphName, String version, String originGraphName,
			String originVersion, State state, Date validFrom, Date validTo,
			Polygon coveredArea, int segmentsCount, int connectionsCount,
			Set<Access> accessTypes, Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl);

	public abstract List<IWayGraphVersionMetadata> getWayGraphVersionMetadataListForOriginGraphname(
			String originGraphName);

	public abstract List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName, State state, Date validFrom, Date validTo,
			Set<Access> access);

	public abstract void saveGraphVersion(IWayGraphVersionMetadata graphMetadata);

	public abstract void updateGraphVersion(
			IWayGraphVersionMetadata graphMetadata);

	public abstract void setGraphVersionState(String graphName, String version,
			State state);

	public abstract void setValidToTimestampOfPredecessorGraphVersion(
			IWayGraphVersionMetadata graphMetadata);

	public abstract boolean checkIfGraphExists(String graphName);

	public abstract void saveGraph(String graphName);

	public abstract List<String> getGraphs();
	
	public abstract List<String> getViews();

	public abstract String checkNewerVersionAvailable(String graphName,
			String version);

	public abstract IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(
			String graphName);

}