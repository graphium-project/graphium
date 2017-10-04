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
package at.srfg.graphium.io.container;

import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.container.IWaySegmentContainer;


/**
 * Extended Container, allows segments to get written to an configuired output format.
 * 
 * @author <a href="mailto:andreas.wagner@salzburgresearch.at">Andreas Wagner</a>
 *
 */
public interface ISerializingWaySegmentContainer<T extends IBaseSegment> extends IWaySegmentContainer<T> {

	void setSegmentOutputFormat(ISegmentOutputFormat<T> outputFormat);

	void serialize(T segment) throws WaySegmentSerializationException;

	
}
