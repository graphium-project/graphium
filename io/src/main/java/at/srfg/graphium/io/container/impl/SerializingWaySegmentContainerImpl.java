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
package at.srfg.graphium.io.container.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.container.ISerializingWaySegmentContainer;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.container.impl.AbstractWaySegmentContainerImpl;
import at.srfg.graphium.model.processor.ISegmentProcessor;

public abstract class SerializingWaySegmentContainerImpl<T extends IWaySegment>
		extends AbstractWaySegmentContainerImpl<T> implements
		ISerializingWaySegmentContainer<T> {

	protected static Logger log = LoggerFactory
			.getLogger(SerializingWaySegmentContainerImpl.class);

	private ISegmentOutputFormat<T> outFormat;

	public SerializingWaySegmentContainerImpl() {}

	public SerializingWaySegmentContainerImpl(
			List<ISegmentProcessor<T>> segmentProcessors) {
		this.segmentProcessors = segmentProcessors;
	}

	public SerializingWaySegmentContainerImpl(ISegmentOutputFormat<T> outFormat) {
		this.outFormat = outFormat;
	}

	public SerializingWaySegmentContainerImpl(
			List<ISegmentProcessor<T>> segmentProcessors,
			ISegmentOutputFormat<T> outFormat) {
		this.segmentProcessors = segmentProcessors;
		this.outFormat = outFormat;
	}

	@Override
	public void setSegmentOutputFormat(ISegmentOutputFormat<T> outputFormat) {
		this.outFormat = outputFormat;
	}

	@Override
	public void serialize(T segment) throws WaySegmentSerializationException {
		if (outFormat == null) {
			log.warn("tried to serializing segments but outputformat was null");
		} else {
			segment = executeProcessors(segment);
			outFormat.serialize(segment);
		}
	}

}
