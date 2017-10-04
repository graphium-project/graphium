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

import java.util.List;

import at.srfg.graphium.io.dto.ISubscriptionContainerDTO;
import at.srfg.graphium.io.dto.ISubscriptionDTO;

/**
 * @author mwimmer
 */
public class SubscriptionContainerDTO implements ISubscriptionContainerDTO {

	private List<ISubscriptionDTO> subscriptions;
	
	public SubscriptionContainerDTO(List<ISubscriptionDTO> subscriptions) {
		super();
		this.subscriptions = subscriptions;
	}

	@Override
	public List<ISubscriptionDTO> getSubscriptions() {
		return subscriptions;
	}

	@Override
	public void setSubscriptions(List<ISubscriptionDTO> subscriptions) {
		this.subscriptions = subscriptions;
	}

}
