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
package at.srfg.graphium.postgis.persistence.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;
import at.srfg.graphium.model.management.impl.Subscription;
import at.srfg.graphium.model.management.impl.SubscriptionGroup;

/**
 * @author mwimmer
 *
 */
public class SubscriptionRowMapper implements RowMapper<ISubscription> {

	@Override
	public ISubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer subscriptionGroupId = rs.getInt("group_id");
		String subscriptionGroupName = rs.getString("group_name");
		Long subscriptionGroupGraphId = rs.getLong("group_graph_id");
		String graphName = rs.getString("graph_name");
		ISubscriptionGroup subscriptionGroup = null;
		if (subscriptionGroupId != null && 
			subscriptionGroupName != null && 
			subscriptionGroupGraphId != null) {
			IWayGraph graph = new WayGraph(subscriptionGroupGraphId, graphName);
			subscriptionGroup = new SubscriptionGroup(subscriptionGroupId, subscriptionGroupName, graph, new ArrayList<>());
		}
		
		ISubscription subscription = new Subscription(
				rs.getString("servername"), 
				rs.getString("viewname"), 
				subscriptionGroup,
				rs.getString("url"),
				rs.getString("user"),
				rs.getString("password"),
				(Date) rs.getTimestamp("timestamp"));
		
		if (subscriptionGroup != null) {
			subscription.getSubscriptionGroup().getSubscriptions().add(subscription);
		}
		
		return subscription;
	}

}
