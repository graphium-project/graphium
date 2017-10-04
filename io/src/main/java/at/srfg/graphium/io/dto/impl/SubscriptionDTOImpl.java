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
package at.srfg.graphium.io.dto.impl;

import java.util.Date;

import at.srfg.graphium.io.dto.ISubscriptionDTO;

/**
 * @author mwimmer
 */
public class SubscriptionDTOImpl implements ISubscriptionDTO {

	private String serverName;
	private String viewName;
	private String url;
	private Date timestamp;
	private String subscriptionGroupName;
	
	public SubscriptionDTOImpl() {}

	public SubscriptionDTOImpl(String serverName, String viewName, String subscriptionGroupName, String url, Date timestamp) {
		super();
		this.serverName = serverName;
		this.viewName = viewName;
		this.url = url;
		this.timestamp = timestamp;
		this.subscriptionGroupName = subscriptionGroupName;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getSubscriptionGroupName() {
		return subscriptionGroupName;
	}

	public void setSubscriptionGroupName(String subscriptionGroupName) {
		this.subscriptionGroupName = subscriptionGroupName;
	}

}
