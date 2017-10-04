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
package at.srfg.graphium.io.outputformat.impl.jackson;

import java.io.OutputStream;

import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.model.IBaseSegment;

public class GenericJacksonSegmentOutputFormatFactoryImpl<T extends IBaseSegment>
	implements ISegmentOutputFormatFactory<T> {
	
	protected ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> adapterRegistry;
	
	public GenericJacksonSegmentOutputFormatFactoryImpl(ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
	}
	
	@Override
	public ISegmentOutputFormat<T> getSegmentOutputFormat(OutputStream stream) {
		return new GenericJacksonSegmentOutputFormat<T>(adapterRegistry, stream, null);
	}

	@Override
	public ISegmentOutputFormat<T> getSegmentOutputFormat(OutputStream stream,
			int flushBatchCount) {
		return new GenericJacksonSegmentOutputFormat<T>(adapterRegistry, stream, null, flushBatchCount);
	}

}
