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
package at.srfg.graphium.lanelet2import.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.hd.IHDWaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

public class LaneletsAdapterTests {
	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	@Test
	public void testRoadCharacteristics() {
		LaneletsAdapter adapter = new LaneletsAdapter();
		
		// Nodes
		TLongObjectHashMap<Node> nodes = new TLongObjectHashMap<Node>();
		nodes.put(1, new Node(new CommonEntityData(1l, 1, new Date(), null, 1l), 13.3, 47.7));
		nodes.put(2, new Node(new CommonEntityData(2l, 1, new Date(), null, 1l), 13.4, 47.7));
		nodes.put(3, new Node(new CommonEntityData(3l, 1, new Date(), null, 1l), 13.3, 47.8));
		nodes.put(4, new Node(new CommonEntityData(4l, 1, new Date(), null, 1l), 13.4, 47.8));
		
		// Ways
		// - Way1
		TLongObjectHashMap<Way> ways = new TLongObjectHashMap<Way>();
		List<Tag> way1Tags = new ArrayList<Tag>();
		way1Tags.add(new Tag("type", "curbstone"));
		List<WayNode> way1Nodes = new ArrayList<WayNode>();
		way1Nodes.add(new WayNode(1));
		way1Nodes.add(new WayNode(2));
		ways.put(1, new Way(new CommonEntityData(1l, 1, new Date(), null, 1l, way1Tags), way1Nodes));
		// - Way2
		List<Tag> way2Tags = new ArrayList<Tag>();
		way2Tags.add(new Tag("type", "curbstone"));
		List<WayNode> way2Nodes = new ArrayList<WayNode>();
		way2Nodes.add(new WayNode(1));
		way2Nodes.add(new WayNode(2));
		ways.put(2, new Way(new CommonEntityData(2l, 1, new Date(), null, 1l, way2Tags), way2Nodes));
		
		// Relations
		// - Relation1
		List<Relation> relations = new ArrayList<Relation>();
		List<RelationMember> relationMembers = new ArrayList<RelationMember>();
		relationMembers.add(new RelationMember(1l, EntityType.Way, "left"));
		relationMembers.add(new RelationMember(2l, EntityType.Way, "right"));
		List<Tag> relationTags = new ArrayList<Tag>();
		relationTags.add(new Tag("type", "lanelet"));
		relationTags.add(new Tag("subtype", "road"));
		relationTags.add(new Tag("location", Constants.URBAN));
		Relation relation = new Relation(new CommonEntityData(1, 1, new Date(), null, 1l, relationTags), relationMembers);
		relations.add(relation);
		
		// urban road
		List<IHDWaySegment> segments = adapter.adaptLanelets(relations, ways, nodes);
		Assert.assertEquals(1, segments.size());
		IHDWaySegment segment = segments.get(0);
		Assert.assertEquals(4, segment.getFrc().getValue());
		Assert.assertEquals(true, segment.isUrban());
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.BIKE));
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.PRIVATE_CAR));
		
		// non-urban road
		relationTags = new ArrayList<Tag>();
		relationTags.add(new Tag("type", "lanelet"));
		relationTags.add(new Tag("subtype", "road"));
		relationTags.add(new Tag("location", Constants.NONURBAN));
		relation = new Relation(new CommonEntityData(1, 1, new Date(), null, 1l, relationTags), relationMembers);
		relations.clear();
		relations.add(relation);
		segments = adapter.adaptLanelets(relations, ways, nodes);
		Assert.assertEquals(1, segments.size());
		segment = segments.get(0);
		Assert.assertEquals(2, segment.getFrc().getValue());
		Assert.assertEquals(false, segment.isUrban());
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.BIKE));
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.PRIVATE_CAR));
		Assert.assertEquals(false, segment.getAccessBkw().contains(Access.PRIVATE_CAR));
		
		// one-way = no
		relationTags = new ArrayList<Tag>();
		relationTags.add(new Tag("type", "lanelet"));
		relationTags.add(new Tag("subtype", "road"));
		relationTags.add(new Tag("location", Constants.NONURBAN));
		relationTags.add(new Tag(Constants.LANELET_ONEWAY, "no"));
		relation = new Relation(new CommonEntityData(1, 1, new Date(), null, 1l, relationTags), relationMembers);
		relations.clear();
		relations.add(relation);
		segments = adapter.adaptLanelets(relations, ways, nodes);
		Assert.assertEquals(1, segments.size());
		segment = segments.get(0);
		Assert.assertEquals(true, segment.getAccessBkw().contains(Access.PRIVATE_CAR));
		
		// urban highway
		relationTags = new ArrayList<Tag>();
		relationTags.add(new Tag("type", "lanelet"));
		relationTags.add(new Tag("subtype", "highway"));
		relationTags.add(new Tag("location", Constants.URBAN));
		relation = new Relation(new CommonEntityData(1, 1, new Date(), null, 1l, relationTags), relationMembers);
		relations.clear();
		relations.add(relation);
		segments = adapter.adaptLanelets(relations, ways, nodes);
		Assert.assertEquals(1, segments.size());
		segment = segments.get(0);
		Assert.assertEquals(0, segment.getFrc().getValue());
		Assert.assertEquals(true, segment.isUrban());
		Assert.assertEquals(false, segment.getAccessTow().contains(Access.BIKE));
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.PRIVATE_CAR));
		
		// non-urban highway
		relationTags = new ArrayList<Tag>();
		relationTags.add(new Tag("type", "lanelet"));
		relationTags.add(new Tag("subtype", "highway"));
		relationTags.add(new Tag("location", Constants.NONURBAN));
		relation = new Relation(new CommonEntityData(1, 1, new Date(), null, 1l, relationTags), relationMembers);
		relations.clear();
		relations.add(relation);
		segments = adapter.adaptLanelets(relations, ways, nodes);
		Assert.assertEquals(1, segments.size());
		segment = segments.get(0);
		Assert.assertEquals(0, segment.getFrc().getValue());
		Assert.assertEquals(false, segment.isUrban());
		Assert.assertEquals(false, segment.getAccessTow().contains(Access.BIKE));
		Assert.assertEquals(true, segment.getAccessTow().contains(Access.PRIVATE_CAR));
		
	}
}
