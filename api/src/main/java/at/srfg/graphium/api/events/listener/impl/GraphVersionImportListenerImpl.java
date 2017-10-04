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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.api.events.IEvent;
import at.srfg.graphium.api.events.listener.IEventListener;
import at.srfg.graphium.api.service.IDownloadGraphService;
import at.srfg.graphium.api.service.impl.GraphVersionsImportInfo;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.ISubscription;

/**
 * Listener reacts on import event from central graph server.
 * 
 * @author mwimmer
 *
 */
public class GraphVersionImportListenerImpl implements IEventListener {

	private static Logger log = LoggerFactory.getLogger(GraphVersionImportListenerImpl.class);
	
	private IGraphVersionMetadataService metadataService;
	private ISubscriptionDao subscriptionDao;
	private IDownloadGraphService downloadGraphService;
	
	@Override
	@Transactional
	public void notify(IEvent event) {
		String serverName = null;
		String originalGraphName = null;
		String originalVersion = null;
		String getGraphVersionUrl = null;
		
		if (event.getParams() != null) {
			serverName = (String) event.getParams().get("serverName");
			originalGraphName = (String) event.getParams().get("graphName");
			originalVersion = (String) event.getParams().get("version");
			getGraphVersionUrl = (String) event.getParams().get("getGraphVersionUrl");
		}
		
		log.info("Got event from " + event.getSource() + ": graphName = " + originalGraphName + ", version = " + originalVersion);
		
		// check if graph version already imported
		IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(originalGraphName, originalVersion); 
		
		// if graph version not imported => download from central graph server
		boolean alreadyImported = false;
		if (metadata != null) {
			if (metadata.getVersion().equals(originalVersion) && !metadata.getState().equals(State.DELETED)) {
				alreadyImported = true;
			}
		}
		
		String centralServerUrl = null;
		if (serverName == null) {
			List<ISubscription> subscriptions = subscriptionDao.getSubscriptionsForGraph(originalGraphName);
			if (subscriptions == null || subscriptions.isEmpty()) {
				log.error("No subscription found for graphName '" + originalGraphName + "'");
			} else {
				centralServerUrl = subscriptions.get(0).getUrl();
			}
			if (subscriptions.size() > 1) {
				log.warn("More than one subscriptions found for graphName '" + originalGraphName + "' - "
						+ " taking URL " + centralServerUrl);
			}
		} else {
			List<ISubscription> subscriptions = subscriptionDao.getSubscriptionsForGraphAndServer(serverName, originalGraphName);
			if (subscriptions != null && !subscriptions.isEmpty()) {
				centralServerUrl = subscriptions.get(0).getUrl();
			}
		}
		
		if (centralServerUrl == null) {
			throw new RuntimeException("Subscription for graphName " + originalGraphName + " not found!");
		}

		if (!alreadyImported) {
			String importUrl = centralServerUrl + (!centralServerUrl.endsWith("/") && !getGraphVersionUrl.startsWith("/") ? "/" : "") + getGraphVersionUrl;
			List<GraphVersionsImportInfo> importInfoList = new ArrayList<>();
			try {
				importInfoList.add(new GraphVersionsImportInfo(originalGraphName, originalVersion, new URL(importUrl), centralServerUrl));
			} catch (MalformedURLException e) {
				log.error("Could not prepare graph "+ originalGraphName + " in version " + originalVersion + " for update", e);
			}
			downloadGraphService.downloadGraphVersion(importInfoList);
		} else {
			log.info("GraphName = " + originalGraphName + " in version = " + originalVersion + " already imported");
		}
		
	}

	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public ISubscriptionDao getSubscriptionDao() {
		return subscriptionDao;
	}

	public void setSubscriptionDao(ISubscriptionDao subscriptionDao) {
		this.subscriptionDao = subscriptionDao;
	}

	public IDownloadGraphService getDownloadGraphService() {
		return downloadGraphService;
	}

	public void setDownloadGraphService(IDownloadGraphService downloadGraphService) {
		this.downloadGraphService = downloadGraphService;
	}

}