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
package at.srfg.graphium.io.adapter.registry.impl;

import java.util.List;

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.model.ISegmentXInfo;

/**
 * Registry for Segment XInfo adapters
 *
 * Created by shennebe on 22.09.2016.
 */
public class SegmentXInfoAdapterRegistry<T extends ISegmentXInfo,S extends ISegmentXInfoDTO>
    extends GenericXInfoAdapterRegistry<T,S> {

	public SegmentXInfoAdapterRegistry() { super(); }
	
	public SegmentXInfoAdapterRegistry(List<IXInfoDTOAdapter<T,S>> adapters) {
		super(adapters);
	}
	
}
