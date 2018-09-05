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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.NoSegmentAdapterFoundException;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistryAware;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;

public class GenericJacksonSegmentOutputFormat<T extends IBaseSegment>
	implements ISegmentOutputFormat<T>, ISegmentAdapterRegistryAware<T> {

	protected static Logger log = LoggerFactory.getLogger(GenericJacksonSegmentOutputFormat.class);
	
	protected ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> adapterRegistry;
	
	protected JsonGenerator generator;

	protected int processed = 0;
	protected int flushBatchCount = 100;
	protected boolean started = false;
	protected boolean wrapInObject = true;
	
	public GenericJacksonSegmentOutputFormat(ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> adapterRegistry, 
			OutputStream stream, JsonGenerator generator)
	{
		this.adapterRegistry = adapterRegistry;
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
	
	public GenericJacksonSegmentOutputFormat(ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> adapterRegistry,
			OutputStream stream, JsonGenerator generator, int flushBatchCount)
	{
		this.adapterRegistry = adapterRegistry;
		if (generator == null) {
			try {
				this.generator = new MappingJsonFactory().createGenerator(new BufferedOutputStream(stream), JsonEncoding.UTF8);
				this.generator.useDefaultPrettyPrinter();
			} catch (IOException e) {
				log.error("error creating jackson json factory", e);
			}
		}
		if (flushBatchCount > 0 ) {
			this.flushBatchCount = flushBatchCount;
		} else {
			log.warn("flushBatchCount ignored, can not be negative or 0");
		}
	}
	
	@Override
	public void serialize(T segment) throws WaySegmentSerializationException {
        try {
        	ISegmentAdapter<? extends IBaseSegmentDTO, T> adapter =  adapterRegistry.getAdapterForModal((Class<T>) segment.getClass());
            IBaseSegmentDTO dto = adapter.adapt(segment);
               
        	// 	write start array on first segment
            if(!started) {
            	if(this.wrapInObject) {
            		generator.writeStartObject();            	
            	}
            	generator.writeFieldName(dto.getSegmentType());
                generator.writeStartArray();
                started = true;
            }
         
            generator.writeObject(dto);
            processed++;

            if(processed % flushBatchCount == 0) {
                generator.flush();
            }
        } catch (IOException e) {
            throw new WaySegmentSerializationException(e.getMessage(),e);
        } catch (NoSegmentAdapterFoundException e) {
			log.error("no segment adapter found",e);
		}
    }
	
	

	@Override
	public void close() throws WaySegmentSerializationException
	{
		try {
			if(processed > 0) {
				generator.writeEndArray();
				if(this.wrapInObject) {
					generator.writeEndObject();
				}			
			}	
			generator.flush();
		} catch (IOException e) {
            throw new WaySegmentSerializationException(e.getMessage(), e);
        }
	}
	
	@Override
	public ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> getAdapterRegistry() {
		return adapterRegistry;
	}

	
	public JsonGenerator getGenerator() {
		return generator;
	}
	
	public boolean isWrapInObject() {
		return wrapInObject;
	}

	public void setWrapInObject(boolean wrapInObject) {
		this.wrapInObject = wrapInObject;
	}
}
