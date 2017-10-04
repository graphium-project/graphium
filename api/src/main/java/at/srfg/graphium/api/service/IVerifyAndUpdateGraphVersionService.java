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
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.service;

import java.util.List;

/**
 * @author User
 */
public interface IVerifyAndUpdateGraphVersionService {
	
	/**
	 * Verifies if current graph version is out of date and has to be updated. Optionally 
	 * graph versions could be updated automatically.
	 * Works with REST-Call requesting to the central graph server.
	 * @param updateAutomatically if set to true graph versions will be updated automatically (if needed)
	 * @return a list of String representing the graph names that are outdated
	 */
	public List<String> verifyAndUpdateGraphVersion(boolean updateAutomatically);

}
