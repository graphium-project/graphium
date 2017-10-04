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
package at.srfg.graphium.api.events.listener.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.api.events.IEvent;
import at.srfg.graphium.api.events.listener.IEventListener;
import at.srfg.graphium.api.service.ISubscribedGraphserverImportStateService;

/**
 * @author mwimmer
 *
 */
public class GraphVersionImportFinishedListenerImpl implements IEventListener {

	private static Logger log = LoggerFactory.getLogger(GraphVersionImportFinishedListenerImpl.class);

	private static final String GRAPH_VERSION_IMPORT_FINISHED = "graphVersionImportFinished";
	private static final String GRAPH_VERSION_IMPORT_FAILED = "graphVersionImportFailed";

	private ISubscribedGraphserverImportStateService subscribedGraphserverImportStateService;
	
	@Override
	public void notify(IEvent event) {
		String graphName = null;
		String version = null;
		String serverName = null;
		
		if (event.getParams() != null) {
			graphName = (String) event.getParams().get("graphName");
			version = (String) event.getParams().get("version");
			serverName = (String) event.getParams().get("serverName");
		}
		
		if (event.getName().equals(GRAPH_VERSION_IMPORT_FINISHED)) {		
			log.info("Got graph version import finished event from " + event.getSource() + 
					": graphName = " + graphName + ", version = " + version);
			subscribedGraphserverImportStateService.processFinishedImport(serverName, graphName, version);
			
		} else if (event.getName().equals(GRAPH_VERSION_IMPORT_FAILED)) {
			log.warn("Got graph version import failed event from " + event.getSource() + 
					": graphName = " + graphName + ", version = " + version);
			subscribedGraphserverImportStateService.processFailedImport(serverName, graphName, version);
			
		}
	}

	public ISubscribedGraphserverImportStateService getSubscribedGraphserverImportStateService() {
		return subscribedGraphserverImportStateService;
	}

	public void setSubscribedGraphserverImportStateService(
			ISubscribedGraphserverImportStateService subscribedGraphserverImportStateService) {
		this.subscribedGraphserverImportStateService = subscribedGraphserverImportStateService;
	}

}
