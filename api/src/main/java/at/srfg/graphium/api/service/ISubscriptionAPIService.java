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
package at.srfg.graphium.api.service;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.io.dto.ISubscriptionContainerDTO;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 */
public interface ISubscriptionAPIService {

	public boolean subscribeNewGraph(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException;

	public boolean subscribeOnView(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException;
	
	public boolean unsubscribe(String serverName, String graphName);
	
	public ISubscription getSubscriptionForViewAndServer(String serverName, String viewName);

	public ISubscriptionGroup getSubscriptionGroup(String groupName);

	public ISubscriptionContainerDTO getAllSubscriptions();

	public ISubscriptionContainerDTO getSubscriptionForGraphAndServer(String serverName, String graphName);

}