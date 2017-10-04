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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.srfg.graphium.io.dto.IGraphStatusDTO;
import at.srfg.graphium.io.dto.IServerStatusDTO;
import at.srfg.graphium.io.dto.impl.GraphStatusDTOImpl;
import at.srfg.graphium.io.dto.impl.ServerStatusDTOImpl;
import at.srfg.graphium.api.service.IServerStatusService;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.IServerStatus;

/**
 * @author mwimmer
 *
 */
public class ServerStatusServiceImpl implements IServerStatusService {

	private IWayGraphVersionMetadataDao metadataDao;
	private IServerStatus serverStatus;
	
	@Override
	public IServerStatusDTO getStatus() {
		
		IServerStatusDTO serverStatusDto = new ServerStatusDTOImpl(serverStatus.getServerName(), 
																   new ArrayList<IGraphStatusDTO>(), 
																   serverStatus.getRunningImports());
		
		List<String> graphs = metadataDao.getGraphs();
		if (graphs != null) {
			for (String graph : graphs) {
				Date lastImported = null;
				String versionLastImported = null;
				Date currentlyActiveDate = null;
				String versionCurrentlyActive = null;
				
				List<IWayGraphVersionMetadata> metadataList = metadataDao.getWayGraphVersionMetadataList(graph);
				
				if (metadataList != null && !metadataList.isEmpty()) {
					
					for (IWayGraphVersionMetadata metadata : metadataList) {
						if (lastImported == null || metadata.getStorageTimestamp().after(lastImported)) {
							lastImported = metadata.getStorageTimestamp();
							versionLastImported = metadata.getVersion();
						}
						
						if (metadata.getState().equals(State.ACTIVE)) {
							if (currentlyActiveDate == null || metadata.getStorageTimestamp().after(currentlyActiveDate)) {
								currentlyActiveDate = metadata.getStorageTimestamp();
								versionCurrentlyActive = metadata.getVersion();
							}
						}
					}
				
					IGraphStatusDTO graphStatusDto = new GraphStatusDTOImpl(graph, metadataList.get(0).getOriginGraphName(), 
																		 	versionLastImported, versionCurrentlyActive);
					serverStatusDto.getGraphStatuses().add(graphStatusDto);
				}
			}
		}
		
		return serverStatusDto;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public IServerStatus getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(IServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}

}