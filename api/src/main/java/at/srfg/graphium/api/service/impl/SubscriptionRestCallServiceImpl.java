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

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import at.srfg.graphium.api.service.ISubscriptionCallService;

/**
 * Subscription on external server using REST Template
 * 
 * @author mwimmer
 *
 */
public class SubscriptionRestCallServiceImpl 
//	extends	AbstractServerUrlAndAuthenticationAwareHttpNotifier
	implements ISubscriptionCallService {

	private static final String SUBSCRIPTION = "graphs/{graph}/subscriptions?servername={servername}&groupname={groupname}&url={url}&user={user}&password={password}";
	private static final String UNSUBSCRIPTION = "graphs/{graph}/subscriptions?servername={servername}";
	private RestTemplate restTemplate;
	
	@Override
	public void subscribe(String localServerName, String graphName, String groupName, String externalUrl, String localUrl, String localUser, String localPassword)
			throws RestClientException {
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("servername", localServerName);
		requestParams.put("graph", graphName);
		requestParams.put("groupname", groupName);
		requestParams.put("url", localUrl);
		requestParams.put("user", localUser);
		requestParams.put("password", localPassword);

		String subscriptionUrl = createSubscriptionUrl(externalUrl);
		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(new HashMap<String, Object>());
//		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(requestParams, 
//				getHeaders(subscriptionUrl, user, password));
		restTemplate.postForLocation(subscriptionUrl + SUBSCRIPTION, request, requestParams);	
	}

	@Override
	public void unsubscribe(String localServerName, String graphName, String externalUrl) throws RestClientException {
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("servername", localServerName);
		requestParams.put("graph", graphName);

		String subscriptionUrl = createSubscriptionUrl(externalUrl);
//		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String,Object>>(requestParams, 
//				getHeaders(subscriptionUrl, user, password));
		restTemplate.delete(subscriptionUrl + UNSUBSCRIPTION, requestParams);	
	}

	private String createSubscriptionUrl(String serverUrl) {
		String subscriptionUrl = serverUrl + (serverUrl.endsWith("/") ? "" : "/");
		if (!subscriptionUrl.endsWith("api/")) {
			subscriptionUrl += "api/";
		}
		return subscriptionUrl;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
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

}