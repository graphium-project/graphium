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
package at.srfg.graphium.model.processor;

import java.util.List;
import java.util.Map;

import at.srfg.graphium.model.IWaySegment;

/**
 * Interface for a processor working on a segment. In addition to @link ISegmentProcessor parameters are supported
 * interfaces enforces methods to define requried and supported parameters, how to convert them to typed 
 * parameters and a method to call the process method 
 *  
 * @author anwagner
 *
 * @param <T>
 */
public interface IParameterizedSegmentProcessor<T extends IWaySegment> extends ISegmentProcessor<T> {

	/**
	 * Returns a list of String which contains all names of parameters the query accepts.
	 * @return list of String with all accepted parameter names.
	 */
	public List<String> getPossibleParams();
	
	/**
	 * Returns a list of String which contains all names of parameters the query needs to can be executed.
	 * @return list of String with all required parameter names.
	 */
	public List<String> getRequiredParams();

	/**
	 * setter for required Parameters
	 * @param requiredParams List of all required parameters of the processor (keys in parameter map)
	 */
	public void setRequiredParams(List<String> requiredParams);

	/**
	 * setter for all Parameters possible in this processor
	 * @param possibleParams List of all possible parameters of the processor (keys in parameter map) (including required!)
	 */
	public void setPossibleParams(List<String> possibleParams);
	
	/**
	 * method enforces implementations to configure the required and possible parameters of the processor instance
	 * TODO: required?
	 */
	public void defineParameters();
	
	public T process(T segment, Map<String, Object> params);
	
	// TODO: Exception if not valid
	public Map<String, Object> validateAndTypeParams(Map<String, Object> params);
}
