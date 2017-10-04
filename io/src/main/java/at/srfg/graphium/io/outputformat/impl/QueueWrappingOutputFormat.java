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
package at.srfg.graphium.io.outputformat.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;

public class QueueWrappingOutputFormat<T extends IBaseSegment> implements
		ISegmentOutputFormat<T> {

	protected static Logger log = LoggerFactory.getLogger(QueueWrappingOutputFormat.class);
	
	boolean closed = false;
	private BlockingQueue<T> queue;
	
	public QueueWrappingOutputFormat(BlockingQueue<T> queue) {
		this.queue = queue;
	}
	
	@Override
	public void serialize(T segment) throws WaySegmentSerializationException {		
		boolean enqueued = false;
		while(!enqueued) {
			try {
				enqueued = queue.offer(segment, 500, TimeUnit.MILLISECONDS);
				if(!enqueued){
					try  {
						log.info("waiting");
						Thread.sleep(2500);
					} catch (Exception e) {
						
					}
				}
			} catch (InterruptedException e) {
				log.error("error enqueued data", e);
			}
		}		
	}

	@Override
	public void close() throws WaySegmentSerializationException {
		closed = true;
	}

}
