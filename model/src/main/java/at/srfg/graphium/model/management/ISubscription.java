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
package at.srfg.graphium.model.management;

import java.util.Date;

/**
 * @author mwimmer
 *
 */
public interface ISubscription {

	public String getServerName();
	public void setServerName(String serverName);
	public String getViewName();
	public void setViewName(String viewName);
	public String getUrl();
	public void setUrl(String url);
	public String getUser();
	public void setUser(String user);
	public String getPassword();
	public void setPassword(String password);
	public Date getTimestamp();
	public void setTimestamp(Date timestamp);
	public ISubscriptionGroup getSubscriptionGroup();
	public void setSubscriptionGroup(ISubscriptionGroup subscriptionGroup);
	
}
