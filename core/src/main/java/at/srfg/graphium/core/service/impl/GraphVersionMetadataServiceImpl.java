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
package at.srfg.graphium.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */
public class GraphVersionMetadataServiceImpl extends Observable implements IGraphVersionMetadataService {


	private IWayGraphVersionMetadataDao metadataDao;
	private IWayGraphViewDao viewDao;

	public IWayGraphVersionMetadata getWayGraphVersionMetadata(long id) {
		return metadataDao.getWayGraphVersionMetadata(id);
	}

	public IWayGraphVersionMetadata newWayGraphVersionMetadata() {
		return metadataDao.newWayGraphVersionMetadata();
	}

	public IWayGraphVersionMetadata getWayGraphVersionMetadata(
			String graphName, String version) {
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
		if (metadata == null) {
			metadata = metadataDao.getWayGraphVersionMetadataForView(graphName, version);
		}
		return metadata;
	}

	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(String graphName) {
		return metadataDao.getWayGraphVersionMetadataList(graphName);
	}

	public IWayGraphVersionMetadata newWayGraphVersionMetadata(long id, long graphId,
			String graphName, String version, String originGraphName,
			String originVersion, State state, Date validFrom, Date validTo,
			Polygon coveredArea, int segmentsCount, int connectionsCount,
			Set<Access> accessTypes, Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl) {
		return metadataDao.newWayGraphVersionMetadata(id, graphId, graphName, version,
				originGraphName, originVersion, state, validFrom, validTo,
				coveredArea, segmentsCount, connectionsCount, accessTypes,
				tags, source, type, description, creationTimestamp,
				storageTimestamp, creator, originUrl);
	}

	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataListForOriginGraphname(
			String originGraphName) {
		return metadataDao
				.getWayGraphVersionMetadataListForOriginGraphname(originGraphName);
	}

	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName, State state, Date validFrom, Date validTo,
			Set<Access> access) {
		return metadataDao.getWayGraphVersionMetadataList(graphName, state,
				validFrom, validTo, access);
	}

	public void saveGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		metadataDao.saveGraphVersion(graphMetadata);
	}

	public void updateGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		metadataDao.updateGraphVersion(graphMetadata);
	}

	public void setGraphVersionState(String graphName, String version,
			State state) {
		
		if (state.equals(State.ACTIVE)) {
			IWayGraphVersionMetadata metadata = getWayGraphVersionMetadata(graphName, version);  //metadataDao.getWayGraphVersionMetadata(graphName, version);
			// set valid_to attribute of graph version's predecessor
			// CAUTION: sets valid_to to ALL but deleted predecessors!
			metadataDao.setValidToTimestampOfPredecessorGraphVersion(metadata);
						
			// TODO: Überprüfen, ob mehr Logik nötig ist - z.B. wenn ein Graph zwischen zwei aktiven Graphen 
			// eingefügt und aktiviert wird
			
			metadata.setState(State.ACTIVE);
			setChanged();
			notifyObservers(metadata);
			
		}
	
		metadataDao.setGraphVersionState(graphName, version, state);
	}

	public void setValidToTimestampOfPredecessorGraphVersion(
			IWayGraphVersionMetadata graphMetadata) {
		metadataDao.setValidToTimestampOfPredecessorGraphVersion(graphMetadata);
	}

	public boolean checkIfGraphExists(String graphName) {
		return metadataDao.checkIfGraphExists(graphName);
	}

	public void saveGraph(String graphName) {
		metadataDao.saveGraph(graphName);
	}

	public List<String> getGraphs() {
		return metadataDao.getGraphs();
	}

	public List<String> getViews() {
		List<String> graphNames = getGraphs();
		List<String> viewNames = new ArrayList<>();
		if (graphNames != null && !graphNames.isEmpty()) {
			for (String graphName : graphNames) {
				List<IWayGraphView> views = viewDao.getViewsForGraph(graphName);
				for (IWayGraphView view : views) {
					viewNames.add(view.getViewName());
				}
			}
		}
		return viewNames;
	}

	public String checkNewerVersionAvailable(String graphName, String version) {
		String result = metadataDao.checkNewerVersionAvailable(graphName, version);
		if (result == null) {
			result = "false";
		}
		return result;
	}

	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(
			String graphName) {
		IWayGraphVersionMetadata metadata = metadataDao.getCurrentWayGraphVersionMetadata(graphName);
		if (metadata == null) {
			metadata = metadataDao.getCurrentWayGraphVersionMetadataForView(graphName);
		}
		return metadata;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public IWayGraphViewDao getViewDao() {
		return viewDao;
	}

	public void setViewDao(IWayGraphViewDao viewDao) {
		this.viewDao = viewDao;
	}
	
}