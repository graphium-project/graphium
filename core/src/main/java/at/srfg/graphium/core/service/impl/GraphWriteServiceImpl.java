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

import java.util.Observable;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.core.service.IGraphWriteService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public class GraphWriteServiceImpl extends Observable implements IGraphWriteService<IWaySegment> {

	private IWayGraphWriteDao<IWaySegment> graphWriteDao;
	private IWayGraphVersionMetadataDao metadataDao;
	
	@Override
	public void deleteGraphVersion(String graphName, String version, boolean keepMetadata) throws GraphNotExistsException {
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
		
		graphWriteDao.deleteSegments(graphName, version);
		if (keepMetadata) {
			metadataDao.setGraphVersionState(graphName, version, State.DELETED);
		} else {
			metadataDao.deleteWayGraphVersionMetadata(graphName, version, keepMetadata);
		}
		
		metadata.setState(State.DELETED);
		setChanged();
		notifyObservers(metadata);
	}

	public IWayGraphWriteDao<IWaySegment> getGraphWriteDao() {
		return graphWriteDao;
	}

	public void setGraphWriteDao(IWayGraphWriteDao<IWaySegment> graphWriteDao) {
		this.graphWriteDao = graphWriteDao;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

}
