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
package at.srfg.graphium.api.events.notifier.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import at.srfg.graphium.api.events.listener.IEventListener;
import at.srfg.graphium.api.events.notifier.IGraphVersionImportNotifier;
import at.srfg.graphium.core.persistence.IGraphImportStateDao;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.impl.GraphImportState;

/**
 * Notifies registered server of new graph version to import
 * @author mwimmer
 *
 */
public class GraphVersionImportHttpNotifierImpl 
		extends	AbstractServerUrlAndAuthenticationAwareHttpNotifier 
		implements IGraphVersionImportNotifier {


    private static Logger log = LoggerFactory
            .getLogger(GraphVersionImportFinishedHttpNotifierImpl.class);
    
	private static final String GRAPH_VERSION_IMPORT = "graphVersionImport";
	private RestTemplate restTemplate;
	private ISubscriptionDao subscriptionDao;
	private IGraphImportStateDao graphImportStateDao;
	private String getGraphVersionUrl;
	private String setGraphVersionStateUrl;
	private String notificationUrl;
	private String serverName;
	
	@Override
	public boolean notifyRegisteredServersOfPublishing(String graphName, String version, String groupName) {
		boolean ok = true;
		
		List<ISubscription> subscriptions;
		
		if (groupName == null) {
			subscriptions = subscriptionDao.getSubscriptionsForGraph(graphName);
		} else {
			subscriptions = subscriptionDao.getSubscriptionsForGraph(graphName, groupName);
		}
		
		if (subscriptions != null && !subscriptions.isEmpty()) {
			for (ISubscription subscription : subscriptions) {
				// persist GraphImportState entry
				graphImportStateDao.update(new GraphImportState(
						subscription.getServerName(),
						subscription.getViewName(),
						version,
						State.PUBLISH,
						null));
				// notify servers
				try {
					notify(subscription, version);
				} catch (RestClientException e) {
					log.error("Notification of server " + subscription.getServerName() + " failed!", e);
					ok = false;
					graphImportStateDao.update(new GraphImportState(
							subscription.getServerName(),
							subscription.getViewName(),
							version,
							State.PUBLISH_FAILED,
							null));
				}
			}
		}
		
		return ok;
	}

	@Override
	public boolean notifyRegisteredServersOfActivating(String graphName, String version, String groupName, Integer segmentsCount) {
		boolean ok = true;
		
		// activate graph version on all registered graph servers
		try {
			List<ISubscription> subscriptions = subscriptionDao.getSubscriptionsForGraph(graphName, groupName);
			if (subscriptions != null && !subscriptions.isEmpty()) {
				for (ISubscription subscription : subscriptions) {
					log.info("Updating graph version " + graphName + "_" + version + " on Server " + subscription.getServerName() + " at " + subscription.getUrl()
							+ " to state " + State.ACTIVATING.toString());
					updateGraphVersionState(subscription.getUrl(), subscription.getViewName(), version, State.ACTIVATING, segmentsCount);
				}
			}
		} catch (RestClientException e) {
			log.error("Activating graph version failed!", e);
			ok = false;
		}
		
		return ok;
	}

	@Override
	public boolean notifyRegisteredServersOfSuccessfullyActivating(String graphName, String version, String groupName, Integer segmentsCount) {
		boolean ok = true;
		
		// activate graph version on all registered graph servers
		try {
			List<ISubscription> subscriptions = subscriptionDao.getSubscriptionsForGraph(graphName, groupName);
			if (subscriptions != null && !subscriptions.isEmpty()) {
				for (ISubscription subscription : subscriptions) {
					log.info("Updating graph version " + graphName + "_" + version + " on Server " + subscription.getServerName() + " at " + subscription.getUrl()
							+ " to state " + State.ACTIVE.toString());
					updateGraphVersionState(subscription.getUrl(), subscription.getViewName(), version, State.ACTIVE, segmentsCount);
				}
			}
		} catch (RestClientException e) {
			log.warn("Informing about succussfully activation graph version failed!", e);
			ok = false;
		}
		
		return ok;
	}

	@Override
	public boolean notifyRegisteredServersOfFailedActivating(String graphName, String version, String groupName, Integer segmentsCount) {
		boolean ok = true;
		
		List<ISubscription> subscriptions = subscriptionDao.getSubscriptionsForGraph(graphName, groupName);
		if (subscriptions != null && !subscriptions.isEmpty()) {
			for (ISubscription subscription : subscriptions) {
				log.info("Updating graph version " + graphName + "_" + version + " on Server " + subscription.getServerName() + " at " + subscription.getUrl()
						+ " to state " + State.ACTIVE.toString());
				// set graph version's state on all registered graph servers back to INITIAL
				try {
					updateGraphVersionState(subscription.getUrl(), subscription.getViewName(), version, State.INITIAL, segmentsCount);
				} catch (RestClientException e) {
					log.error("Updating graph version failed!", e);
					ok = false;
				}
			}
		}
		return ok;
	}

	private String createSetGraphVersionStateUrl(String satelliteUrl, String graphName, String version, String state, Integer segmentsCount) {
		// /graphs/{graph}/version/{version}/state/{state}
		String url = satelliteUrl 
					+ (!satelliteUrl.endsWith("/") && !setGraphVersionStateUrl.startsWith("/") ? "/" : "")
					+ setGraphVersionStateUrl;
		url = url.replace("{graph}", graphName)
				  .replace("{version}", version)
				  .replace("{state}", state);
		url = url.replace("{segmentscount}", (segmentsCount == null ? "0" : Integer.toString(segmentsCount)));
		return url;
	}

	private void updateGraphVersionState(String url, String graphName, String version, State state, Integer segmentsCount) throws RestClientException {
		Map<String, Object> requestParams = new HashMap<String, Object>();

		String graphVersionStateUpdateUrl = createSetGraphVersionStateUrl(url, graphName, version, state.toString(), segmentsCount);
// TODO: Set Credentials
		HttpHeaders headers = getHeaders(graphVersionStateUpdateUrl); //, subscription.getUser(), subscription.getPassword());
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(requestParams, 
				headers);
		restTemplate.put(graphVersionStateUpdateUrl, request);	
	}
	
	private void notify(ISubscription subscription, String version) throws RestClientException {
		log.info("Notifing Server " + subscription.getServerName() + " at " + subscription.getUrl());
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put(IEventListener.EVENTSOURCEPARAMNAME, getSourceString());
		requestParams.put("serverName", serverName);
		requestParams.put("graphName", subscription.getViewName());
		requestParams.put("version", version);
		requestParams.put("getGraphVersionUrl", getGraphVersionUrl + (getGraphVersionUrl.endsWith("/") ? "" : "/")); 
//												"/graphs/{graph}/version/{version}/segments");

		String serverNotificationUrl = createServerNotificationUrl(subscription.getUrl());
// TODO: Set Credentials
		HttpHeaders headers = getHeaders(serverNotificationUrl); //, subscription.getUser(), subscription.getPassword());
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(requestParams, 
				headers);
		restTemplate.put(serverNotificationUrl + GRAPH_VERSION_IMPORT, request);	
		
	}

	/**
	 * @param url
	 * @return
	 */
	private String createServerNotificationUrl(String serverUrl) {
		return serverUrl + 
			   (serverUrl.endsWith("/") ? "" : "/") + 
			   notificationUrl + 
			   (notificationUrl.endsWith("/") ? "" : "/");
	}

	private HttpHeaders getHeaders(String url, String user, String password) {
		HttpHeaders headers = new HttpHeaders();
		String base64Auth = getBase64Authentication(url, user, password);
		if(base64Auth != null) {
			headers.add("Authorization", "Basic " + base64Auth);
		}
		return headers;		
	}
	
	private HttpHeaders getHeaders(String url) {
		HttpHeaders headers = new HttpHeaders();
		String base64Auth = getBase64Authentication(url);
		if(base64Auth != null) {
			headers.add("Authorization", "Basic " + base64Auth);
		}
		return headers;		
	}

	// TODO: Brauchen wir das hier oder können wir die Implementierung aus AbstractServerUrlAndAuthenticationAwareHttpNotifier verwenden?
	public String getBase64Authentication(String url, String user, String password) {
		String base64Creds = null;
		if (user != null) {
			String plainCreds = user + ":" + (password == null ? "" : password);
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			base64Creds = new String(base64CredsBytes);			
		}
		return base64Creds;
	}
	
	public ISubscriptionDao getSubscriptionDao() {
		return subscriptionDao;
	}

	public void setSubscriptionDao(ISubscriptionDao subscriptionDao) {
		this.subscriptionDao = subscriptionDao;
	}

	public String getGetGraphVersionUrl() {
		return getGraphVersionUrl;
	}

	public void setGetGraphVersionUrl(String getGraphVersionUrl) {
		this.getGraphVersionUrl = getGraphVersionUrl;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public IGraphImportStateDao getGraphImportStateDao() {
		return graphImportStateDao;
	}

	public void setGraphImportStateDao(IGraphImportStateDao graphImportStateDao) {
		this.graphImportStateDao = graphImportStateDao;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getSetGraphVersionStateUrl() {
		return setGraphVersionStateUrl;
	}

	public void setSetGraphVersionStateUrl(String setGraphVersionStateUrl) {
		this.setGraphVersionStateUrl = setGraphVersionStateUrl;
	}

}