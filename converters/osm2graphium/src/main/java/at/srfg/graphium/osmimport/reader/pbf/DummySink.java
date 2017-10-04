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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.osmimport.reader.pbf;

import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mwimmer
 */
public class DummySink implements Sink {
	
	private static Logger log = LoggerFactory.getLogger(DummySink.class);

	private int boundCount = 0;
	private int nodeCount = 0;
	private int wayCount = 0;
	private int relationCount = 0;
	
	@Override
	public void initialize(Map<String, Object> arg0) {
	}

	@Override
	public void complete() {
		log.info("Bounds: " + boundCount);
		log.info("Nodes: " + nodeCount);
		log.info("Relations: " + relationCount);
		log.info("Ways: " + wayCount);
	}

	@Override
	public void release() {
	}

	@Override
	public void process(EntityContainer entityContainer) {
		Entity entity = entityContainer.getEntity();
		if (entity instanceof Bound) {
			boundCount++;
		} else if (entity instanceof Node) {
			nodeCount++;
		} else if (entity instanceof Relation) {
			relationCount++;
		} else if (entity instanceof Way) {
			wayCount++;
		}
	}

}
