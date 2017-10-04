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
package at.srfg.graphium.model.container.impl;

import java.util.List;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.container.IWaySegmentContainer;
import at.srfg.graphium.model.processor.ISegmentProcessor;

public abstract class AbstractWaySegmentContainerImpl<T extends IBaseSegment> implements IWaySegmentContainer<T> {

	protected List<ISegmentProcessor<T>> segmentProcessors;
	
	public AbstractWaySegmentContainerImpl() {}
	
	public AbstractWaySegmentContainerImpl(List<ISegmentProcessor<T>> segmentProcessors)
	{
		this.segmentProcessors = segmentProcessors;
	}
	
	protected T executeProcessors(T segment) {
		if(segmentProcessors != null) {
			for(ISegmentProcessor<T> segmentProcessor : segmentProcessors) {
				segment = segmentProcessor.process(segment);
			}
		}
		return segment;
	}

	@Override
	public void setSegmentProcessors(List<ISegmentProcessor<T>> segmentProcessors) {
		this.segmentProcessors = segmentProcessors;
	}

	@Override
	public List<ISegmentProcessor<T>> getSegmentProcessors() {
		return segmentProcessors;
	}
}
