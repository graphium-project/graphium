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
package at.srfg.graphium.io.inputformat;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 * @param <T>
 */
public interface IQueuingGraphInputFormat<T extends IBaseWaySegment> extends IQueuingSegmentInputFormat<T> {

	/**
	 * @param stream InputStream containing JSON data 
	 * @param segmentsQueue Queue retrieving parsed way segment objects
	 * @param metadataQueue Queue retrieving parsed metadata object; there will be retrieved only one object 
	 */
	public abstract void deserialize(InputStream stream,
			BlockingQueue<T> segmentsQueue,
			BlockingQueue<IWayGraphVersionMetadata> metadataQueue)
			throws WaySegmentDeserializationException;

}