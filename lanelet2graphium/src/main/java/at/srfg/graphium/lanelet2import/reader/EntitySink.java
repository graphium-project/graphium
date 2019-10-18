/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.lanelet2import.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityProcessor;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class EntitySink implements Sink, EntityProcessor {

	private static Logger log = LoggerFactory.getLogger(EntitySink.class);
	
	private TLongObjectHashMap<Node> nodes = new TLongObjectHashMap<Node>();
	private TLongObjectHashMap<Way> ways = new TLongObjectHashMap<Way>();
	private List<Relation> relations = new ArrayList<Relation>();

	@Override
	public void initialize(Map<String, Object> metaData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void complete() {
		log.info("Parsed " + nodes.size() + " nodes");
		log.info("Parsed " + ways.size() + " ways");
		log.info("Parsed " + relations.size() + " relations");
	}

	@Override
	public void release() {
	}

	@Override
	public void process(BoundContainer bound) {
	}

	@Override
	public void process(NodeContainer node) {
		process(node.getEntity());
	}

	@Override
	public void process(WayContainer way) {
		process(way.getEntity());
	}

	@Override
	public void process(RelationContainer relation) {
		process(relation.getEntity());
	}

	@Override
	public void process(EntityContainer entityContainer) {
		if (entityContainer instanceof NodeContainer) {
			process(((NodeContainer)entityContainer).getEntity());
		} else if (entityContainer instanceof WayContainer) {
			process(((WayContainer)entityContainer).getEntity());
		} else if (entityContainer instanceof RelationContainer) {
			process(((RelationContainer)entityContainer).getEntity());
		}
	}
	
	private void process(Node node) {
		nodes.put(node.getId(), node);
	}

	private void process(Way way) {
		ways.put(way.getId(), way);
	}

	private void process(Relation relation) {
		relations.add(relation);
	}

	public TLongObjectHashMap<Node> getNodes() {
		return nodes;
	}

	public TLongObjectHashMap<Way> getWays() {
		return ways;
	}

	public List<Relation> getRelations() {
		return relations;
	}

}
