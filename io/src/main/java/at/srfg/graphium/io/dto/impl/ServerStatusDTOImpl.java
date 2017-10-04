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
package at.srfg.graphium.io.dto.impl;

import java.util.List;

import at.srfg.graphium.io.dto.IGraphStatusDTO;
import at.srfg.graphium.io.dto.IServerStatusDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author mwimmer
 *
 */
public class ServerStatusDTOImpl implements IServerStatusDTO {

	private String serverName;
	private List<IGraphStatusDTO> graphStatuses;
	private int runningImports;

	public ServerStatusDTOImpl() {
	}

	public ServerStatusDTOImpl(String serverName,
							   List<IGraphStatusDTO> graphStatuses, int runningImports) {
		super();
		this.serverName = serverName;
		this.graphStatuses = graphStatuses;
		this.runningImports = runningImports;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public List<IGraphStatusDTO> getGraphStatuses() {
		return graphStatuses;
	}
	@JsonDeserialize(contentAs = GraphStatusDTOImpl.class)
	public void setGraphStatuses(List<IGraphStatusDTO> graphStatuses) {
		this.graphStatuses = graphStatuses;
	}
	@Override
	public int getRunningImports() {
		return runningImports;
	}
	@Override
	public void setRunningImports(int runningImports) {
		this.runningImports = runningImports;
	}

	@Override
	public String toString() {
		return "ServerStatusDTOImpl{" +
				"serverName='" + serverName + '\'' +
				", graphStatuses=" + graphStatuses +
				", runningImports=" + runningImports +
				'}';
	}

}