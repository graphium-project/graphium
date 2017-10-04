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

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.io.inputformat.IQueuingGraphInputFormat;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @author mwimmer
 *
 */
public class GenericQueuingJacksonGraphInputFormat<T extends IBaseWaySegment> 
		extends GenericQueuingJacksonSegmentInputFormat<T>
		implements IQueuingGraphInputFormat<T> {
	
	public GenericQueuingJacksonGraphInputFormat(
			ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
		super(adapterRegistry);
	}

	protected IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> metadataAdapter;

	/**
	 * @param stream InputStream containing JSON data
	 * @param segmentsQueue Queue retrieving parsed way segment objects
	 * @param metadataQueue Queue retrieving parsed metadata object; there will be retrieved only one object 
	 */
	@Override
	public void deserialize(InputStream stream, BlockingQueue<T> segmentsQueue, 
			BlockingQueue<IWayGraphVersionMetadata> metadataQueue)
			throws WaySegmentDeserializationException {
		
		try {
			JsonParser jp = jsonFactory.createParser(stream);
		
			JsonToken token = jp.nextToken();
			
    		long count = 0;
    		
    		ISegmentAdapter<IBaseSegmentDTO, T> adapter = null;
			while (!jp.isClosed()){
				token = jp.nextToken();
				if (token != null && token != JsonToken.END_ARRAY) {
	
					if (jp.getCurrentToken() == JsonToken.FIELD_NAME &&
						jp.getCurrentName().equals("graphVersionMetadata")) {

						jp.nextToken();
						IGraphVersionMetadataDTO dto = jp.readValueAs(GraphVersionMetadataDTOImpl.class);
						IWayGraphVersionMetadata metadata  = metadataAdapter.adapt(dto);
						metadataQueue.offer(metadata, 500, TimeUnit.MILLISECONDS);
					} 
					// extract type of segments. its encoded as the name of the array field holding the segments
					else if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {    			
	    				String segmentType = jp.getCurrentName();
						adapter = this.getAndRegisterDeserializers(segmentType);
	    			}
					else if (jp.getCurrentToken() != JsonToken.END_OBJECT) {
	    				enqueue(segmentsQueue, processSegment(jp, adapter));
	    				count++;
					}					
				}				
			}
			log.info("parsed and enqueued: " + count + " segments");
		} catch (Exception e) {
			throw new WaySegmentDeserializationException(e.getMessage(),e);
	    }
	}

	public IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> getMetadataAdapter() {
		return metadataAdapter;
	}

	public void setMetadataAdapter(
			IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> metadataAdapter) {
		this.metadataAdapter = metadataAdapter;
	}
	
}