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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.LaneletHelper;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.HDWaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletsAdapter {
	
	
	// TODO: Sollen wir die Boarder-Linestrings umdrehen, falls die Digitalisierungsrichtung nicht mit der Fahrtrichtung zusammenpasst?
	

	private static Logger log = LoggerFactory.getLogger(LaneletsAdapter.class);

	private float miles2km = 1.60934f;
	
	public List<IHDWaySegment> adaptLanelets(List<Relation> relations, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		List<IHDWaySegment> segments = new ArrayList<>();
		
		for (Relation rel : relations) {
			String type = LaneletHelper.getType(rel);
			if (type != null && type.equals(Constants.TYPE_LANELET)) {
				segments.add(adapt(rel, ways, nodes));
			}
		}
		
		return segments;
	}
	
	public IHDWaySegment adapt(Relation relation, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(relation.getId());
		
		Way leftBoarder = null;
		Way rightBoarder = null;
		
		for (RelationMember member : relation.getMembers()) {
			if (member.getMemberType().equals(EntityType.Way)) {
				String role = member.getMemberRole();
				if (role.equals("left")) {
					leftBoarder = ways.get(member.getMemberId());
					if (leftBoarder == null) {
						log.error("Way " + member.getMemberId() + " is null");
						return null;
					}
				} else if (role.equals("right")) {
					rightBoarder = ways.get(member.getMemberId());
					if (rightBoarder == null) {
						log.error("Way " + member.getMemberId() + " is null");
						return null;
					}
				}
			}
		}
		
		segment.setLeftBoarderGeometry(LaneletHelper.createLinestring(leftBoarder, nodes, Constants.SRID));
		segment.setLeftBoarderStartNodeId(leftBoarder.getWayNodes().get(0).getNodeId());
		segment.setLeftBoarderEndNodeId(leftBoarder.getWayNodes().get(leftBoarder.getWayNodes().size()-1).getNodeId());
		segment.setRightBoarderGeometry(LaneletHelper.createLinestring(rightBoarder, nodes, Constants.SRID));
		segment.setRightBoarderStartNodeId(rightBoarder.getWayNodes().get(0).getNodeId());
		segment.setRightBoarderEndNodeId(rightBoarder.getWayNodes().get(rightBoarder.getWayNodes().size()-1).getNodeId());

		boolean[] boarderDirections = checkBoarderInversion(segment);
		
		determineLaneChanges(segment, boarderDirections, leftBoarder, rightBoarder);
		
		// build tag map
		Map<String, String> tags = new HashMap<>();
		relation.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
		
		setLanes(segment, tags);
		setRoadCharacteristics(segment, tags);
		
//		setAccess(segment, tags);
		setOptionalTags(segment, tags);
		
		calculateCenterLine(segment, boarderDirections);

		return segment;
	}

	private void calculateCenterLine(IHDWaySegment segment, boolean[] boarderDirections) {
		segment.setGeometry(LaneletHelper.calculateCenterline(segment, boarderDirections));
	}

	private void setRoadCharacteristics(IHDWaySegment segment, Map<String, String> tags) {
		// see https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Attribute.cpp
		String roadType = tags.get("subtype");
		if (roadType == null) {
			roadType = "lane"; // default
		}
		String location = tags.get("location");
		if (location == null) {
			location = "urban"; // default
		}
		
		segment.getTags().put(Constants.ROAD_TYPE, roadType);
		segment.getTags().put(Constants.LOCATION, location);
		
		Set<Access> accesses;
		switch (roadType) {
		case "road":
			if (location.equals(Constants.URBAN)) {
				segment.setFrc(FuncRoadClass.OTHER_MAJOR_ROAD);
				segment.setUrban(true);
				// TODO: set default city speed limit
			} else {
				segment.setFrc(FuncRoadClass.MAJOR_ROAD_LESS_IMORTANT_THAN_MOTORWAY);
				segment.setUrban(false);
				// TODO: set default nonurban speed limit
			}
			accesses = allVehiclesAccesses();
			accesses.add(Access.BIKE);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_MULTI_CARRIAGEWAY_WHICH_IS_NOT_A_MOTORWAY);
			break;

		case "highway":
			if (location.equals(Constants.URBAN)) {
				segment.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
				segment.setUrban(true);
				// TODO: set default urban highway speed limit
			} else {
				segment.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
				segment.setUrban(false);
				// TODO: set default nonurban highway speed limit
			}
			accesses = allVehiclesAccesses();
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_MOTORWAY);
			break;

		case "play_street":
			segment.setFrc(FuncRoadClass.SONSTIGE_STRASSEN);
//			segment.setUrban(true);
			// TODO: set default play street speed limit
			accesses = allVehiclesAccesses();
			accesses.add(Access.BIKE);
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_SERVICE_ROAD);
			break;

		case "emergency_lane":
			segment.setFrc(FuncRoadClass.SONSTIGE_STRASSEN);
//			segment.setUrban(true);
			// TODO: set default average emergency vehicle speed?
			accesses = new HashSet<>();
			accesses.add(Access.EMERGENCY_VEHICLE);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.ROAD_FOR_AUTHORITIES);
			break;

		case "bus_lane":
			if (location.equals(Constants.URBAN)) {
				segment.setFrc(FuncRoadClass.LOCAL_ROAD_OF_HIGH_IMPORTANCE);
				segment.setUrban(true);
				// TODO: set default city 1speed limit
			} else {
				segment.setFrc(FuncRoadClass.LOCAL_ROAD_OF_HIGH_IMPORTANCE);
				segment.setUrban(false);
				// TODO: set default nonurban speed limit
			}
			accesses = new HashSet<>();
			accesses.add(Access.EMERGENCY_VEHICLE);
			accesses.add(Access.PUBLIC_BUS);
			accesses.add(Access.TAXI);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.ROAD_FOR_AUTHORITIES);
			break;

		case "bicycle_lane":
			segment.setFrc(FuncRoadClass.RAD_FUSSWEG);
//			segment.setUrban(true);
			// TODO: set default average bike vehicle speed?
			accesses = new HashSet<>();
			accesses.add(Access.BIKE);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_WALKWAY_OR_BICYCLE_WAY);
			break;

		case "exit":
			segment.setFrc(FuncRoadClass.SONSTIGE_STRASSEN);
			segment.setUrban(true);
			// TODO: set default urban speed limit
			accesses = allVehiclesAccesses();
			accesses.add(Access.BIKE);
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.NOT_APPLICABLE);
			break;

		case "walkway":
			segment.setFrc(FuncRoadClass.RAD_FUSSWEG);
//			segment.setUrban(true);
			// TODO: set default average pedestrian walking speed
			accesses = new HashSet<>();
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_WALKWAY_OR_BICYCLE_WAY);
			break;

		case "shared_walkway":
			segment.setFrc(FuncRoadClass.RAD_FUSSWEG);
//			segment.setUrban(true);
			// TODO: set default average pedestrian walking speed
			accesses = new HashSet<>();
			accesses.add(Access.BIKE);
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_WALKWAY_OR_BICYCLE_WAY);
			break;

		case "crosswalk":
			segment.setFrc(FuncRoadClass.RAD_FUSSWEG);
//			segment.setUrban(true);
			// TODO: set default average pedestrian walking speed
			accesses = new HashSet<>();
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_WALKWAY_OR_BICYCLE_WAY);
			break;

		case "stairs":
			segment.setFrc(FuncRoadClass.RAD_FUSSWEG);
//			segment.setUrban(true);
			// TODO: set default average pedestrian walking speed
			accesses = new HashSet<>();
			accesses.add(Access.PEDESTRIAN);
			segment.setAccessTow(accesses);
			segment.setFormOfWay(FormOfWay.PART_OF_A_PEDESTRIAN_ZONE);
			break;

		default:
			segment.setFrc(FuncRoadClass.NOT_APPLICABLE);
			segment.setFormOfWay(FormOfWay.NOT_APPLICABLE);
			break;
		}

	}

	private Set<Access> allVehiclesAccesses() {
		// see https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Attribute.cpp
		Set<Access> accesses = new HashSet<>();
		accesses.add(Access.PUBLIC_BUS);
		accesses.add(Access.PRIVATE_CAR);
		accesses.add(Access.ELECTRIC_CAR);
		accesses.add(Access.TRUCK);
		accesses.add(Access.MOTORCYCLE);
		accesses.add(Access.TAXI);
		accesses.add(Access.EMERGENCY_VEHICLE);
		return accesses;
	}

	private void setLanes(IHDWaySegment segment, Map<String, String> tags) {
		// see https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Attribute.cpp
		segment.setLanesTow((short)1);
		for (String key : tags.keySet()) {
			if (key.contains(Constants.LANELET_ONEWAY)) {
				if (tags.get(key).equals("no")) {
					segment.setLanesBkw((short)1);
				}
			}
		}
	}

	private void determineLaneChanges(IHDWaySegment segment, boolean[] boarderDirections, Way leftBoarder, Way rightBoarder) {
		// left boarder
		SimpleEntry<String, String> typeEntry = LaneletHelper.parseLaneletBoarderType(leftBoarder);
		String laneChangePossible = "false";
		if (typeEntry != null) {
			segment.getTags().put("left:" + typeEntry.getKey(), typeEntry.getValue());
			laneChangePossible = determineLaneChange(true, !boarderDirections[0], typeEntry.getKey(), typeEntry.getValue());
		}
		segment.getTags().put(Constants.TAG_LANE_CHANGE + ":left", laneChangePossible);
		
		// right boarder
		typeEntry = LaneletHelper.parseLaneletBoarderType(rightBoarder);
		laneChangePossible = "false";
		if (typeEntry != null) {
			segment.getTags().put("right:" + typeEntry.getKey(), typeEntry.getValue());
			laneChangePossible = determineLaneChange(true, !boarderDirections[0], typeEntry.getKey(), typeEntry.getValue());
		}
		segment.getTags().put(Constants.TAG_LANE_CHANGE + ":right", laneChangePossible);
	}
	
	private boolean[] checkBoarderInversion(IHDWaySegment segment) {
		// Check if left and / or right boarder have to be inverted (linestring and nodeIds)
		boolean[] boarderDirections = LaneletHelper.checkLineDirections(segment);
		// if so put a flag into tags: "invertedBoarder=left/right/both")
		if (!boarderDirections[0] && boarderDirections[1]) {
			segment.getTags().put(Constants.BOARDER_INVERTED, "left");
			invertLeftBoarderNodeIds(segment);
		} else if (boarderDirections[0] && !boarderDirections[1]) {
			segment.getTags().put(Constants.BOARDER_INVERTED, "right");
			invertRightBoarderNodeIds(segment);
		} else if (!boarderDirections[0] && !boarderDirections[1]) {
			segment.getTags().put(Constants.BOARDER_INVERTED, "both");
			invertLeftBoarderNodeIds(segment);
			invertRightBoarderNodeIds(segment);
		}

		// TODO: Sollen wir die Boarder-Linestrings umdrehen, falls die Digitalisierungsrichtung nicht mit der Fahrtrichtung zusammenpasst?
		// Zur Zeit wird der Linestring so belassen wie er im Original ist.
		
		return boarderDirections;
	}
	
	private void invertLeftBoarderNodeIds(IHDWaySegment segment) {
		long startNodeId = segment.getLeftBoarderStartNodeId();
		segment.setLeftBoarderStartNodeId(segment.getLeftBoarderEndNodeId());
		segment.setLeftBoarderEndNodeId(startNodeId);
	}

	private void invertRightBoarderNodeIds(IHDWaySegment segment) {
		long startNodeId = segment.getRightBoarderStartNodeId();
		segment.setRightBoarderStartNodeId(segment.getRightBoarderEndNodeId());
		segment.setRightBoarderEndNodeId(startNodeId);
	}

	private String determineLaneChange(boolean leftBoarder, boolean inverted, String type, String subType) {
		boolean laneChange = false; // default false
		if (type.equals(Constants.LANELET_TYPE_LINE_THIN) || type.equals(Constants.LANELET_TYPE_LINE_THICK)) {
			if (subType != null) {
				switch (subType) {
				case Constants.LANELET_SUBTYPE_SOLID:
					// no lane change allowed
					laneChange = false;
					break;
	
				case Constants.LANELET_SUBTYPE_DASHED:
					// lane change allowed
					laneChange = true;
					break;
	
				case Constants.LANELET_SUBTYPE_DASHED_SOLID:
					// lane change from left to right allowed
					if (leftBoarder) {
						if (inverted) {
							laneChange = false;
						} else {
							laneChange = true;
						}
					} else {
						if (inverted) {
							laneChange = true;
						} else {
							laneChange = false;
						}
					}
						
					break;
	
				case Constants.LANELET_SUBTYPE_SOLID_DASHED:
					// lane change from right to left allowed
					if (leftBoarder) {
						if (inverted) {
							laneChange = true;
						} else {
							laneChange = false;
						}
					} else {
						if (inverted) {
							laneChange = false;
						} else {
							laneChange = true;
						}
					}
						
					break;
	
				default:
					break;
				}
			}
		}
		
		return Boolean.toString(laneChange);
	}

	private void setOptionalTags(IWaySegment segment, Map<String, String> tags) {
		// TODO: Tag Direction?
		
		if (tags.containsKey(Constants.LANELET_ROAD_NAME)) {
			segment.setName(tags.get(Constants.LANELET_ROAD_NAME));
		}
		if (tags.containsKey(Constants.LANELET_ROAD_SURFACE)) {
			segment.getTags().put(Constants.ROAD_SURFACE, tags.get(Constants.LANELET_ROAD_SURFACE));
		}
		if (tags.containsKey(Constants.LANELET_REGION)) {
			segment.getTags().put(Constants.REGION, tags.get(Constants.REGION));
		}
		
		if (tags.containsKey(Constants.LANELET_SPEED_LIMIT)) {
			String speedLimitOriginStr = tags.get(Constants.LANELET_SPEED_LIMIT);
			String speedLimitStr;
			if (speedLimitOriginStr.contains("km/h")) {
				speedLimitStr = speedLimitOriginStr.replace("km/h", "").trim();
				try {
					segment.setMaxSpeedTow(Short.parseShort(speedLimitStr));
				} catch (NumberFormatException e) {
					log.error("Could not parse speed limit: " + speedLimitOriginStr, e);
				}
			} else if (speedLimitOriginStr.contains("mp/h")) {
				speedLimitStr = speedLimitOriginStr.replace("mp/h", "").trim();
				try {
					segment.setMaxSpeedTow((short) (miles2km * Short.parseShort(speedLimitStr)));
				} catch (NumberFormatException e) {
					log.error("Could not parse speed limit: " + speedLimitOriginStr, e);
				}
			}
		}
		
		// TODO: Participants...
	}

}
