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
package at.srfg.graphium.io.outputformat.hd.impl.jackson;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormatFactory;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonSegmentOutputFormat;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDRoadInfrastructure;
import at.srfg.graphium.model.hd.IHDWaySegment;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author mwimmer
 *
 */
public class GenericJacksonHdWayGraphOutputFormatFactoryImpl<T extends IHDWaySegment>
		implements IHdWayGraphOutputFormatFactory<T> {

	private ISegmentOutputFormatFactory<T> segmentOutputFormatFactory;
	// TODO generic for area
	private ISegmentOutputFormatFactory<IHDArea> areaOutputFormatFactory;
	private ISegmentOutputFormatFactory<IHDRoadInfrastructure> roadInfraOutputFormatFactory;
	private IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter;

	public GenericJacksonHdWayGraphOutputFormatFactoryImpl(
			ISegmentOutputFormatFactory<T> segmentOutputFormatFactory,
			ISegmentOutputFormatFactory<IHDArea> areaOutputFormatFactory,
			ISegmentOutputFormatFactory<IHDRoadInfrastructure> roadInfraOutputFormatFactory,
			IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter) {
		this.segmentOutputFormatFactory = segmentOutputFormatFactory;
		this.areaOutputFormatFactory = areaOutputFormatFactory;
		this.roadInfraOutputFormatFactory = roadInfraOutputFormatFactory;
		this.adapter = adapter;
	}

	@Override
	public IHdWayGraphOutputFormat<T> getWayGraphOutputFormat(OutputStream stream) throws IOException {
		ISegmentOutputFormat<T> segmentOutputFormat = segmentOutputFormatFactory.getSegmentOutputFormat(stream);
		JsonGenerator generator = ((GenericJacksonSegmentOutputFormat<T>)segmentOutputFormat).getGenerator();
		((GenericJacksonSegmentOutputFormat<T>)segmentOutputFormat).setWrapInObject(false);

		ISegmentOutputFormat<IHDArea> areaOutputFormat = areaOutputFormatFactory.getSegmentOutputFormat(stream, generator);
		((GenericJacksonSegmentOutputFormat<IHDArea>)areaOutputFormat).setWrapInObject(false);

		ISegmentOutputFormat<IHDRoadInfrastructure> roadInfraOutputFormat = roadInfraOutputFormatFactory.getSegmentOutputFormat(stream, generator);
		((GenericJacksonSegmentOutputFormat<IHDRoadInfrastructure>)roadInfraOutputFormat).setWrapInObject(false);

		return new GenericJacksonHdWayGraphOutputFormat<T>(segmentOutputFormat, areaOutputFormat, roadInfraOutputFormat, adapter, stream, generator);
	}
	
}