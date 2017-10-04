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
package at.srfg.graphium.model.impl;

import java.util.List;

import at.srfg.graphium.model.IBaseGraphModelFactory;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.container.IWaySegmentContainer;
import at.srfg.graphium.model.container.impl.WaySegmentContainerImpl;
import at.srfg.graphium.model.processor.ISegmentProcessor;

public abstract class AbstractGraphModelFactory<T extends IBaseSegment> implements IBaseGraphModelFactory<T> {

	@Override
	public IWaySegmentContainer<T> newWaySegmentContainer() {
		return new WaySegmentContainerImpl<T>();
	}

	@Override
	public IWaySegmentContainer<T> newWaySegmentContainer(
			List<ISegmentProcessor<T>> segmentProcessors) {
		return new WaySegmentContainerImpl<T>();
	}
}
