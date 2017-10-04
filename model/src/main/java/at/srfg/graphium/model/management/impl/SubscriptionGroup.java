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
package at.srfg.graphium.model.management.impl;

import java.util.List;

import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 *
 */
public class SubscriptionGroup implements ISubscriptionGroup {

	private int id;
	private String name;
	private IWayGraph graph;
	private List<ISubscription> subscriptions;
	
	public SubscriptionGroup() {}
	
	public SubscriptionGroup(int id, String name, IWayGraph graph, List<ISubscription> subscriptions) {
		super();
		this.id = id;
		this.name = name;
		this.graph = graph;
		this.subscriptions = subscriptions;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<ISubscription> getSubscriptions() {
		return subscriptions;
	}

	@Override
	public void setSubscriptions(List<ISubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public IWayGraph getGraph() {
		return graph;
	}

	@Override
	public void setGraph(IWayGraph graph) {
		this.graph = graph;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriptionGroup other = (SubscriptionGroup) obj;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubscriptionGroup [id=" + id + ", name=" + name + ", graph=" + graph + ", subscriptions="
				+ subscriptions + "]";
	}

}