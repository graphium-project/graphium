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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.service.ISubscriptionService;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */
public class SubscriptionServiceImpl implements ISubscriptionService {

	private static Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

	private IWayGraphVersionMetadataDao metadataDao;
	private IWayGraphViewDao viewDao;
	private ISubscriptionDao dao;
	
	/**
	 * Subscribe on new graph in case of a satellite server. Will create new waygraph, subscription and subscriptionGroup.
	 * @param subscription
	 * @return
	 * @throws GraphNotExistsException
	 * @throws SubscriptionFailedException 
	 */
	@Override
	public boolean subscribeNewGraph(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException {
		log.info("Subscribing for: " + subscription);

		IWayGraph wayGraph = metadataDao.getGraph(subscription.getViewName());
		if (wayGraph == null) {
			// save new graph entry
			metadataDao.saveGraph(subscription.getViewName());
			wayGraph = metadataDao.getGraph(subscription.getViewName());
		}

		validateSubscriptionGroup(subscription, wayGraph.getName());

		subscription.getSubscriptionGroup().setGraph(wayGraph);
		
		return dao.subscribe(subscription);
	}

	/**
	 * Subscribe on existing view in case of a central server. Will read view and create, if view exists, subscription and subscriptionGroup.
	 * @param subscription
	 * @return
	 * @throws GraphNotExistsException
	 * @throws SubscriptionFailedException 
	 */
	@Override
	public boolean subscribeOnView(ISubscription subscription) throws GraphNotExistsException, SubscriptionFailedException {
		log.info("Subscribing for: " + subscription);

		IWayGraphView view = viewDao.getView(subscription.getViewName());
		
		if (view == null) {
			throw new GraphNotExistsException("View does not exist", subscription.getViewName());
		}

		validateSubscriptionGroup(subscription, view.getGraph().getName());
		
		subscription.getSubscriptionGroup().setGraph(view.getGraph());
		
		return dao.subscribe(subscription);
	}

	private void validateSubscriptionGroup(ISubscription subscription, String graphName) throws SubscriptionFailedException {
		ISubscriptionGroup subscriptionGroup = getSubscriptionGroup(subscription.getSubscriptionGroup().getName());
		if (subscriptionGroup != null && !subscriptionGroup.getGraph().getName().equals(graphName)) {
			throw new SubscriptionFailedException("subscription failed - subscription group " + subscription.getSubscriptionGroup().getName() + " already exists for another graph!");
		}
	}

	@Override
	public boolean unsubscribe(String serverName, String graphName) {
		log.info("Unsubscribing for: serverName = " + serverName + " and graphName = " + graphName);
		boolean rc = dao.unsubscribe(serverName, graphName);
		log.info("Unsubscription " + (rc ? "successful" : "not successful"));
		return rc;
	}

	@Override
	public ISubscription getSubscriptionForViewAndServer(String serverName, String viewName) {
		return dao.getSubscriptionForViewAndServer(serverName, viewName);
	}
	
	@Override
	public List<ISubscription> getSubscriptionsForGraph(String graphName) {
		return dao.getSubscriptionsForGraph(graphName);
	}
	
	@Override
	public List<ISubscription> getSubscriptionsForGraphAndServer(String serverName, String graphName) {
		return dao.getSubscriptionsForGraphAndServer(serverName, graphName);
	}
	
	@Override
	public ISubscriptionGroup getSubscriptionGroup(String groupName) {
		return dao.getSubscriptionGroup(groupName);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ISubscription> getAllSubscriptions() {
		return dao.getAllSubscriptions();
	}
	
	public ISubscriptionDao getDao() {
		return dao;
	}

	public void setDao(ISubscriptionDao dao) {
		this.dao = dao;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public IWayGraphViewDao getViewDao() {
		return viewDao;
	}

	public void setViewDao(IWayGraphViewDao viewDao) {
		this.viewDao = viewDao;
	}

}