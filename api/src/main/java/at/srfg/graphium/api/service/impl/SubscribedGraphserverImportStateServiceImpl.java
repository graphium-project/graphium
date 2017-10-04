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
package at.srfg.graphium.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.api.service.ISubscribedGraphserverImportStateService;
import at.srfg.graphium.core.persistence.IGraphImportStateDao;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.IGraphImportState;
import at.srfg.graphium.model.management.impl.GraphImportState;

public class SubscribedGraphserverImportStateServiceImpl
	implements ISubscribedGraphserverImportStateService {

	private static Logger log = LoggerFactory.getLogger(SubscribedGraphserverImportStateServiceImpl.class);

	private IGraphImportStateDao dao;
	private ISubscriptionDao subscriptionDao;
	private IGraphVersionMetadataService metadataService;

	@Override
	public void processFinishedImport(String serverName, String graphName, String version) {
		// update graphImportState table
		dao.update(new GraphImportState(serverName, graphName, version, State.SYNCHRONIZED, null));
		
		
		// TODO: für jede Gruppe!
		
		
		// are there all graphs imported?
		List<IGraphImportState> graphImportStateList = dao.listGraphImportStatesForGraphVersion(graphName, version);
		boolean allImported = true;
		if (graphImportStateList != null) {
			for (IGraphImportState graphImportState : graphImportStateList) {
				if (!graphImportState.getState().equals(State.SYNCHRONIZED)) {
					allImported = false;
				}
			}
		} else {
			allImported = false;
		}
		
		if (allImported) {
			IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(graphName, version);
			if (!metadata.getState().equals(State.ACTIVE)) {
				// set graph's state to SYNCHRONIZED
				metadataService.setGraphVersionState(graphName, version, State.SYNCHRONIZED);
			}
		} else {
			// no => wait for next event
		}
	}
	
	@Override
	public void processFailedImport(String serverName, String graphName, String version) {
		// update graphImportState table
		dao.update(new GraphImportState(serverName, graphName, version, State.PUBLISH_FAILED, null));
	}

	public IGraphImportStateDao getDao() {
		return dao;
	}

	public void setDao(IGraphImportStateDao dao) {
		this.dao = dao;
	}

	public ISubscriptionDao getSubscriptionDao() {
		return subscriptionDao;
	}

	public void setSubscriptionDao(ISubscriptionDao subscriptionDao) {
		this.subscriptionDao = subscriptionDao;
	}

	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

}
