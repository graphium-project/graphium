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
package at.srfg.graphium.osmimport.connections;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class TestConnectionsBuilder {

	@Test
	public void testCreateConnections() {
		ConnectionsBuilder connectionsBuilder = new ConnectionsBuilder(); 
		
		long segmentFromId = 258306367;
		long segmentToId = 258306365;
		long segmentTo2Id = 143558685;
		long nodeId = 269468749;
		
		TLongObjectHashMap<List<Relation>> wayRelations = new TLongObjectHashMap<>();

		RelationMember memberFrom = new RelationMember(segmentFromId, EntityType.Way, "from");
		RelationMember memberNode = new RelationMember(nodeId, EntityType.Node, "via");
		RelationMember memberTo = new RelationMember(segmentToId, EntityType.Way, "to");
		List<RelationMember> members = new ArrayList<>();
		members.add(memberFrom);
		members.add(memberNode);
		members.add(memberTo);
		Relation rel = new Relation(new CommonEntityData(1, 1, new Date(), new OsmUser(1, "user"), 1), members);
		rel.getTags().add(new Tag("type", "restriction"));
		rel.getTags().add(new Tag("restriction", "only_right_turn"));
		
		List<Relation> relations = new ArrayList<>();
		relations.add(rel);
		
		wayRelations.put(258306367, relations);
		
		Set<Access> defaultAccesses = new HashSet<>();
		defaultAccesses.add(Access.PRIVATE_CAR);
		
		IWaySegment segmentFrom = new WaySegment();
		segmentFrom.setId(segmentFromId);
		segmentFrom.setStartNodeId(nodeId);
		segmentFrom.setAccessBkw(defaultAccesses);
		segmentFrom.setAccessTow(defaultAccesses);
		
		IWaySegment segmentTo = new WaySegment();
		segmentTo.setId(segmentToId);
		segmentTo.setEndNodeId(nodeId);
		segmentTo.setAccessTow(defaultAccesses);
		segmentTo.setAccessBkw(defaultAccesses);
		
		IWaySegment segmentTo2 = new WaySegment();
		segmentTo2.setId(segmentTo2Id);
		segmentTo2.setEndNodeId(nodeId);
		segmentTo2.setAccessTow(defaultAccesses);
		segmentTo2.setAccessBkw(defaultAccesses);
		
		List<IWaySegment> segmentsTo = new ArrayList<>();
		segmentsTo.add(segmentTo);
		segmentsTo.add(segmentTo2);
		
		
		connectionsBuilder.createConnections(segmentFrom, segmentsTo, nodeId, defaultAccesses, wayRelations);
		
		Assert.assertEquals(1, segmentFrom.getStartNodeCons().size());
		
		
	}
	
}
