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
package at.srfg.graphium.api.events.impl;

import java.util.Map;

import at.srfg.graphium.api.events.IEvent;

public class EventImpl implements IEvent {

	private String name;
	private String source;
	private Map<String,Object> params;
	
	public EventImpl() {}
	
	public EventImpl(String name, String source, Map<String, Object> params) {
		this.name = name;
		this.source = source;
		this.params = params;
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
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
