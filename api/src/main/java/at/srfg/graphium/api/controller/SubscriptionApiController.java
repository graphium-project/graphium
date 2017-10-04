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
package at.srfg.graphium.api.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.service.ISubscriptionAPIService;
import at.srfg.graphium.api.service.ISubscriptionCallService;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.io.dto.ISubscriptionContainerDTO;
import at.srfg.graphium.io.dto.ISubscriptionDTO;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.impl.Subscription;

/**
 * @author mwimmer
 *
 */
@RestController
public class SubscriptionApiController {
	
	private static Logger log = LoggerFactory.getLogger(SubscriptionApiController.class);

	private ISubscriptionAPIService subscriptionAPIService;

	/**
	 *  will be set in case of satellite graph server
	 */
	private ISubscriptionCallService subscriptionCallService = null;
	
	private String localServerName = null;
	private String localUrl = null;
	private String localUser = null;
	private String localPassword = null;
	
	@PostConstruct
	public void setup() {
	}

	@Transactional(readOnly=false)
	@RequestMapping(value = "/graphs/{graph}/subscriptions", method = RequestMethod.POST)
	public String subscribe(
			@RequestParam(value = "servername", required = true) String serverName,
			@PathVariable(value = "graph") String graph,
			@RequestParam(value = "groupname", required = true) String groupName,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "password", required = false) String password)
			throws SubscriptionFailedException, GraphNotExistsException {

		if (subscriptionCallService != null) {
			ISubscription subscription = new Subscription(serverName, graph, groupName, url, user, password, null);
			boolean ok = subscriptionAPIService.subscribeNewGraph(subscription);

			if (ok) {
				log.info("local subscription successful: " + serverName + "(" + url + "): " + graph);
				subscriptionCallService.subscribe(localServerName, graph, groupName, url, localUrl, localUser, localPassword);
				log.info("external subscription successful: " + localServerName + "(" + localUrl + "): " + graph);
				return "Subscription for " + graph + " and server " + serverName + " successful";
			} else {
				throw new SubscriptionFailedException("local subscription failed: " + serverName + "(" + url + "): " + graph);
			}
		} else {
			ISubscription subscription = new Subscription(serverName, graph, groupName, url, user, password, null);
			boolean ok = subscriptionAPIService.subscribeOnView(subscription);

			if (ok) {
				log.info("local subscription successful: " + serverName + "(" + url + "): " + graph);
				return "Subscription for " + graph + " and server " + serverName + " successful";
			} else {
				throw new SubscriptionFailedException("local subscription failed: " + serverName + "(" + url + "): " + graph);
			}			
		}
		
	}

	@RequestMapping(value = "/graphs/{graph}/subscriptions", method = RequestMethod.DELETE)
	@ResponseBody
	public String unsubscribe(
			@RequestParam(value = "servername", required = true) String serverName,
			@PathVariable(value = "graph") String graph) throws SubscriptionFailedException {

		ISubscription subscription = subscriptionAPIService.getSubscriptionForViewAndServer(serverName, graph);

		boolean ok;

		ok = subscriptionAPIService.unsubscribe(serverName, graph);

		if (ok) {
			log.info("local unsubscription successful: " + serverName + ": " + graph);
		} else {
			throw new SubscriptionFailedException("local unsubscription not successful: " + serverName + ": " + graph);
		}


		if (subscriptionCallService != null) {
			subscriptionCallService.unsubscribe(localServerName, graph, subscription.getUrl());

			log.info("external unsubscription successful: " + localServerName + ": " + graph);
		}

		return "unsubscription successful";
	}

	@RequestMapping(value = "/graphs/{graph}/subscriptions", method = RequestMethod.GET)
	@ResponseBody
	public List<ISubscriptionDTO> getSubscriptions(
			@PathVariable(value = "graph") String graph,
			@RequestParam(value = "servername", required = false) String serverName)
			throws SubscriptionFailedException, GraphNotExistsException, ResourceNotFoundException {

		ISubscriptionContainerDTO subscriptions = subscriptionAPIService.getSubscriptionForGraphAndServer(serverName, graph);

		if (subscriptions != null) {
			return subscriptions.getSubscriptions();
		} else {
			throw new ResourceNotFoundException("No subscriptions found");
		}		
		
	}

	@RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
	@ResponseBody
	public List<ISubscriptionDTO> getSubscriptions() throws ResourceNotFoundException {

		ISubscriptionContainerDTO subscriptions = subscriptionAPIService.getAllSubscriptions();

		if (subscriptions != null) {
			return subscriptions.getSubscriptions();
		} else {
			throw new ResourceNotFoundException("No subscriptions found");
		}
		
	}

	public ISubscriptionAPIService getSubscriptionAPIService() {
		return subscriptionAPIService;
	}

	public void setSubscriptionAPIService(ISubscriptionAPIService subscriptionAPIService) {
		this.subscriptionAPIService = subscriptionAPIService;
	}

	public ISubscriptionCallService getSubscriptionCallService() {
		return subscriptionCallService;
	}

	public void setSubscriptionCallService(ISubscriptionCallService subscriptionCallService) {
		this.subscriptionCallService = subscriptionCallService;
	}

	public String getLocalServerName() {
		return localServerName;
	}

	public void setLocalServerName(String localServerName) {
		this.localServerName = localServerName;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getLocalUser() {
		return localUser;
	}

	public void setLocalUser(String localUser) {
		this.localUser = localUser;
	}

	public String getLocalPassword() {
		return localPassword;
	}

	public void setLocalPassword(String localPassword) {
		this.localPassword = localPassword;
	}

}