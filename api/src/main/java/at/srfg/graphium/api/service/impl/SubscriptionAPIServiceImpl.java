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
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.api.service.ISubscriptionAPIService;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.core.service.ISubscriptionService;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.ISubscriptionContainerDTO;
import at.srfg.graphium.io.dto.ISubscriptionDTO;
import at.srfg.graphium.io.dto.impl.SubscriptionContainerDTO;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 */
public class SubscriptionAPIServiceImpl implements ISubscriptionAPIService {
	
	private ISubscriptionService subscriptionService;
	private IAdapter<ISubscriptionDTO, ISubscription> adapter;

	@Override
	public boolean subscribeNewGraph(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException {
		return subscriptionService.subscribeNewGraph(subscription);
	}

	@Override
	public boolean subscribeOnView(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException {
		return subscriptionService.subscribeOnView(subscription);
	}

	@Override
	public boolean unsubscribe(String serverName, String graphName) {
		return subscriptionService.unsubscribe(serverName, graphName);
	}

	@Override
	public ISubscriptionGroup getSubscriptionGroup(String groupName) {
		return subscriptionService.getSubscriptionGroup(groupName);
	}

	@Override
	public ISubscription getSubscriptionForViewAndServer(String serverName, String viewName) {
		return subscriptionService.getSubscriptionForViewAndServer(serverName, viewName);
	}
	
	@Override
	public ISubscriptionContainerDTO getSubscriptionForGraphAndServer(String serverName, String graphName) {
		ISubscriptionContainerDTO container = null;
		List<ISubscriptionDTO> subscriptionDTOs = null;
		List<ISubscription> subscriptions = null; 
		if (serverName == null) {
			subscriptions = subscriptionService.getSubscriptionsForGraph(graphName);
		} else {
			subscriptions = subscriptionService.getSubscriptionsForGraphAndServer(serverName, graphName);
		}
		
		if (subscriptions != null && !subscriptions.isEmpty()) {
			subscriptionDTOs = new ArrayList<>();
			for (ISubscription subscription : subscriptions) {
				subscriptionDTOs.add(adapter.adapt(subscription));
			}
			container = new SubscriptionContainerDTO(subscriptionDTOs);
		}
		
		return container;
	}
		
	@Override
	public ISubscriptionContainerDTO getAllSubscriptions() {
		ISubscriptionContainerDTO container = null;
		List<ISubscriptionDTO> subscriptionDTOs = null;
		List<ISubscription> subscriptions = subscriptionService.getAllSubscriptions();
		
		if (subscriptions != null && !subscriptions.isEmpty()) {
			subscriptionDTOs = new ArrayList<>();
			for (ISubscription subscription : subscriptions) {
				subscriptionDTOs.add(adapter.adapt(subscription));
			}
			container = new SubscriptionContainerDTO(subscriptionDTOs);
		}
		
		return container;
	}

	public ISubscriptionService getSubscriptionService() {
		return subscriptionService;
	}

	public void setSubscriptionService(ISubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public IAdapter<ISubscriptionDTO, ISubscription> getAdapter() {
		return adapter;
	}

	public void setAdapter(IAdapter<ISubscriptionDTO, ISubscription> adapter) {
		this.adapter = adapter;
	}

}
