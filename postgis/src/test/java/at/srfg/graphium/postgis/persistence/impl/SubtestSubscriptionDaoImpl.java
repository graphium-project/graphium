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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.ITestGraphiumPostgis;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;
import at.srfg.graphium.model.management.impl.Subscription;
import at.srfg.graphium.model.management.impl.SubscriptionGroup;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */

public class SubtestSubscriptionDaoImpl implements ITestGraphiumPostgis{

	@Autowired
	private ISubscriptionDao sDao;

	@Autowired
	private IWayGraphViewDao dao;

	@Autowired
	private IWayGraphVersionMetadataDao metaDao;

	@Value("${db.serverName}")
	String serverName;
	@Value("${db.graphName}")
	String graphName;
	@Value("${db.viewName}")
	String viewName1;
	@Value("${db.viewName2}")
	String viewName2;
	@Value("${db.groupName1}")
	String groupName1;
	@Value("${db.groupName2}")
	String groupName2;
	@Value("${db.serverURL}")
	String url;
    @Value("${db.subscriptionGroupID}")
    int subscriptionID;

	private static Logger log = LoggerFactory.getLogger(SubtestSubscriptionDaoImpl.class);

	@Transactional(readOnly=false)
	public void testSubscriptionDao() {
		IWayGraphView view = saveView(graphName, null);
		
		ISubscriptionGroup subscriptionGroup1 = new SubscriptionGroup(subscriptionID, groupName1, view.getGraph(), null);
		ISubscriptionGroup subscriptionGroup2 = new SubscriptionGroup(subscriptionID, groupName2, view.getGraph(), null);

		ISubscription subscription = new Subscription(serverName, graphName, subscriptionGroup1, url, null, null, null);
		boolean subscribed = sDao.subscribe(subscription);
		log.info("\nSubscription (1. Versuch) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		subscription = new Subscription(serverName, viewName1, subscriptionGroup1, url, null, null, null);
		subscribed = sDao.subscribe(subscription);
		log.info("\nSubscription (1. Versuch für View) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));

		subscription = new Subscription(serverName, viewName2, subscriptionGroup2, url, null, null, null);
		subscribed = sDao.subscribe(subscription);
		log.info("\nSubscription (1. Versuch für View) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		List<ISubscription> subscriptions = sDao.getSubscriptionsForGraph(graphName);
		log.info("\ngespeicherte Subscriptions für graphName:");
		for (ISubscription sub : subscriptions) {
			log.info("\n\t" + sub);
		}
		
		subscriptions = sDao.getSubscriptionsForGraph(graphName, groupName1);
		log.info("\ngespeicherte Subscriptions für graphName and groupName:");
		for (ISubscription sub : subscriptions) {
			log.info("\n\t" + sub);
		}
		
		subscriptions = sDao.getSubscriptionsForGraphAndServer(serverName, graphName);
		log.info("\ngespeicherte Subscription für server und graphName:");
		for (ISubscription sub : subscriptions) {
			log.info("\n\t" + sub);
		}
		
		subscriptions = sDao.getSubscriptionsForView(viewName1);
		log.info("\ngespeicherte Subscriptions für viewName:");
		for (ISubscription sub : subscriptions) {
			log.info("\n\t" + sub);
		}
		
		ISubscription savedSubscription = sDao.getSubscriptionForViewAndServer(serverName, viewName1);
		log.info("\ngespeicherte Subscription für server und viewName:");
		log.info("\n\t" + savedSubscription);

		subscribed = sDao.subscribe(subscription);
		log.info("\nSubscription (2. Versuch) war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));
		
		subscribed = sDao.unsubscribe(serverName, graphName);
		log.info("\nUnsubscription war '" + (subscribed ? "erfolgreich" : "nicht erfolgreich"));

		subscriptions = sDao.getSubscriptionsForGraph(graphName);
		log.info("\ngespeicherte Subscriptions:");
		for (ISubscription sub : subscriptions) {
			log.info("\n\t" + sub);
		}
		
		ISubscriptionGroup subscriptionGroup = sDao.getSubscriptionGroup("group1");
		log.info("\nSubscription Group: " + subscriptionGroup);
	}

	public IWayGraph saveWayGraph(String graphName, String version) {
		if (graphName == null) {
			graphName = "osm_lu_180213";
		}
		if (version == null) {
			version = "1.0";
		}

		IWayGraph wayGraph = metaDao.getGraph(graphName);
		if (wayGraph != null) {
			log.info("Waygraph '" + graphName + "' existiert bereits");
		} else {
			log.info("Waygraph '" + graphName + "' existiert noch nicht");
			metaDao.saveGraph(graphName);
			wayGraph = metaDao.getGraph(graphName);
			log.info("Waygraph '" + graphName + "' wurde gespeichert");
		}
		return wayGraph;
	}

	public IWayGraphView saveView(String graphName, String version) {
		IWayGraph wayGraph = saveWayGraph(graphName, version);
		dao.saveDefaultView(wayGraph);

		try {
			return dao.getView(graphName);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		return null;
	}

	@Override
	public void run() {
		testSubscriptionDao();
	}

}
