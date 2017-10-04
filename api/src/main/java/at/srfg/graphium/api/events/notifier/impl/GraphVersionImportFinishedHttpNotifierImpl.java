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
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import at.srfg.graphium.api.events.listener.IEventListener;
import at.srfg.graphium.api.events.notifier.IGraphVersionImportFinishedNotifier;

/**
 * Notifies central graph server of finished graph import into satellite server
 * @author mwimmer
 *
 */
public class GraphVersionImportFinishedHttpNotifierImpl
	extends AbstractServerUrlAndAuthenticationAwareHttpNotifier
	implements IGraphVersionImportFinishedNotifier {

    private static Logger log = LoggerFactory
            .getLogger(GraphVersionImportFinishedHttpNotifierImpl.class);
    
	private static final String GRAPH_VERSION_IMPORT_FINISHED = "graphVersionImportFinished";
	private static final String GRAPH_VERSION_IMPORT_FAILED = "graphVersionImportFailed";

	private RestTemplate restTemplate;

	private String notificationUrl;
	private String user;
	private String password;
	private String serverName;

	@Override
	public void notifyCentralServers(String url, String graphName, String version, boolean failed) {
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put(IEventListener.EVENTSOURCEPARAMNAME, getSourceString());
		requestParams.put("graphName", graphName);
		requestParams.put("version", version);
		requestParams.put("serverName", serverName);

		String importFinishedNotificationUrl;
		if (failed) {
			importFinishedNotificationUrl = createImportFailedNotificationUrl(url, graphName);
		} else {
			importFinishedNotificationUrl = createImportFinishedNotificationUrl(url, graphName);
		}
		log.info("Notifing Central Server at " + importFinishedNotificationUrl);
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(requestParams, 
				getHeaders(importFinishedNotificationUrl, user, password));
		restTemplate.put(importFinishedNotificationUrl, request);	
	}

	private String createImportFinishedNotificationUrl(String url, String originalGraphName) {
		return url + (!url.endsWith("/") && !notificationUrl.startsWith("/") ? "/" : "") + notificationUrl + GRAPH_VERSION_IMPORT_FINISHED;
	}

	private String createImportFailedNotificationUrl(String url, String originalGraphName) {
		return url + (!url.endsWith("/") && !notificationUrl.startsWith("/") ? "/" : "") + notificationUrl + GRAPH_VERSION_IMPORT_FAILED;
	}
	
	private HttpHeaders getHeaders(String url, String user, String password) {
		HttpHeaders headers = new HttpHeaders();
		String base64Auth = getBase64Authentication(url, user, password);
		if(base64Auth != null) {
			headers.add("Authorization", "Basic " + base64Auth);
		}
		return headers;		
	}
	
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

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
		if (!this.notificationUrl.endsWith("/")) {
			this.notificationUrl += "/";
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
