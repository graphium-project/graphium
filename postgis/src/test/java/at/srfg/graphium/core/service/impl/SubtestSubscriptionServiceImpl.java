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
package at.srfg.graphium.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import at.srfg.graphium.ITestGraphiumPostgis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.service.ISubscriptionService;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;
import at.srfg.graphium.model.management.impl.Subscription;
import at.srfg.graphium.model.management.impl.SubscriptionGroup;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.model.view.impl.WayGraphView;

/**
 * @author mwimmer
 *
 */

public class TestSubscriptionServiceImpl implements ITestGraphiumPostgis{

	private static Logger log = LoggerFactory.getLogger(TestSubscriptionServiceImpl.class);
    @Value("${db.graphNameSubscriptionTest}")
    String graphName;
	@Value("${db.graphNameSubscriptionTest2}")
	String graphName2;
    @Value("${db.graphVersionSubscriptionTest}")
    String version;
    @Value("${db.viewNameSubscriptionTest}")
    String viewName;
    @Value("${db.serverName}")
    String serverName;
    @Value("${db.serverURL}")
    String serverURL;
    @Value("${db.test.user}")
    String user;
    @Value("${db.test.pw}")
    String pw;
    @Value("${db.subscriptionGroupID}")
    int subscriptionGroupID;
    @Value("#{new java.text.SimpleDateFormat(\"${db.dateFormat}\").parse(\"${db.subscriptionDateString}\")}") //using spEL
    Date subscriptionDate;
	
	@Autowired
	private ISubscriptionService subscriptionService;
	@Autowired
	private IWayGraphViewDao viewDao;
	@Autowired
	private IWayGraphVersionMetadataDao metaDao;

	@Transactional(readOnly=false)
	public void testCreateValidSubscriptionForDefaultView() {
		IWayGraph wayGraph = saveWayGraph(graphName, version);
		viewDao.saveDefaultView(wayGraph);
		
		ISubscription subscription = new Subscription(serverName, graphName, serverURL, user, pw, subscriptionDate);
		List<ISubscription> subscriptions = new ArrayList<>();
		subscriptions.add(subscription);
		ISubscriptionGroup subscriptionGroup = new SubscriptionGroup(subscriptionGroupID, graphName, wayGraph, subscriptions);
		subscription.setSubscriptionGroup(subscriptionGroup);
		
		try {
			subscriptionService.subscribeOnView(subscription);
		} catch (GraphNotExistsException | SubscriptionFailedException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional(readOnly=false)
	public void testCreateValidSubscriptionForCustomView() {
		IWayGraph wayGraph = saveWayGraph(graphName2, version);
		saveView(wayGraph, viewName, version);

		ISubscription subscription = new Subscription(serverName, viewName, serverURL, user, pw, subscriptionDate);
		List<ISubscription> subscriptions = new ArrayList<>();
		subscriptions.add(subscription);
		ISubscriptionGroup subscriptionGroup = new SubscriptionGroup(subscriptionGroupID, graphName2, wayGraph, subscriptions);
		subscription.setSubscriptionGroup(subscriptionGroup);

		try {
			subscriptionService.subscribeOnView(subscription);
		} catch (GraphNotExistsException | SubscriptionFailedException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public IWayGraph saveWayGraph(String graphName, String version) {
		IWayGraph wayGraph = metaDao.getGraph(graphName);
		if (wayGraph != null) {
			log.error("Waygraph '" + graphName + "' existiert bereits");
		} else {
			log.info("Waygraph '" + graphName + "' existiert noch nicht");
			metaDao.saveGraph(graphName);
			wayGraph = metaDao.getGraph(graphName);
			log.info("Waygraph '" + graphName + "' wurde gespeichert");
		}
		return wayGraph;
	}
	
	public void saveView(IWayGraph wayGraph, String viewName, String version) {
		IWayGraphView view = new WayGraphView(viewName, wayGraph, viewName, true, null, 0, 0, null);
		viewDao.saveView(view);
	}

	@Override
	public void run() {
		testCreateValidSubscriptionForDefaultView();
		testCreateValidSubscriptionForCustomView();
	}
}