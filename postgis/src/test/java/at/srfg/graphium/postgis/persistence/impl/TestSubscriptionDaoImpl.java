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
package at.srfg.graphium.postgis.persistence.impl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;
import at.srfg.graphium.model.management.impl.Subscription;
import at.srfg.graphium.model.management.impl.SubscriptionGroup;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-postgis_test.xml",
		"classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis-datasource.xml",
		"classpath:application-context-graphium-postgis-aliasing.xml"})
public class TestSubscriptionDaoImpl {

	@Autowired
	private ISubscriptionDao dao;
	
	@Autowired
	private TestWayGraphViewDaoImpl viewDaoTest;
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(true)
	public void testSubscriptionDao() {
		String serverName = "MOWI89";
		String graphName = "gip_at_frc_0_4";
		String viewName1 = "view1_gip_at_frc_0_4";
		String viewName2 = "view2_gip_at_frc_0_4";
		String groupName1 = "group1";
		String groupName2 = "group2";
		String url = "http://mowi89.at";
		
		IWayGraphView view = viewDaoTest.saveView(graphName, null);
		
		ISubscriptionGroup subscriptionGroup1 = new SubscriptionGroup(0, groupName1, view.getGraph(), null);
		ISubscriptionGroup subscriptionGroup2 = new SubscriptionGroup(0, groupName2, view.getGraph(), null);

		ISubscription subscription = new Subscription(serverName, graphName, subscriptionGroup1, url, null, null, null);
		boolean subscribed = dao.subscribe(subscription);
		System.out.println("\nSubscription (1. Versuch) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		subscription = new Subscription(serverName, viewName1, subscriptionGroup1, url, null, null, null);
		subscribed = dao.subscribe(subscription);
		System.out.println("\nSubscription (1. Versuch für View) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));

		subscription = new Subscription(serverName, viewName2, subscriptionGroup2, url, null, null, null);
		subscribed = dao.subscribe(subscription);
		System.out.println("\nSubscription (1. Versuch für View) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		List<ISubscription> subscriptions = dao.getSubscriptionsForGraph(graphName);
		System.out.println("\ngespeicherte Subscriptions für graphName:");
		for (ISubscription sub : subscriptions) {
			System.out.println("\n\t" + sub);
		}
		
		subscriptions = dao.getSubscriptionsForGraph(graphName, groupName1);
		System.out.println("\ngespeicherte Subscriptions für graphName and groupName:");
		for (ISubscription sub : subscriptions) {
			System.out.println("\n\t" + sub);
		}
		
		subscriptions = dao.getSubscriptionsForGraphAndServer(serverName, graphName);
		System.out.println("\ngespeicherte Subscription für server und graphName:");
		for (ISubscription sub : subscriptions) {
			System.out.println("\n\t" + sub);
		}
		
		subscriptions = dao.getSubscriptionsForView(viewName1);
		System.out.println("\ngespeicherte Subscriptions für viewName:");
		for (ISubscription sub : subscriptions) {
			System.out.println("\n\t" + sub);
		}
		
		ISubscription savedSubscription = dao.getSubscriptionForViewAndServer(serverName, viewName1);
		System.out.println("\ngespeicherte Subscription für server und viewName:");
		System.out.println("\n\t" + savedSubscription);

		subscribed = dao.subscribe(subscription);
		System.out.println("\nSubscription (2. Versuch) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		subscribed = dao.unsubscribe(serverName, graphName);
		System.out.println("\nUnsubscription war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));

		subscriptions = dao.getSubscriptionsForGraph(graphName);
		System.out.println("\ngespeicherte Subscriptions:");
		for (ISubscription sub : subscriptions) {
			System.out.println("\n\t" + sub);
		}
		
		ISubscriptionGroup subscriptionGroup = dao.getSubscriptionGroup("group1");
		System.out.println("\nSubscription Group: " + subscriptionGroup);

	}

}
