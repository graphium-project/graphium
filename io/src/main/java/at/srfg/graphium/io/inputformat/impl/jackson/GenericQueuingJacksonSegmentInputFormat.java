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

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.io.inputformat.IQueuingSegmentInputFormat;
import at.srfg.graphium.model.IBaseSegment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class GenericQueuingJacksonSegmentInputFormat<T extends IBaseSegment> 
	extends GenericJacksonSegmentInputFormat<T> implements IQueuingSegmentInputFormat<T> {

	public GenericQueuingJacksonSegmentInputFormat(
			ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
		super(adapterRegistry);
	}

	@Override
	public void deserialize(InputStream stream, BlockingQueue<T> outputQueue)
			throws WaySegmentDeserializationException {
		try {
        	JsonParser jp = jsonFactory.createParser(stream);    	
    		JsonToken token = jp.nextToken();
    		long count = 0;
    		
    		ISegmentAdapter<IBaseSegmentDTO, T> adapter = null;
    		while(!jp.isClosed()){
    			token = jp.nextToken();
    			// extract type of segments. its encoded as the name of the array field holding the segments
    			if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {    			
    				String segmentType = jp.getCurrentName();
					adapter = this.getAndRegisterDeserializers(segmentType);
    			}
    			else if(token != null && token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT) {
    				enqueue(outputQueue, processSegment(jp, adapter));
    				count++;
    			}
    		}
    		
    		log.info("parsed and enqueued: " + count + " segments");
        } catch (Exception e) {
            throw new WaySegmentDeserializationException(e.getMessage(),e);
        }		
	}

	protected void enqueue(BlockingQueue<T> outputQueue, T segment) throws InterruptedException {
		boolean enqueued = false;
		
		while(!enqueued) {
			enqueued = outputQueue.offer(segment, 500, TimeUnit.MILLISECONDS);
		}	
	}

}
