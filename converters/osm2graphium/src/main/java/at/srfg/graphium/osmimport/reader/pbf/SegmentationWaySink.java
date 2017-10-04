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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.osmimport.adapter.Way2WaySegmentAdapter;
import at.srfg.graphium.osmimport.connections.ConnectionsBuilder;
import at.srfg.graphium.osmimport.helper.WayHelper;
import at.srfg.graphium.osmimport.model.impl.NodeCoord;
import at.srfg.graphium.osmimport.segmentation.WaySegmenter;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class SegmentationWaySink implements Sink, EntityProcessor {
	
	private static Logger log = LoggerFactory.getLogger(SegmentationWaySink.class);
	
	private TLongObjectHashMap<List<WayRef>> wayRefs = new TLongObjectHashMap<List<WayRef>>();
	private TLongObjectHashMap<List<Relation>> wayRelations = new TLongObjectHashMap<>();
	private TLongObjectHashMap<TLongArrayList> waysToSegment = new TLongObjectHashMap<>();
	private TLongObjectHashMap<NodeCoord> nodes = new TLongObjectHashMap<>();
	private BlockingQueue<IWaySegment> waysQueue;
	private WaySegmenter segmenter = new WaySegmenter();
    private Way2WaySegmentAdapter wayAdapter = new Way2WaySegmentAdapter();
    private Set<Access> defaultAccesses = null;
    private TLongObjectHashMap<List<IWaySegment>> segmentedWaySegments = new TLongObjectHashMap<>(); 
	private int wayCount = 0;
	private int nodeCount = 0;
	private ConnectionsBuilder connectionsBuilder;
	
	public SegmentationWaySink(TLongObjectHashMap<List<WayRef>> wayRefs, TLongObjectHashMap<List<Relation>> wayRelations, 
			BlockingQueue<IWaySegment> waysQueue) {
		this.wayRefs = wayRefs;
		this.wayRelations = wayRelations;
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
		connectionsBuilder = new ConnectionsBuilder();
	}

	@Override
	public void initialize(Map<String, Object> metaData) {
		defaultAccesses = Access.getAccessTypes(new int[]{3});
	}

	@Override
	public void complete() {
		
		// add connection for each way of each node
		for (Long nodeId : segmentedWaySegments.keys()) {
			List<IWaySegment> segments = segmentedWaySegments.get(nodeId);
			Iterator<IWaySegment> itFromSegment = segments.iterator();
			
			while (itFromSegment.hasNext()) {
				Iterator<IWaySegment> itToSegment = segments.iterator();
				IWaySegment segmentFrom = itFromSegment.next();
				
				// add connection between all segmented neighbours
				List<IWaySegment> segmentsTo = new ArrayList<>();
				while (itToSegment.hasNext()) {
					IWaySegment segmentTo = itToSegment.next();
					if (segmentFrom.getId() != segmentTo.getId()) {
						segmentsTo.add(segmentTo);
					}
				}
				
				connectionsBuilder.createConnections(segmentFrom, segmentsTo, nodeId, defaultAccesses, wayRelations);
				
			}
			
		}
		
		Set<IWaySegment> distinctSegments = new HashSet<>();
		for (List<IWaySegment> segments : segmentedWaySegments.valueCollection()) {
			distinctSegments.addAll(segments);
		}
		for (IWaySegment segment : distinctSegments) {
			try {
				// add connection to all non-segmented neighbours
				// these connections are only directed outgoing, incoming connections has to be made in next step where all 
				// non-segmented ways will be processed
				addProbablyNonSegmentedWays(segment, segment.getStartNodeId());
				if (segment.getStartNodeId() != segment.getEndNodeId()) { // prevent duplicate connection entries if start and end node are the same
					addProbablyNonSegmentedWays(segment, segment.getEndNodeId());
				}

				waysQueue.put(segment);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				return;
			}
		}
	}

	private void addProbablyNonSegmentedWays(IWaySegment segment, long nodeId) {
		List<IWaySegment> segmentsTo = new ArrayList<>();
		List<WayRef> nodeNeighbours = wayRefs.get(nodeId);
		for (WayRef ref : nodeNeighbours) {
			if (ref.getWayId() != segment.getWayId() // check if same way
					&& ref.getType() == 0) {
				// only ways having this node as an end node will be processed
				
				// look for segmented way
				boolean wayAlreadySegmented = false;
				List<IWaySegment> waySegments = segmentedWaySegments.get(nodeId);
				if (waySegments != null && !waySegments.isEmpty()) {
					for (IWaySegment waySegment : waySegments) {
						if (waySegment.getWayId() == ref.getWayId()) {
							wayAlreadySegmented = true;
						}
					}
				}
				if (!wayAlreadySegmented) {
					segmentsTo.add(WayHelper.createDummyWaySegment(ref, defaultAccesses));
				}
			}
		}
		if (!segmentsTo.isEmpty()) {
			connectionsBuilder.createConnections(segment, segmentsTo, nodeId, defaultAccesses, wayRelations);
		}
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
	public void process(NodeContainer nodeContainer) {
		if (nodeCount == 0) {
			log.info("processing nodes...");
		}
		nodeCount++;
		if (nodeCount > 0 && nodeCount % 10000000 == 0) {
			log.info(nodeCount + " nodes");
		}
		
		Node node = nodeContainer.getEntity();
		if (wayRefs.containsKey(node.getId())) {
			nodes.put(node.getId(), new NodeCoord(node.getLongitude(), node.getLatitude()));
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

		if (way != null && waysToSegment.contains(way.getId())) {
			int[] segmentationIndexes = findSegmentationIndexes(way, waysToSegment.get(way.getId()));
			if (segmentationIndexes.length > 0) {
				for (int i=1; i<segmentationIndexes.length; i++) {
					if (log.isDebugEnabled()) {
						log.debug("segmenting way " + way.getId() + " from index " + segmentationIndexes[i-1] + " to index " + segmentationIndexes[i]);
					}
					Way segmentedWay = segmenter.segment(way, segmentationIndexes[i-1], segmentationIndexes[i]);
					
					// adapt to IWaySegment
					IWaySegment segment = wayAdapter.adapt(segmentedWay, nodes);

					if (segment != null) {
						addToMap(segment);
					}
				}
			}
		}
		
		wayCount++;
	}

	@Override
	public void process(RelationContainer relation) {
	}

	private void addToMap(IWaySegment segment) {
		if (segment != null) {
			if (segmentedWaySegments.contains(segment.getStartNodeId())) {
				segmentedWaySegments.get(segment.getStartNodeId()).add(segment);
			} else {
				List<IWaySegment> segmentList = new ArrayList<>();
				segmentList.add(segment);
				segmentedWaySegments.put(segment.getStartNodeId(), segmentList);
			}
	
			if (segment.getStartNodeId() != segment.getEndNodeId()) {
				if (segmentedWaySegments.contains(segment.getEndNodeId())) {
					segmentedWaySegments.get(segment.getEndNodeId()).add(segment);
				} else {
					List<IWaySegment> segmentList = new ArrayList<>();
					segmentList.add(segment);
					segmentedWaySegments.put(segment.getEndNodeId(), segmentList);
				}
			}
		}		
	}

	private int[] findSegmentationIndexes(Way way, TLongArrayList segmentationNodeIds) {
		int[] segmentationIndexes = new int[segmentationNodeIds.size() + 2];
		int pos = 0;
		segmentationIndexes[pos++] = 0; // first node index (start node)
		segmentationIndexes[pos++] = way.getWayNodes().size() - 1; // last node index (end node)
		
		int i=0;
		for (WayNode node : way.getWayNodes()) {
			long nodeId = node.getNodeId();
			if (segmentationNodeIds.contains(nodeId)) {
				segmentationIndexes[pos++] = i; // index of a segmentation node
			}
			i++;
		}
		
		Arrays.sort(segmentationIndexes);
		
		return segmentationIndexes;
	}

	public TLongObjectHashMap<NodeCoord> getNodes() {
		return nodes;
	}

	public TLongObjectHashMap<List<IWaySegment>> getSegmentedWaySegments() {
		return segmentedWaySegments;
	}

}