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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.srfg.graphium.model.management.ISubscription;
import at.srfg.graphium.model.management.ISubscriptionGroup;

/**
 * @author mwimmer
 *
 */
public class Subscription implements ISubscription {
	
	private String serverName;
	private String viewName;
	private String url;
	private String user;
	private String password;
	private Date timestamp;
	private ISubscriptionGroup subscriptionGroup;
	
	public Subscription() {}
	
	public Subscription(String serverName, String viewName, String url, String user, String password, Date timestamp) {
		super();
		this.serverName = serverName;
		this.viewName = viewName;
		this.url = url;
		this.user = user;
		this.password = password;
		this.timestamp = timestamp;
	}

	public Subscription(String serverName, String viewName, String groupName, String url, String user, String password, Date timestamp) {
		super();
		this.serverName = serverName;
		this.viewName = viewName;
		this.url = url;
		this.user = user;
		this.password = password;
		this.timestamp = timestamp;
		List<ISubscription> subscriptions = new ArrayList<>();
		subscriptions.add(this);
		subscriptionGroup = new SubscriptionGroup(0, groupName, null, subscriptions);
	}

	public Subscription(String serverName, String viewName, ISubscriptionGroup subscriptionGroup, String url, String user, String password, Date timestamp) {
		super();
		this.serverName = serverName;
		this.viewName = viewName;
		this.url = url;
		this.user = user;
		this.password = password;
		this.timestamp = timestamp;
		this.subscriptionGroup = subscriptionGroup;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public ISubscriptionGroup getSubscriptionGroup() {
		return subscriptionGroup;
	}

	@Override
	public void setSubscriptionGroup(ISubscriptionGroup subscriptionGroup) {
		this.subscriptionGroup = subscriptionGroup;
	}

	@Override
	public String toString() {
		return "Subscription [serverName=" + serverName + ", viewName=" + viewName + ", url=" + url + ", user=" + user
				+ ", password=" + password + ", timestamp=" + timestamp + ", subscriptionGroup=" + subscriptionGroup.getName()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((viewName == null) ? 0 : viewName.hashCode());
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Subscription other = (Subscription) obj;
		if (viewName == null) {
			if (other.viewName != null)
				return false;
		} else if (!viewName.equals(other.viewName))
			return false;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}