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
package at.srfg.graphium.model.container;

import java.util.List;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.processor.ISegmentProcessor;

/**
 * Container for transfer and output of street segment collections
 * allows setting an @ISegmentOutputFormat to let the container use different serialisation mechanisms
 * to transfer data to a client 
 * 
 * @author <a href="mailto:andreas.wagner@salzburgresearch.at">Andreas Wagner</a>
 *
 */
public interface IWaySegmentContainer<T extends IBaseSegment>{

	/**
	 * get stored street segments (if any) none are loaded returns null.
	 *  
	 * @return contained street segments or null
	 */
	List<T> getSegments();
	
	/**
	 * setter for street segments
	 * 
	 * @param segments the segment list to set
	 */
	void setSegments(List<T> segments);
	
	/**
	 * add a segment on the end of the stored segment list
	 * 
	 * @param segment the segment to add
	 */
	void addSegment(T segment);
	
	/**
	 * setter for a list of processors enhancing or altering attributes of the segment before seralisation
	 * 
	 * @param segmentProcessors list of processors to apply, order in list is the list the processors are applied
	 */
	void setSegmentProcessors(List<ISegmentProcessor<T>> segmentProcessors);

	/**
	 * getter for a list of processors enhancing or altering attributes of the segment before seralisation
	 * 
	 * @return list of processors to apply, order in list is the list the processors are applied
	 */
	List<ISegmentProcessor<T>> getSegmentProcessors();
}
