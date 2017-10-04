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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistryAware;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * @author mwimmer
 *
 */
public class GenericJacksonWayGraphOutputFormat<T extends IBaseWaySegment> implements
		IWayGraphOutputFormat<T> {

	protected static Logger log = LoggerFactory.getLogger(GenericJacksonWayGraphOutputFormat.class);
	protected JsonGenerator generator;
	protected IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter;
	protected ISegmentOutputFormat<T> segmentOutputFormat;
	
	protected IWayGraphVersionMetadata metadataToSerialize;

	public GenericJacksonWayGraphOutputFormat(ISegmentOutputFormat<T> segmentOutputFormat, 
			IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter,
			OutputStream stream, 
			JsonGenerator generator)
	{
		this.segmentOutputFormat = segmentOutputFormat;
		this.adapter = adapter;
		if (generator == null) {
			try {
				this.generator = new MappingJsonFactory().createGenerator(stream, JsonEncoding.UTF8);
				this.generator.useDefaultPrettyPrinter();
			} catch (IOException e) {
				log.error("error creating jackson json factory", e);
			}
		} else {
			this.generator = generator;
		}
	}

	@Override
	public void serialize(IWayGraphVersionMetadata metadata) throws WaySegmentSerializationException {
		this.metadataToSerialize = metadata;
    }

	protected void doSerializeMetadata(IWayGraphVersionMetadata metadata) throws WaySegmentSerializationException {
		try {
            generator.writeStartObject();
            doWriteGraphMetadata(metadata);
            generator.flush();
        } catch (IOException e) {
            throw new WaySegmentSerializationException(e.getMessage(), e);
        }
	}
	
	
	@Override
	public void serialize(T segment) throws WaySegmentSerializationException {
		if(metadataToSerialize != null) {
			if(segmentOutputFormat instanceof ISegmentAdapterRegistryAware) {
				String segmentType = ((ISegmentAdapterRegistryAware)segmentOutputFormat).
					getAdapterRegistry().getSegmentDtoType(segment.getClass());
				
				if(metadataToSerialize.getType() == null || !metadataToSerialize.getType().equals(segmentType)) {
					log.info("different segment type in metadata then present based on deserialisation,"
							+ " replacing metadata value with present value: " + segmentType);
					metadataToSerialize.setType(segmentType);
				}
				
			}
			doSerializeMetadata(metadataToSerialize);
			metadataToSerialize = null;
		}
		segmentOutputFormat.serialize(segment);
	}
	
	@Override
	public void close() throws WaySegmentSerializationException {
        try {
        	segmentOutputFormat.close();
        	if(metadataToSerialize != null) {
        		doSerializeMetadata(metadataToSerialize);
        		metadataToSerialize = null;
        	}
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            throw new WaySegmentSerializationException(e.getMessage(), e);
        }
    }

	/**
	 * @param graph
	 * @throws IOException 
	 * @throws JsonGenerationException 
	 */
	private void doWriteGraphMetadata(IWayGraphVersionMetadata metadata) throws JsonGenerationException, IOException {
		IGraphVersionMetadataDTO metadataDto = adapter.adapt(metadata);
		generator.writeObjectField("graphVersionMetadata", metadataDto);
	}
	
	@Override
	public ISegmentOutputFormat<T> getSegmentOutputFormat() {
		return segmentOutputFormat;
	}

	public IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> getAdapter() {
		return adapter;
	}

	public void setAdapter(
			IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter) {
		this.adapter = adapter;
	}
	
}
