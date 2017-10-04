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

import at.srfg.graphium.model.IBaseSegment;

public interface ISegmentProcessor<T extends IBaseSegment> {

	/**
	 * Returns the ID of this <code>IDataSource</code>
	 * @return The ID of this dataSource 
	 */
	public String getId();
	
	/**
	 * Sets the ID of this dataSource.
	 * @param Id
	 */
	public void setId(String Id);
	
	/**
	 * return a readable name for this processor
	 * 
	 * @return the readable name describing this processor
	 */
	public String getReadableName();
	
	/**
	 * Method to get a description about the behaviour of this processor bean
	 * 
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Setter for the flag indicating external visibility (e.g. over an API) of the component
	 * 
	 * @param externalAccessable true if visible external
	 */
	public void setExternalAccessable(boolean externalAccessable);
	
	/**
	 * Getter for the flag indicating external visibility (e.g. over an API) of the component
	 * 
	 * @return true if visible external
	 */
	public boolean isExternalAccessable();

	public T process(T segment);
	
}
