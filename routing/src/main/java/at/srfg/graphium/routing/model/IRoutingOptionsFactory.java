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
package at.srfg.graphium.routing.model;

import org.springframework.util.MultiValueMap;

import at.srfg.graphium.routing.exception.RoutingParameterException;


public interface IRoutingOptionsFactory<O extends IRoutingOptions> {
	
	public final static String PARAM_TIMESTAMP = "timestamp";
	public final static String PARAM_ALGO = "algo";
	public final static String PARAM_MODE = "mode";
	public final static String PARAM_CRITERIA = "criteria";
	public final static String PARAM_SEARCHDISTANCE = "searchDistance";
	public final static String PARAM_TIMEOUT = "timeout";
	
	public O newRoutingOptions(String graphName, String graphVersion, 
			String coordsString,
			MultiValueMap<String, String> allParams) throws RoutingParameterException;
}
