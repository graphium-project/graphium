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

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonGenerator;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormatFactory;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 */
public class GenericJacksonWayGraphOutputFormatFactoryImpl<T extends IBaseWaySegment>
		implements IWayGraphOutputFormatFactory<T> {

	private ISegmentOutputFormatFactory<T> segmentOutputFormatFactory;
	private IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter;
	
	public GenericJacksonWayGraphOutputFormatFactoryImpl(ISegmentOutputFormatFactory<T> segmentOutputFormatFactory,
			IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter) {
		this.segmentOutputFormatFactory = segmentOutputFormatFactory;
		this.adapter = adapter;
	}

	@Override
	public IWayGraphOutputFormat<T> getWayGraphOutputFormat(OutputStream stream) throws IOException {
		ISegmentOutputFormat<T> segmentOutputFormat = segmentOutputFormatFactory.getSegmentOutputFormat(stream);
		JsonGenerator generator = ((GenericJacksonSegmentOutputFormat<T>)segmentOutputFormat).getGenerator();
		((GenericJacksonSegmentOutputFormat<T>)segmentOutputFormat).setWrapInObject(false);
		return new GenericJacksonWayGraphOutputFormat<T>(segmentOutputFormat, adapter, stream, generator);
		//return new GenericJacksonWayGraphOutputFormat<T>(stream, getSegmentOutputFormat(stream, generator), generator);
	}
	
}