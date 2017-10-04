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
package at.srfg.graphium.gipimport.producer.impl;

import java.util.concurrent.BlockingQueue;

import at.srfg.graphium.gipimport.model.IDFMetadata;
import at.srfg.graphium.gipimport.model.IImportConfig;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.producer.IGipLinkProducer;
import at.srfg.graphium.model.IBaseSegment;


public class GipLinkProducerImpl<T extends IBaseSegment> implements IGipLinkProducer<T> {
	
	private BlockingQueue<T> queue;
	
	private IImportConfig config;
	private IDFMetadata metadata;
	
	private IGipParser<T> parser;

	public GipLinkProducerImpl(IGipParser<T> parser) {
		this.parser = parser;
	}
	
	@Override
	public Thread produceLinks(BlockingQueue<T> queue,
			   final IImportConfig config,
			   final IDFMetadata metadata) {		
		this.queue = queue;
		this.metadata = metadata;
		this.config = config;
		Thread parserThread = new Thread(this);
		parserThread.setName("Gip Link Producer");
		parserThread.start();
		return parserThread;
	}

	@Override
	public void run() {		
		this.parser.parseGip(queue, config, metadata);
	}

	@Override
	public boolean isReady() {
		return parser.isReady();
	}

}
