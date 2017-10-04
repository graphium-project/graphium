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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import at.srfg.graphium.api.service.IDownloadGraphService;
import at.srfg.graphium.api.service.IVerifyAndUpdateGraphVersionService;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.ISubscription;

/**
 * @author mwimmer
 */
public class VerifyAndUpdateGraphVersionServiceImpl 
		implements IVerifyAndUpdateGraphVersionService {

	private static Logger log = LoggerFactory.getLogger(VerifyAndUpdateGraphVersionServiceImpl.class);
	
	private static final String GRAPH_CHECK_UPDATE = "metadata/graphs/{graph}/checkupdate?lastImportedVersion={version}";
	private RestTemplate restTemplate;
	private IWayGraphVersionMetadataDao metadataDao;
	private IGraphVersionMetadataService metadataService;
	private ISubscriptionDao subscriptionDao;
	private IDownloadGraphService downloadGraphService;
	private String getGraphVersionUrl;
	private List<GraphVersionsImportInfo> importInfoList;
	
	@PostConstruct
	public void setup() {
		verifyAndUpdateGraphVersion(true);
	}
	
	@Override
	public List<String> verifyAndUpdateGraphVersion(boolean updateAutomatically) {
		List<String> outdatedGraphNames = new ArrayList<String>();
		
		importInfoList = new ArrayList<>();
		
		// read all subscriptions on external central graph server
		List<ISubscription> subscriptions = subscriptionDao.getAllSubscriptions();
		if (subscriptions != null && !subscriptions.isEmpty()) {
			for (ISubscription subscription : subscriptions) {
				// get current graph's version metadata
				Set<State> states = new HashSet<>();
				states.add(State.ACTIVATING);
				states.add(State.ACTIVE);
				states.add(State.INITIAL);
				states.add(State.SYNCHRONIZED);
				IWayGraphVersionMetadata metadata = metadataDao.getCurrentWayGraphVersionMetadata(subscription.getViewName(), states);
				
				String updateVersion = null;
				boolean update = false;
				if (metadata == null) {
//					// no version stored locally => update
//					update = true;
					metadata = new WayGraphVersionMetadata();
					metadata.setGraphName(subscription.getViewName());
					metadata.setVersion(null);
					metadata.setState(State.INITIAL);
				}

				// check if graph version has to be updated
				updateVersion = checkUpdate(metadata, subscription.getUrl());
				if (updateVersion != null) {
					IWayGraphVersionMetadata specificMetadata = metadataDao.getWayGraphVersionMetadata(subscription.getViewName(), updateVersion);
					if (specificMetadata == null) {
						update = true;
					}
				}
				
				if (update) {
					outdatedGraphNames.add(subscription.getViewName());
					log.info("New version " + updateVersion + " for graph " + subscription.getViewName() + " available!");
					
					if (updateAutomatically) {
						// prepare update graph's version
						log.info("Updating graph " + subscription.getViewName() + " asynchronously");
						try {
							prepareGraphVersionsToUpdate(subscription, updateVersion);
						} catch (MalformedURLException e) {
							log.error("Could not prepare graph "+ subscription.getViewName() + " in version " + updateVersion +
									  " for update", e);
						}
					}
				} else {
					log.info("Version for graph " + subscription.getViewName() + " is up-to-date");
					if (metadata.getState().equals(State.ACTIVATING)) {
						// graph's version has to be updated to ACTIVE
						// notify listener before (=> metadataService)
						log.info("...but state is ACTIVATING - updating to ACTIVE...");
						metadataService.setGraphVersionState(metadata.getGraphName(), metadata.getVersion(), State.ACTIVE);
					}
				}
			}
			
			// update graph versions asynchronously
			updateGraphVersions();
			
			if (outdatedGraphNames.isEmpty()) {
				outdatedGraphNames = null;
			}
			return outdatedGraphNames;
		} else {		
			return null;
		}
	}
	
	private void updateGraphVersions() {
		if (importInfoList != null && !importInfoList.isEmpty()) {
			downloadGraphService.downloadGraphVersion(importInfoList);
		}
	}

	private void prepareGraphVersionsToUpdate(ISubscription subscription, String updateVersion) throws MalformedURLException {
		String importUrl = subscription.getUrl() + (!subscription.getUrl().endsWith("/") && !getGraphVersionUrl.startsWith("/") ? "/" : "") + getGraphVersionUrl;
		importInfoList.add(new GraphVersionsImportInfo(subscription.getViewName(), updateVersion, new URL(importUrl), subscription.getUrl()));
	}

	private String checkUpdate(IWayGraphVersionMetadata metadata, String externalGraphserverApiUrl) throws RestClientException {
		log.info("Check for update of graph " + metadata.getGraphName());
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
 		requestParams.put("version", (metadata.getVersion() == null ? "null" : metadata.getVersion()));

// TODO: Set Credentials
//		HttpHeaders headers = getHeaders(externalGraphserverApiUrl); //, subscription.getUser(), subscription.getPassword());
//		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String checkUpdateResult = null;
		try {
			checkUpdateResult = restTemplate.getForObject(externalGraphserverApiUrl + (externalGraphserverApiUrl.endsWith("/") ? "" : "/") + 
													GRAPH_CHECK_UPDATE.replace("{graph}", metadata.getGraphName()), String.class, requestParams);
		} catch (RestClientException e) {
			log.error("Error while checking if update of graph is required", e);
		}
		if (checkUpdateResult != null) {
			String[] resultTokens = checkUpdateResult.replace("\"", "").replace("{", "").replace("}", "").split(":");
			if (resultTokens.length == 2 && !resultTokens[1].equals("null") && !resultTokens[1].equals("false")) {
				return resultTokens[1];
			}
			return null;
		} else {
			return null;
		}
		
	}

//	private HttpHeaders getHeaders(String url, String user, String password) {
//		HttpHeaders headers = new HttpHeaders();
//		String base64Auth = getBase64Authentication(url, user, password);
//		if(base64Auth != null) {
//			headers.add("Authorization", "Basic " + base64Auth);
//		}
//		return headers;		
//	}
//	
//	private HttpHeaders getHeaders(String url) {
//		HttpHeaders headers = new HttpHeaders();
//		String base64Auth = getBase64Authentication(url);
//		if(base64Auth != null) {
//			headers.add("Authorization", "Basic " + base64Auth);
//		}
//		return headers;		
//	}
//
//	// TODO: Brauchen wir das hier oder können wir die Implementierung aus AbstractServerUrlAndAuthenticationAwareHttpNotifier verwenden?
//	public String getBase64Authentication(String url, String user, String password) {
//		String base64Creds = null;
//		if (user != null) {
//			String plainCreds = user + ":" + (password == null ? "" : password);
//			byte[] plainCredsBytes = plainCreds.getBytes();
//			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
//			base64Creds = new String(base64CredsBytes);			
//		}
//		return base64Creds;
//	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
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

	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public String getGetGraphVersionUrl() {
		return getGraphVersionUrl;
	}

	public void setGetGraphVersionUrl(String getGraphVersionUrl) {
		this.getGraphVersionUrl = getGraphVersionUrl;
	}
	
}