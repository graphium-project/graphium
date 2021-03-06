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
package at.srfg.graphium.model;

import java.util.List;
import java.util.Map;

import at.srfg.graphium.model.container.IWaySegmentContainer;
import at.srfg.graphium.model.processor.ISegmentProcessor;

public interface IBaseGraphModelFactory<T extends IBaseSegment> {

	public T newSegment();
	
	public T newSegment(long id, Map<String, List<ISegmentXInfo>> xInfo);
	
	public T newSegment(long id, Map<String, List<ISegmentXInfo>> xInfo,
			List<IWaySegmentConnection> connections);

	/**
	 * create a new way segment container
	 * @return empty way segment container object
	 */
	public IWaySegmentContainer<T> newWaySegmentContainer();
	
	/**
	 * create new way segment container with given processors linked to execute when a segment is added
	 * 
	 * @param segmentProcessors processor object to execute when object is added to the container
	 * @return way segment container with given segment processors linked
	 */
	public IWaySegmentContainer<T> newWaySegmentContainer(List<ISegmentProcessor<T>> segmentProcessors);

}