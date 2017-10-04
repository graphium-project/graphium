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
package at.srfg.graphium.core.persistence;

import java.util.List;

import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 *
 */
public interface ISubscriptionDao {

	boolean subscribe(ISubscription subscription);
	
	boolean unsubscribe(String serverName, String graphName);
	
	List<ISubscription> getSubscriptionsForGraph(String graphName);

	List<ISubscription> getSubscriptionsForGraphAndServer(String serverName,
			String graphName);

	List<ISubscription> getSubscriptionsForView(String viewName);

	ISubscription getSubscriptionForViewAndServer(String serverName, String viewName);

	List<ISubscription> getSubscriptionsForGraph(String graphName, String groupName);
	
	List<ISubscription> getAllSubscriptions();

	ISubscriptionGroup getSubscriptionGroup(String groupName);
	
}
