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
package at.srfg.graphium.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.srfg.graphium.api.service.IServerStatusService;
import at.srfg.graphium.io.dto.IServerStatusDTO;

/**
 * @author mwimmer
 *
 */
@Controller
public class MaintenanceApiController  {

	private IServerStatusService serverStatusService;

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	@ResponseBody
	public IServerStatusDTO getStatus() {
		return serverStatusService.getStatus();
	}

	public IServerStatusService getServerStatusService() {
		return serverStatusService;
	}

	public void setServerStatusService(IServerStatusService serverStatusService) {
		this.serverStatusService = serverStatusService;
	}
}