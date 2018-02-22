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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityProcessor;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.osmimport.helper.WayHelper;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 */
public class SegmentationNodesSink implements SinkSource, EntityProcessor {
	
	private static Logger log = LoggerFactory.getLogger(SegmentationNodesSink.class);

	private Sink sink = null;
	
	private TLongObjectHashMap<List<WayRef>> wayRefs = new TLongObjectHashMap<List<WayRef>>();
	
	private int wayCount = 0;
	private int nodeCount = 0;
	
	// TODO: consider barriers; discuss if we should treat them as segmentation points
	
	@Override
	public void initialize(Map<String, Object> arg0) {
	}

	public SegmentationNodesSink() {}
			
	public SegmentationNodesSink(TLongObjectHashMap<List<WayRef>> wayRefs) {
		super();
		this.wayRefs = wayRefs;
	}

	@Override
	public void complete() {
		int segmentationNodeCount = 0;
		int endNodeCount = 0;
		for (Object obj : wayRefs.values()) {
			List<WayRef> wayRefList = (List<WayRef>) obj;
			for (WayRef wayRef : wayRefList) {
				if (wayRef.getType() == (byte)1) {
					endNodeCount++;
					} else if (wayRef.getType() == (byte)2) {
					segmentationNodeCount++;
				}
			}
		}

		log.info("total way count: " + wayCount + " ways");
		log.info("total node count: " + wayRefs.size() + " nodes");
		log.info("segmentationNodeCount: " + segmentationNodeCount);
		log.info("endNodeCount: " + endNodeCount);
		
	}

	@Override
	public void release() {
	}

	@Override
	public void process(EntityContainer entityContainer) {
		entityContainer.process(this);
		
		if (sink != null) {
			sink.process(entityContainer);
		}
	}

	@Override
	public void process(BoundContainer bound) {
	}

	@Override
	public void process(NodeContainer node) {
		if (nodeCount == 0) {
			log.info("processing nodes...");
		}
		nodeCount++;
		if (nodeCount > 0 && nodeCount % 10000000 == 0) {
			log.info(nodeCount + " nodes");
		}
	}

	@Override
	public void process(WayContainer wayContainer) {
		if (wayCount == 0) {
			log.info(nodeCount + " nodes processed");
			log.info("processing ways...");
		}
		Way way = wayContainer.getEntity();
		way = WayHelper.preprocessWay(way);
			
		long firstNodeId = -1;
		if (way != null) {
			for (WayNode node : way.getWayNodes()) {
				long nodeId = node.getNodeId();

				if (firstNodeId != nodeId) { // ignore duplicate nodeIds on same way (e.g. roundabout)
					
					byte type = 1;
					if (nodeId == way.getWayNodes().get(0).getNodeId() ||
						nodeId == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()) {
						type = 0;
					}
					List<WayRef> wayRefList = wayRefs.get(nodeId);
					byte oneway = WayHelper.checkOneway(WayHelper.createTagMap(way));
					WayRef wayRef = new WayRef(way.getId(), type, oneway, way.getWayNodes().get(0).getNodeId(), 
												way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId());
					if (wayRefList == null) {
						wayRefList = new ArrayList<WayRef>();
						wayRefList.add(wayRef);
						wayRefs.put(nodeId, wayRefList);
					} else {
						wayRefList.add(wayRef);
						// this node is referenced by more than one way; so it is potentially a segmentation node;
						// => update type of referencing way (IDs)
						for (WayRef ref : wayRefList) {
							if (ref.getType() > 0) {
								ref.setType((byte)2); // 2 ... node is a segmentation node for this way
							}
						}
					}
				}
				if (firstNodeId == -1) {
					firstNodeId = nodeId;
				}

			}
		}
			
		wayCount++;
	}

	@Override
	public void process(RelationContainer relation) {
	}

	public TLongObjectHashMap<List<WayRef>> getWayRefs() {
		return wayRefs;
	}

	public int getWayCount() {
		return wayCount;
	}

	@Override
	public void setSink(Sink sink) {
	}

}