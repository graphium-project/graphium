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
package at.srfg.graphium.io.inputformat.impl.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.module.SimpleModule;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.NoSegmentAdapterFoundException;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.io.inputformat.ISegmentInputFormat;
import at.srfg.graphium.model.IBaseSegment;


public abstract class GenericJacksonSegmentInputFormat<T extends IBaseSegment>
		implements ISegmentInputFormat<T> {

	protected static Logger log = LoggerFactory.getLogger(GenericJacksonSegmentInputFormat.class);
	
	protected MappingJsonFactory jsonFactory;
	protected ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry;
	
	public GenericJacksonSegmentInputFormat(ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
		jsonFactory = new MappingJsonFactory();
	}
	
	@Override
	public void close() throws IOException { }

	@Override
	public List<T> deserialize(InputStream stream) throws WaySegmentDeserializationException {
        try {
        	JsonParser jp = jsonFactory.createParser(stream);
    		List<T> segments = new ArrayList<T>();
    		JsonToken token = jp.nextToken();
    		
    		ISegmentAdapter<IBaseSegmentDTO, T> adapter = null;
    		while(!jp.isClosed()){
    			token = jp.nextToken();
    			// extract type of segments. its encoded as the name of the array field holding the segments
    			if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {    			
    				String segmentType = jp.getCurrentName();
    				adapter = this.getAndRegisterDeserializers(segmentType);
    			}
    			else if(token != null && token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT) {
    				segments.add(processSegment(jp, adapter));
    			}
    		}

    		log.info("parsed: " + segments.size() + " segments");
    		return segments;
        } catch (Exception e) {
            throw new WaySegmentDeserializationException(e.getMessage(),e);
        }
    }

    protected ISegmentAdapter<IBaseSegmentDTO, T> getAndRegisterDeserializers(String segmentType) throws NoSegmentAdapterFoundException {
		ISegmentAdapter<IBaseSegmentDTO, T> adapter = adapterRegistry.getAdapterForType(segmentType);
		//Create custom deserializer for XInfos
		SimpleModule module = new SimpleModule();
		module.addDeserializer(IConnectionXInfoDTO.class, new ConnectionXInfoDeserializer(adapter));
		module.addKeyDeserializer(String.class, new XInfoKeyDeserializer());
		module.addDeserializer(ISegmentXInfoDTO.class, new SegmentXInfoDeserializer(adapter));
		jsonFactory.getCodec().registerModule(module);
		return adapter;
	}
	
	protected T processSegment(JsonParser jp,
			ISegmentAdapter<IBaseSegmentDTO, T> adapter) throws IOException {
		if(jp.currentToken() != JsonToken.START_OBJECT) {
			jp.nextValue();
		}
		IBaseSegmentDTO dto = jp.readValueAs(adapter.getDtoClass());
		return adapter.adaptReverse(dto);
	}

	public ISegmentAdapterRegistry<IBaseSegmentDTO, T> getAdapterRegistry() {
		return adapterRegistry;
	}

	public void setAdapterRegistry(
			ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
	}
}