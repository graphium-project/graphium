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

import java.util.Date;

import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.IGraphImportState;

public class GraphImportState implements IGraphImportState {

	private String serverName;
	private String viewName;
	private String version;
	private State state;
	private Date timestamp;
	
	public GraphImportState() {}
	
	public GraphImportState(String serverName, String viewName, String version, State state, Date timestamp) {
		super();
		this.serverName = serverName;
		this.viewName = viewName;
		this.version = version;
		this.state = state;
		this.timestamp = timestamp;
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
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
