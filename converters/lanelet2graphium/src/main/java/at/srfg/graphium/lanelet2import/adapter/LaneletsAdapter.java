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
package at.srfg.graphium.lanelet2import.adapter;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.EntityParsingHelper;
import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.HDWaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletsAdapter {

	private static Logger log = LoggerFactory.getLogger(LaneletsAdapter.class);
	
	public List<IHDWaySegment> adaptLanelets(List<Relation> relations, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		List<IHDWaySegment> segments = new ArrayList<>();
		
		for (Relation rel : relations) {
			String type = EntityParsingHelper.getType(rel);
			if (type != null && type.equals(Constants.TYPE_LANELET)) {
				segments.add(adapt(rel, ways, nodes));
			}
		}
		
		return segments;
	}
	
	// TODO: Wie erkenne ich die Richtung eines Lanelets?
	// Laut Doku: "The direction is determined by the order in which the left and right bound is set." 

	public IHDWaySegment adapt(Relation relation, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(relation.getId());
		
		Map<String, String> tags = new HashMap<>();
		
		for (RelationMember member : relation.getMembers()) {
			if (member.getMemberType().equals(EntityType.Way)) {
				String role = member.getMemberRole();
				if (role.equals("left") || role.equals("right")) {
					Way way = ways.get(member.getMemberId());
					if (way == null) {
						log.error("Way " + member.getMemberId() + " is null");
						return null;
					}
					SimpleEntry<String, String> typeEntry = EntityParsingHelper.parseLaneletBoarderType(role, way);
					String laneChangePossible = "false";
					if (typeEntry != null) {
						tags.put(role + ":" + typeEntry.getKey(), typeEntry.getValue());
						laneChangePossible = determineLaneChange(role, typeEntry.getKey(), typeEntry.getValue());
					}
					tags.put(Constants.TAG_LANE_CHANGE + ":" + role, laneChangePossible);
					
					LineString boarderLinestring = EntityParsingHelper.createLinestring(way, nodes, Constants.SRID);
					if (boarderLinestring == null) {
						log.error("Geometry of way " + way.getId() + " is null!");
						return null;
					}
					if (role.equals("left")) {
						segment.setLeftBoarderGeometry(boarderLinestring);
						segment.setLeftBoarderStartNodeId(way.getWayNodes().get(0).getNodeId());
						segment.setLeftBoarderEndNodeId(way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId());
					} else {
						segment.setRightBoarderGeometry(boarderLinestring);
						segment.setRightBoarderStartNodeId(way.getWayNodes().get(0).getNodeId());
						segment.setRightBoarderEndNodeId(way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId());
					}
				}
			}
		}

		segment.setLanesTow((short)1);
		
//		calculateCenterLine(segment);
//		
//		setAccess(segment, tags);
//		setName(segment, tags);
//		setFrc(segment, tags);
//		setFormOfWay(segment, tags);
//		setSpeed(segment, tags);
//		setBridge(segment, tags);
//		setTunnel(segment, tags);
//		setUrban(segment, tags);
		
		return segment;
	}

	private String determineLaneChange(String side, String type, String subType) {
		boolean laneChange = false; // default false
		if (type.equals(Constants.LANELET_TYPE_LINE_THIN) || type.equals(Constants.LANELET_TYPE_LINE_THICK)) {
			if (subType != null) {
				switch (subType) {
				case Constants.LANELET_SUBTYPE_SOLID:
					laneChange = false;
					break;
	
				case Constants.LANELET_SUBTYPE_DASHED:
					laneChange = true;
					break;
	
				case Constants.LANELET_SUBTYPE_DASHED_SOLID:
					// TODO: Hier müssen wir wissen, in welcher Richtung der Boarder zum Lanelet ausgerichtet ist!!!
	//				if (side.equals("left")) {
	//					laneChange = false;
					break;
	
	
				default:
					break;
				}
			}
		}
		
		return Boolean.toString(laneChange);
	}

	private void setName(IWaySegment segment, Map<String, String> tags) {
		String name = null;
		if (tags.containsKey("ref")) {
			name = tags.get("ref");
		}
		if (tags.containsKey("name")) {
			if (name == null) {
				name = tags.get("name");
			} else {
				name += " - " + tags.get("name");
			}
		}
		segment.setName(name);
	}

}
