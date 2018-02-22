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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.persistence.ISubscriptionDao;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 *
 */
public class SubscriptionDaoImpl extends AbstractDaoImpl implements
		ISubscriptionDao {
	
	private static Logger log = LoggerFactory.getLogger(SubscriptionDaoImpl.class);
	
	private RowMapper<ISubscription> subscriptionRowMapper;
	private RowMapper<ISubscriptionGroup> subscriptionGroupRowMapper;
	
	@Override
	public boolean subscribe(ISubscription subscription) {
		
		ISubscription existingSubscription = getSubscriptionForViewAndServer(subscription.getServerName(), subscription.getViewName());
		
		if (existingSubscription != null) {
			Object[] args = new Object[6];
			args[0] = existingSubscription.getSubscriptionGroup().getId();
			args[1] = subscription.getUrl();
			args[2] = subscription.getUser();
			args[3] = subscription.getPassword();
			args[4] = subscription.getServerName();
			args[5] = subscription.getViewName();
			
			int inserted = getJdbcTemplate().update("UPDATE " + schema + "subscriptions " +
							"SET group_id = ?, url = ?, \"user\" = ?, \"password\" = ? " +
							"WHERE servername = ? AND viewname = ?", args);
			
			return inserted == 1;

		} else {
			Object[] args = new Object[1];
			args[0] = subscription.getSubscriptionGroup().getName();
			
			List<Integer> groupIds = getJdbcTemplate().queryForList("SELECT id FROM " + schema + "subscription_groups WHERE name = ?", 
									Integer.class, args);

			Integer groupId = 0;
			if (groupIds == null || groupIds.isEmpty()) {
				// insert subscription group
				Map<String, Object>  params = new HashMap<String, Object>(); 
				params.put("group_name", subscription.getSubscriptionGroup().getName());
				params.put("group_id", subscription.getSubscriptionGroup().getGraph().getId());
				MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
				KeyHolder keyHolder = new GeneratedKeyHolder();
	
				getNamedParameterJdbcTemplate().update("INSERT INTO " + schema + "subscription_groups " + 
						"(name, graph_id) VALUES (:group_name, :group_id)", 
						sqlParameterSource, keyHolder, new String[] {"id"});
				groupId = Integer.class.cast(keyHolder.getKey());
			} else {
				groupId = groupIds.get(0);
			}
			
			subscription.getSubscriptionGroup().setId(groupId);
			
			// insert subscription
			args = new Object[6];
			args[0] = subscription.getServerName();
			args[1] = subscription.getViewName();
			args[2] = subscription.getSubscriptionGroup().getId();
			args[3] = subscription.getUrl();
			args[4] = subscription.getUser();
			args[5] = subscription.getPassword();
			
			int inserted = getJdbcTemplate().update("INSERT INTO " + schema + "subscriptions " + 
							"(servername, viewname, group_id, url, \"user\", \"password\") VALUES (?,?,?,?,?,?)", args);
			
			return inserted == 1;
		}
	}

	@Override
	public boolean unsubscribe(String serverName, String graphName) {
		Object[] args = new Object[2];
		args[0] = serverName;
		args[1] = graphName;
		
		ISubscription subscription = getSubscriptionForViewAndServer(serverName, graphName);
		
		if (subscription == null) {
			return false;
		} else {
			
			int deleted = getJdbcTemplate().update("DELETE FROM " + schema + "subscriptions " + 
							"WHERE servername = ? AND viewname = ?", args);
			
			// check if all subscriptions deleted to a subscription group; if so => delete the subscription group
			args = new Object[1];
			args[0] = subscription.getSubscriptionGroup().getId();
			Long countSubscriptions = getJdbcTemplate().queryForObject("SELECT count(1) FROM " + schema + "subscriptions WHERE group_id = ?", 
																		args, Long.class);
			if (countSubscriptions == null || countSubscriptions == 0) {
				getJdbcTemplate().update("DELETE FROM " + schema + "subscription_groups WHERE id = ?", args);
			}

			return deleted == 1;
		}
	}

//	GraphVersionImportListenerImpl.notify => in SGS; von ZGS kommt viewName, der in SGS auch graphName ist			=> getSubscriptionsForGraph
	@Override
	@Transactional(readOnly=true)
	public List<ISubscription> getSubscriptionsForGraph(String graphName) {
		Object[] args = new Object[1];
		args[0] = graphName;
		
		// <graphName> => lies waygraphs => <graph_id> => lies subscription_groups => <group_id> => lies subscriptions
		return getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
				"FROM " + schema + "subscriptions AS s, " + schema + "subscription_groups AS sg " +
				"LEFT JOIN " + schema + "waygraphs AS wg ON sg.graph_id = wg.id " +
				"WHERE s.group_id = sg.id AND sg.graph_id = (SELECT w.id FROM " + schema + "waygraphs AS w WHERE name = ?)", 
				args, subscriptionRowMapper);
	}

//	GraphVersionImportHttpNotifierImpl.notifyRegisteredServersOfActivating => in ZGS; Eingabe des graphName			=> getSubscriptionsForGraph
//	GraphVersionImportHttpNotifierImpl.notifyRegisteredServersOfFailedActivating => in ZGS; Eingabe des graphName	=> getSubscriptionsForGraph
//	GraphVersionImportHttpNotifierImpl.notifyRegisteredServersOfPublishing => in ZGS; Eingabe des graphName			=> getSubscriptionsForGraph
	@Override
	@Transactional(readOnly=true)
	public List<ISubscription> getSubscriptionsForGraph(String graphName, String groupName) {
		if (groupName == null) {
			return getSubscriptionsForGraph(graphName);
		} else {
			Object[] args = new Object[2];
			args[0] = graphName;
			args[1] = groupName;
			
			// <graphName> => lies waygraphs => <graph_id> => lies subscription_groups => <group_id> => lies subscriptions
			return getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
					"FROM " + schema + "subscriptions AS s, " +
							  schema + "subscription_groups AS sg " +
					"LEFT JOIN " + schema + "waygraphs AS wg ON sg.graph_id = wg.id " +
					"WHERE s.group_id = sg.id AND sg.graph_id = (SELECT w.id FROM " + schema + "waygraphs AS w WHERE name = ?) " +
					"AND sg.name = ?", 
					args, subscriptionRowMapper);
		}
	}

//	GraphVersionImportListenerImpl.notify => in SGS; von ZGS kommt viewName, der in SGS auch graphName ist			=> getSubscriptionsForGraphAndServer
	@Override
	@Transactional(readOnly=true)
	public List<ISubscription> getSubscriptionsForGraphAndServer(String serverName, String graphName) {
		Object[] args = new Object[2];
		args[0] = graphName;
		args[1] = serverName;
		
		// <graphName> => lies waygraphs => <graph_id> => lies subscription_groups => <group_id> => lies subscriptions
		return getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
				"FROM " + schema + "subscriptions AS s, " + 
				  		  schema + "subscription_groups AS sg " +
				"LEFT JOIN " + schema + "waygraphs AS wg ON sg.graph_id = wg.id " +
				"WHERE s.group_id = sg.id AND sg.graph_id = (SELECT w.id FROM " + schema + "waygraphs AS w WHERE name = ?) AND servername = ?", 
				args, subscriptionRowMapper);
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<ISubscription> getSubscriptionsForView(String viewName) {
		Object[] args = new Object[1];
		args[0] = viewName;
		
		return getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
				"FROM " + schema + "subscriptions AS s " +
				"LEFT JOIN " + 
				schema + "subscription_groups AS sg ON s.group_id = sg.id " +
				"LEFT JOIN " + 
				schema + "waygraphs AS wg ON sg.graph_id = wg.id " +
				"WHERE viewname = ?", args, subscriptionRowMapper);
	}

//	SubscriptionApiController.subscribe => in ZGS und SGS; Eingabe des viewName, von SGS kommt viewName				=> getSubscriptionsForViewAndServer
//	SubscriptionApiController.unsubscribe => in ZGS und SGS; Eingabe des viewName, von SGS kommt viewName			=> getSubscriptionsForViewAndServer
	@Override
	@Transactional(readOnly=true)
	public ISubscription getSubscriptionForViewAndServer(String serverName, String viewName) {
		Object[] args = new Object[2];
		args[0] = serverName;
		args[1] = viewName;
		
		List<ISubscription> subscriptions = getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
				"FROM " + schema + "subscriptions AS s " +
				"LEFT JOIN " + 
				schema + "subscription_groups AS sg ON s.group_id = sg.id " +
				"LEFT JOIN " + 
				schema + "waygraphs AS wg ON sg.graph_id = wg.id " +
				"WHERE servername = ? AND viewname = ?", args, subscriptionRowMapper);
		
		if (subscriptions != null && !subscriptions.isEmpty()) {
			return subscriptions.get(0);
		} else {
			return null;
		}
		
	}

	@Override
	public List<ISubscription> getAllSubscriptions() {
		return getJdbcTemplate().query("SELECT s.*, sg.name AS group_name, sg.graph_id AS group_graph_id, wg.name AS graph_name " +
				"FROM " + schema + "subscriptions AS s " +
				"LEFT JOIN " + 
				schema + "subscription_groups AS sg ON s.group_id = sg.id " +
				"LEFT JOIN " + 
				schema + "waygraphs AS wg ON sg.graph_id = wg.id" , subscriptionRowMapper);
	}
	
	@Override
	public ISubscriptionGroup getSubscriptionGroup(String groupName) {
		Object[] args = new Object[1];
		args[0] = groupName;
		List<ISubscriptionGroup> subscriptionGroups = getJdbcTemplate().query("SELECT sg.*, wg.name AS graph_name FROM " + schema + "subscription_groups AS sg " +
					"LEFT JOIN " + schema + "waygraphs AS wg ON sg.graph_id = wg.id WHERE sg.name = ?", args, subscriptionGroupRowMapper);
		if (subscriptionGroups != null && !subscriptionGroups.isEmpty()) {
			return subscriptionGroups.get(0);
		} else {
			return null;
		}
	}

	public RowMapper<ISubscription> getSubscriptionRowMapper() {
		return subscriptionRowMapper;
	}

	public void setSubscriptionRowMapper(RowMapper<ISubscription> subscriptionRowMapper) {
		this.subscriptionRowMapper = subscriptionRowMapper;
	}

	public RowMapper<ISubscriptionGroup> getSubscriptionGroupRowMapper() {
		return subscriptionGroupRowMapper;
	}

	public void setSubscriptionGroupRowMapper(RowMapper<ISubscriptionGroup> subscriptionGroupRowMapper) {
		this.subscriptionGroupRowMapper = subscriptionGroupRowMapper;
	}
	
}