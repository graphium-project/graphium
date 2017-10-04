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
package at.srfg.graphium.io.producer.impl;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.inputformat.IQueuingSegmentInputFormat;
import at.srfg.graphium.io.producer.IBaseSegmentProducer;
import at.srfg.graphium.model.IBaseSegment;

/**
 * @author anwagner
 *
 */
public class BaseSegmentProducerImpl<T extends IBaseSegment> implements IBaseSegmentProducer<T> {

	protected static Logger log = LoggerFactory.getLogger(BaseSegmentProducerImpl.class);
	
	protected IQueuingSegmentInputFormat<T> inputFormat;
	protected BlockingQueue<T> segmentsQueue;
	protected InputStream stream;
	protected Throwable exception; 

	public BaseSegmentProducerImpl(IQueuingSegmentInputFormat<T> inputFormat, 
			InputStream stream,
			BlockingQueue<T> segmentsQueue) {
		this.inputFormat = inputFormat;
		this.stream = stream;
		this.segmentsQueue = segmentsQueue;
	}
	
	@Override
	public void run() {
		try {
			inputFormat.deserialize(stream, segmentsQueue);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	@Override
	public Throwable getException() {
		return exception;
	}
	
	protected void handleException(Exception e) {
		log.error("deserialization error",e);
		this.exception = e;
	}
	
}
