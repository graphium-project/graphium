/**
 * Copyright © 2021 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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

package at.srfg.graphium.lanelet2import.connections;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.reader.LaneletContainer;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDWaySegment;

public class ConnectionsBuilderTests {
	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	@Test
	public void testSucceedingSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * no valid connection
		 *  [11---12] x [15---16]  // left border start/end node ids
		 *  [1-->--2] x [3-->--4]  // start/end node ids
		 *  [13---14] x [17---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(15);
		segment2.setLeftBorderEndNodeId(16);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(18);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(0, segment1.getCons().size());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(0, segment2.getCons().size());
		
		/**
		 * valid connection
		 *  [11---12] > [12---16]  // left border start/end node ids
		 *  [1-->--2] > [2-->--4]  // start/end node ids
		 *  [13---14] > [14---18]  // right border start/end node ids 
		 */
		
		segment1.getCons().clear();
		
		segment2.getCons().clear();
		segment2.setStartNodeId(2);
		segment2.setLeftBorderStartNodeId(12);
		segment2.setRightBorderStartNodeId(14);
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(2, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(0, segment2.getCons().size());
		
		/**
		 * first segment reversed (no connection possible)
		 *  [11---12] X [12---16]
		 *  [1--<--2] X [2-->--4]
		 *  [13---14] X [14---18]
		 */
		
		segment1.getCons().clear();
		segment1.setStartNodeId(2);
		segment1.setEndNodeId(1);
		segment1.setLeftBorderStartNodeId(14);
		segment1.setLeftBorderEndNodeId(13);
		segment1.setRightBorderStartNodeId(12);
		segment1.setRightBorderEndNodeId(11);
		
		segment2.getCons().clear();
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(0, segment1.getCons().size());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(0, segment2.getCons().size());
		
		/**
		 * second segment reversed (no connection possible)
		 *  [11---12] X [12---16]
		 *  [1-->--2] X [2--<--4]
		 *  [13---14] X [14---18]
		 */
		
		segment1.getCons().clear();
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		segment2.getCons().clear();
		segment2.setStartNodeId(4);
		segment2.setEndNodeId(2);
		segment2.setLeftBorderStartNodeId(18);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(16);
		segment2.setRightBorderEndNodeId(12);
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(0, segment1.getCons().size());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(0, segment2.getCons().size());
		
		/**
		 * both segment reversed
		 *  [11---12] < [12---16]
		 *  [1--<--2] < [2--<--4]
		 *  [13---14] < [14---18]
		 */
		
		segment1.getCons().clear();
		segment1.setStartNodeId(2);
		segment1.setEndNodeId(1);
		segment1.setLeftBorderStartNodeId(14);
		segment1.setLeftBorderEndNodeId(13);
		segment1.setRightBorderStartNodeId(12);
		segment1.setRightBorderEndNodeId(11);
		
		segment2.getCons().clear();
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(0, segment1.getCons().size());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(2, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
	}
	
	@Test
	public void testNeighborSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * neighbors in forward direction
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *          |
		 *      [13---14]  // left border start/end node ids
		 *  sg2 [3-->--4]  // start/end node ids
		 *      [17---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(18);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		/**
		 * neighbors in different directions A
		 *      [11---12] // right border
		 *  sg1 [1--<--2] // backward
		 *      [13---14] // left border
		 *          |
		 *      [13---14] // left border
		 *  sg2 [3-->--4] // forward
		 *      [17---18] // right border
		 */
		
		segment1.getCons().clear();
		segment1.setId(1l);
		segment1.setStartNodeId(2);
		segment1.setEndNodeId(1);
		segment1.setLeftBorderStartNodeId(14);
		segment1.setLeftBorderEndNodeId(13);
		segment1.setRightBorderStartNodeId(12);
		segment1.setRightBorderEndNodeId(11);
		
		segment2.getCons().clear();
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		Assert.assertEquals(Constants.CONNECTION_REVERSE, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		Assert.assertEquals(Constants.CONNECTION_REVERSE, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		
		/**
		 * neighbors in different directions B
		 *      [11---12]
		 *  sg1 [1-->--2] // forward
		 *      [13---14]
		 *          |
		 *      [13---14]
		 *  sg2 [3--<--4] // backward
		 *      [17---18]
		 */
		
		segment1.getCons().clear();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		segment2.getCons().clear();
		segment2.setId(2l);
		segment2.setStartNodeId(4);
		segment2.setEndNodeId(3);
		segment2.setLeftBorderStartNodeId(18);
		segment2.setLeftBorderEndNodeId(17);
		segment2.setRightBorderStartNodeId(14);
		segment2.setRightBorderEndNodeId(13);
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		Assert.assertEquals(Constants.CONNECTION_REVERSE, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
		Assert.assertEquals(Constants.CONNECTION_REVERSE, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIRECTION));
	}
	
	@Test
	public void testRestrictedNeighborSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * neighbors in forward direction, no lane change allowed
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *          X
		 *      [13---14]  // left border start/end node ids
		 *  sg2 [3-->--4]  // start/end node ids
		 *      [17---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		segment1.getTags().put(Constants.TAG_LANE_CHANGE + ":" + Constants.RIGHT, "false");
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(18);
		segment1.getTags().put(Constants.TAG_LANE_CHANGE + ":" + Constants.LEFT, "false");
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		
		/**
		 * neighbors in forward direction, lane change from left to right allowed
		 *      [11---12]
		 *  sg1 [1-->--2]
		 *      [13---14]
		 *          V
		 *      [13---14]
		 *  sg2 [3-->--4]
		 *      [17---18]
		 */
		
		segment1.getCons().clear();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		segment1.getTags().remove(Constants.TAG_LANE_CHANGE + ":" + Constants.RIGHT);
		
		segment2.getCons().clear();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(18);
		segment2.getTags().put(Constants.TAG_LANE_CHANGE + ":" + Constants.LEFT, "false");
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		/**
		 * neighbors in forward direction, lane change from right to left allowed
		 *      [11---12]
		 *  sg1 [1-->--2]
		 *      [13---14]
		 *          ^
		 *      [13---14]
		 *  sg2 [3-->--4]
		 *      [17---18]
		 */
		
		segment1.getCons().clear();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		segment1.getTags().put(Constants.TAG_LANE_CHANGE + ":" + Constants.RIGHT, "false");
		
		segment2.getCons().clear();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(18);
		segment2.getTags().remove(Constants.TAG_LANE_CHANGE + ":" + Constants.LEFT);
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
	}
	
	@Test
	public void testDivergingNeighborAndOverlappingSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * DIVERGING neighbors in forward direction (OVERLAPPING)
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *         -<
		 *      [11---14]  // left border start/end node ids
		 *  sg2 [1-->--4]  // start/end node ids
		 *      [13---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(1);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(11);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(13);
		segment2.setRightBorderEndNodeId(18);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
	}
	
	@Test
	public void testDivergingNeighborAndNonOverlappingSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * DIVERGING neighbors in forward direction (NON-OVERLAPPING)
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *         -<
		 *      [13---14]  // left border start/end node ids
		 *  sg2 [13->--4]  // start/end node ids
		 *      [13---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(13);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(13);
		segment2.setRightBorderEndNodeId(18);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		// Connection TO diverging lanelet
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		
		// Connection FROM diverging lanelet
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		Assert.assertNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
	}
	
	@Test
	public void testMergingNeighborAndOverlappingSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * MERGING neighbors in forward direction (OVERLAPPING)
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *          >-
		 *      [13---12]  // left border start/end node ids
		 *  sg2 [3-->--2]  // start/end node ids
		 *      [17---14]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(2);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(12);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(14);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment2.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
	}
	
	@Test
	public void testMergingNeighborAndNonOverlappingSegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * MERGING neighbors in forward direction (NON-OVERLAPPING)
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *          >-
		 *      [13---14]  // left border start/end node ids
		 *  sg2 [3-->-14]  // start/end node ids
		 *      [17---14]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(3);
		segment2.setEndNodeId(14);
		segment2.setLeftBorderStartNodeId(13);
		segment2.setLeftBorderEndNodeId(14);
		segment2.setRightBorderStartNodeId(17);
		segment2.setRightBorderEndNodeId(14);
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		// Connection to merging lanelet
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
		
		connectionsBuilder.build(segment2, laneletContainer);
		// Connection from merging lanelet
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.LEFT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertEquals(Boolean.TRUE.toString(), segment2.getCons().get(0).getTags().get(Constants.CONNECTION_MERGING));
		Assert.assertNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_DIVERGING));
	}
	
	@Test
	public void testSucceedingTwoWaySegments() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder();
		
		/**
		 * no valid connection
		 *  [11---12] >< [12---16]  // left border start/end node ids
		 *  [1-<>--2] >< [2-<>--4]  // start/end node ids
		 *  [13---14] >< [14---18]  // right border start/end node ids 
		 */
		
		IHDWaySegment segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		segment1.setAccessTow(new HashSet<Access>());
		segment1.getAccessTow().add(Access.PRIVATE_CAR);
		segment1.setAccessBkw(segment1.getAccessTow());
		
		IHDWaySegment segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(2);
		segment2.setEndNodeId(4);
		segment2.setLeftBorderStartNodeId(12);
		segment2.setLeftBorderEndNodeId(16);
		segment2.setRightBorderStartNodeId(14);
		segment2.setRightBorderEndNodeId(18);
		segment2.setAccessTow(new HashSet<Access>());
		segment2.getAccessTow().add(Access.PRIVATE_CAR);
		segment2.setAccessBkw(segment2.getAccessTow());
		
		LaneletContainer laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(2, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		
		connectionsBuilder.build(segment2, laneletContainer);
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(2, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		
		/**
		 * neighbors in forward direction
		 *      [11---12]  // left border start/end node ids
		 *  sg1 [1-->--2]  // start/end node ids
		 *      [13---14]  // right border start/end node ids 
		 *          |
		 *      [13---14]  // left border start/end node ids
		 *  sg2 [3--<--4]  // start/end node ids
		 *      [17---18]  // right border start/end node ids 
		 */
		
		segment1 = new HDWaySegment();
		segment1.setId(1l);
		segment1.setStartNodeId(1);
		segment1.setEndNodeId(2);
		segment1.setLeftBorderStartNodeId(11);
		segment1.setLeftBorderEndNodeId(12);
		segment1.setRightBorderStartNodeId(13);
		segment1.setRightBorderEndNodeId(14);
		
		segment2 = new HDWaySegment();
		segment2.setId(2l);
		segment2.setStartNodeId(4);
		segment2.setEndNodeId(3);
		segment2.setLeftBorderStartNodeId(18);
		segment2.setLeftBorderEndNodeId(17);
		segment2.setRightBorderStartNodeId(14);
		segment2.setRightBorderEndNodeId(13);
		segment2.setAccessTow(new HashSet<Access>());
		segment2.getAccessTow().add(Access.PRIVATE_CAR);
		segment2.setAccessBkw(segment2.getAccessTow());
		
		laneletContainer = new LaneletContainer();
		laneletContainer.addLanelet(segment1);
		laneletContainer.addLanelet(segment2);
		
		connectionsBuilder.build(segment1, laneletContainer);
		
		Assert.assertEquals(1, segment1.getCons().size());
		Assert.assertEquals(-1l, segment1.getCons().get(0).getNodeId());
		Assert.assertEquals(1l, segment1.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(2l, segment1.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment1.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
		connectionsBuilder.build(segment2, laneletContainer);
		
		Assert.assertEquals(1, segment2.getCons().size());
		Assert.assertEquals(-1l, segment2.getCons().get(0).getNodeId());
		Assert.assertEquals(2l, segment2.getCons().get(0).getFromSegmentId());
		Assert.assertEquals(1l, segment2.getCons().get(0).getToSegmentId());
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertEquals(Constants.RIGHT, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_PARALLEL));
		Assert.assertNotNull(segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		Assert.assertEquals(Constants.CONNECTION_TYPE_CONNECTS, segment2.getCons().get(0).getTags().get(Constants.CONNECTION_TYPE));
		
	}
}
