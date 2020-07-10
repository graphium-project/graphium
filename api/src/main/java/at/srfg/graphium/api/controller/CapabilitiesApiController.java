/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.srfg.graphium.core.capabilities.ICapability;

/**
 * All defined capabilities can be requested by this capabilities API. 
 * 
 * @author mwimmer
 *
 */
@Controller
public class CapabilitiesApiController {

	@Autowired
	private List<ICapability> capabilities = null;
	
	@RequestMapping(value = "/capabilities", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getCapabilities() {
		List<String> allCaps = new ArrayList<>();
		
		if (capabilities != null) {
			for (ICapability cap : capabilities) {
				allCaps.add(cap.getCapability());
			}
		}
		
		return allCaps;
	}
	
	@RequestMapping(value = "/capabilities/{capability}", method = RequestMethod.GET)
	@ResponseBody
	public String getCapabilities(@PathVariable(value = "capability") String capability) {
		
		if (capabilities != null) {
			for (ICapability cap : capabilities) {
				if (capability.equals(cap.getCapability())) {
					return "true";
				}
			}
		}
		
		return "false";
	}

}