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
package at.srfg.graphium.osmimport.reader.pbf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityProcessor;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.osmimport.adapter.Way2WaySegmentAdapter;
import at.srfg.graphium.osmimport.connections.ConnectionsBuilder;
import at.srfg.graphium.osmimport.helper.WayHelper;
import at.srfg.graphium.osmimport.model.impl.NodeCoord;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class WaySink implements Sink, EntityProcessor {
	
	private static Logger log = LoggerFactory.getLogger(WaySink.class);
	
	private TLongObjectHashMap<List<WayRef>> wayRefs = new TLongObjectHashMap<List<WayRef>>();
	private TLongObjectHashMap<List<Relation>> wayRelations = new TLongObjectHashMap<>();
	private TLongObjectHashMap<TLongArrayList> waysToSegment = new TLongObjectHashMap<>();
	private TLongObjectHashMap<NodeCoord> nodes = new TLongObjectHashMap<>();
	private TLongObjectHashMap<List<IWaySegment>> segmentedWaySegments = new TLongObjectHashMap<>();
	private BlockingQueue<IWaySegment> waysQueue;
    private Way2WaySegmentAdapter wayAdapter = new Way2WaySegmentAdapter();
    private Set<Access> defaultAccesses = null;
    private ConnectionsBuilder connectionsBuilder;
	
	public WaySink(TLongObjectHashMap<List<WayRef>> wayRefs, TLongObjectHashMap<NodeCoord> nodes, 
			TLongObjectHashMap<List<IWaySegment>> segmentedWaySegments, TLongObjectHashMap<List<Relation>> wayRelations,
			BlockingQueue<IWaySegment> waysQueue) {
		this.wayRefs = wayRefs;
		this.nodes = nodes;
		this.wayRelations = wayRelations;
		this.segmentedWaySegments = segmentedWaySegments;
		for (long nodeId : wayRefs.keys()) {
			List<WayRef> wayRefList = wayRefs.get(nodeId);
			for (WayRef wayRef : wayRefList) {
				if (wayRef.getType() == 2) {
					if (waysToSegment.get(wayRef.getWayId()) == null) {
						waysToSegment.put(wayRef.getWayId(), new TLongArrayList(new long[] {nodeId}));
					} else {
						waysToSegment.get(wayRef.getWayId()).add(nodeId);
					}
				}
			}
		}
		
		this.waysQueue = waysQueue;
		this.connectionsBuilder = new ConnectionsBuilder();
	}

	@Override
	public void initialize(Map<String, Object> metaData) {
		defaultAccesses = Access.getAccessTypes(new int[]{3});
	}

	@Override
	public void complete() {
	}

	@Override
	public void release() {
	}

	@Override
	public void process(EntityContainer entityContainer) {
		entityContainer.process(this);
		
	}

	@Override
	public void process(BoundContainer bound) {
	}

	@Override
	public void process(NodeContainer node) {
	}

	@Override
	public void process(WayContainer wayContainer) {
		Way way = wayContainer.getEntity();
		way = WayHelper.preprocessWay(way);

		try {
		
			if (way != null && !waysToSegment.contains(way.getId())) {
				IWaySegment segment = wayAdapter.adapt(way, nodes);

				if (segment != null) {
					addConnections(segment);
					
					waysQueue.put(segment);
				}
			}

		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			return;
		}
	}

	@Override
	public void process(RelationContainer relation) {
	}

	private void addConnections(IWaySegment segment) {
		addConnectionsOnNode(segment, segment.getStartNodeId());
		if (segment.getStartNodeId() != segment.getEndNodeId()) { // prevent duplicate connection entries if start and end node are the same
			addConnectionsOnNode(segment, segment.getEndNodeId());
		}
	}

	private void addConnectionsOnNode(IWaySegment segment, long nodeId) {
		List<WayRef> nodeNeighbours = wayRefs.get(nodeId);
		List<IWaySegment> segmentsTo = new ArrayList<>();
		for (WayRef ref : nodeNeighbours) {
			if (ref.getWayId() != segment.getId()) { // check if same way
				
				// look for segmented way
				boolean wayHasBeenSegmented = false;
				List<IWaySegment> waySegments = segmentedWaySegments.get(nodeId);
				if (waySegments != null && !waySegments.isEmpty()) {

					for (IWaySegment waySegment : waySegments) {
						if (waySegment.getWayId() == ref.getWayId()) {
							segmentsTo.add(waySegment);
							wayHasBeenSegmented = true;
						}
					}
				}
				
				// way has not been segmented create connection to original way
				if (!wayHasBeenSegmented) {
					segmentsTo.add(WayHelper.createDummyWaySegment(ref, defaultAccesses));
				}
				
			}
			
		}

		if (!segmentsTo.isEmpty()) {
			connectionsBuilder.createConnections(segment, segmentsTo, nodeId, defaultAccesses, wayRelations);
		}

	}

}