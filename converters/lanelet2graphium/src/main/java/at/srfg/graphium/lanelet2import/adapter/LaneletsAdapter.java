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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.LaneletHelper;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDWaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletsAdapter {
	
	
	// TODO: Sollen wir die Border-Linestrings umdrehen, falls die Digitalisierungsrichtung nicht mit der Fahrtrichtung zusammenpasst?
	

	private static Logger log = LoggerFactory.getLogger(LaneletsAdapter.class);
	
	private long nodeIdCounter = -1;
	//TODO replace maps with spatial index
	private Map<Double, Map<Double, Long>> nodeIdRepository = null; // Map<Longitudes, Map<Latitudes, nodeId>>

	private float miles2km = 1.60934f;
	
	public List<IHDWaySegment> adaptLanelets(List<Relation> relations, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		List<IHDWaySegment> segments = new ArrayList<>();
		
		nodeIdCounter = -1;
		nodeIdRepository = new HashMap<Double, Map<Double, Long>>();
		
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
		
		Way leftBorder = null;
		Way rightBorder = null;
		
		for (RelationMember member : relation.getMembers()) {
			if (member.getMemberType().equals(EntityType.Way)) {
				String role = member.getMemberRole();
				if (role.equals("left")) {
					leftBorder = ways.get(member.getMemberId());
					if (leftBorder == null) {
						log.error("Way " + member.getMemberId() + " is null");
						return null;
					}
				} else if (role.equals("right")) {
					rightBorder = ways.get(member.getMemberId());
					if (rightBorder == null) {
						log.error("Way " + member.getMemberId() + " is null");
						return null;
					}
				}
			}
		}
		
		segment.setLeftBorderGeometry(LaneletHelper.createLinestring(leftBorder, nodes, Constants.SRID));
		segment.setLeftBorderStartNodeId(leftBorder.getWayNodes().get(0).getNodeId());
		segment.setLeftBorderEndNodeId(leftBorder.getWayNodes().get(leftBorder.getWayNodes().size()-1).getNodeId());
		segment.setRightBorderGeometry(LaneletHelper.createLinestring(rightBorder, nodes, Constants.SRID));
		segment.setRightBorderStartNodeId(rightBorder.getWayNodes().get(0).getNodeId());
		segment.setRightBorderEndNodeId(rightBorder.getWayNodes().get(rightBorder.getWayNodes().size()-1).getNodeId());

		boolean[] borderDirections = checkBorderInversion(segment);
		
		determineLaneChanges(segment, borderDirections, leftBorder, rightBorder);
		
		// build tag map
		Map<String, String> tags = new HashMap<>();
		relation.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
		
		setRoadCharacteristics(segment, tags);
		setAccesses(segment, tags);
		setOnewayAttributes(segment, tags);
		
//		setAccess(segment, tags);
		setOptionalTags(segment, tags);
		
		validateTags(segment);
		
		calculateCenterLine(segment, borderDirections);

		return segment;
	}

	private void validateTags(IHDWaySegment segment) {
		// null values are forbidden
		for (String key : segment.getTags().keySet()) {
			String value = segment.getTags().get(key);
			if (value == null) {
				segment.getTags().put(key, "");
			}
		}
	}

	private void calculateCenterLine(IHDWaySegment segment, boolean[] borderDirections) {
		LineString centerline = LaneletHelper.calculateCenterline(segment, borderDirections);
		segment.setGeometry(centerline);
		segment.setStartNodeId(findOrCreateNodeId(centerline.getCoordinateN(0)));
		segment.setEndNodeId(findOrCreateNodeId(centerline.getCoordinateN(centerline.getCoordinates().length - 1)));
		segment.setStartNodeIndex(0);
		segment.setEndNodeIndex(centerline.getCoordinates().length - 1);
	}
	
	private long findOrCreateNodeId(Coordinate coordinate) {
		Map<Double, Long> latitudes = nodeIdRepository.get(coordinate.x);
		if (latitudes == null) {
			latitudes = new HashMap<Double, Long>();
			nodeIdRepository.put(coordinate.x, latitudes);
		}
		Long nodeId = latitudes.get(coordinate.y);
		if (nodeId == null) {
			nodeId = ++nodeIdCounter;
			latitudes.put(coordinate.y, nodeId);
		}
		return nodeId.longValue();
	}

	private void setRoadCharacteristics(IHDWaySegment segment, Map<String, String> tags) {
		// see https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Attribute.cpp
		String roadType = tags.get("subtype");
		if (roadType == null) {
			roadType = "road"; // default
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

	private void setOnewayAttributes(IHDWaySegment segment, Map<String, String> tags) {
		// see https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Attribute.cpp
		segment.setLanesTow((short)1);

		Set<Access> accesses = new HashSet<>();
		boolean oneway = convertAccesses(tags, Constants.LANELET_ONEWAY, "no", accesses);

		if (!accesses.isEmpty()) {
			segment.setAccessBkw(accesses);
			segment.setLanesBkw((short)1);
		} else if (oneway) {
			// lanelet has tag "one_way=no", but no explicitly set participants
			segment.setAccessBkw(segment.getAccessTow());
			segment.setLanesBkw((short)1);
		}
		
	}

	private void determineLaneChanges(IHDWaySegment segment, boolean[] borderDirections, Way leftBorder, Way rightBorder) {
		// left border
		SimpleEntry<String, String> typeEntry = LaneletHelper.parseLaneletBorderType(leftBorder);
		String laneChangePossible = "false";
		if (typeEntry != null) {
			segment.getTags().put("left:" + typeEntry.getKey(), typeEntry.getValue());
			laneChangePossible = determineLaneChange(true, !borderDirections[0], typeEntry.getKey(), typeEntry.getValue());
		}
		segment.getTags().put(Constants.TAG_LANE_CHANGE + ":left", laneChangePossible);
		
		// right border
		typeEntry = LaneletHelper.parseLaneletBorderType(rightBorder);
		laneChangePossible = "false";
		if (typeEntry != null) {
			segment.getTags().put("right:" + typeEntry.getKey(), typeEntry.getValue());
			laneChangePossible = determineLaneChange(false, !borderDirections[1], typeEntry.getKey(), typeEntry.getValue());
		}
		segment.getTags().put(Constants.TAG_LANE_CHANGE + ":right", laneChangePossible);
	}
	
	private boolean[] checkBorderInversion(IHDWaySegment segment) {
		// Check if left and / or right border have to be inverted (linestring and nodeIds)
		boolean[] borderDirections = LaneletHelper.checkLineDirections(segment);
		// if so put a flag into tags: "invertedBorder=left/right/both")
		if (!borderDirections[0] && borderDirections[1]) {
			segment.getTags().put(Constants.BORDER_INVERTED, "left");
			invertLeftBorderNodeIds(segment);
		} else if (borderDirections[0] && !borderDirections[1]) {
			segment.getTags().put(Constants.BORDER_INVERTED, "right");
			invertRightBorderNodeIds(segment);
		} else if (!borderDirections[0] && !borderDirections[1]) {
			segment.getTags().put(Constants.BORDER_INVERTED, "both");
			invertLeftBorderNodeIds(segment);
			invertRightBorderNodeIds(segment);
		}

		// TODO: Sollen wir die Border-Linestrings umdrehen, falls die Digitalisierungsrichtung nicht mit der Fahrtrichtung zusammenpasst?
		// Zur Zeit wird der Linestring so belassen wie er im Original ist.
		
		return borderDirections;
	}
	
	private void invertLeftBorderNodeIds(IHDWaySegment segment) {
		long startNodeId = segment.getLeftBorderStartNodeId();
		segment.setLeftBorderStartNodeId(segment.getLeftBorderEndNodeId());
		segment.setLeftBorderEndNodeId(startNodeId);
	}

	private void invertRightBorderNodeIds(IHDWaySegment segment) {
		long startNodeId = segment.getRightBorderStartNodeId();
		segment.setRightBorderStartNodeId(segment.getRightBorderEndNodeId());
		segment.setRightBorderEndNodeId(startNodeId);
	}

	private String determineLaneChange(boolean leftBorder, boolean inverted, String type, String subType) {
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
					if (leftBorder) {
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
					if (leftBorder) {
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

	private void setOptionalTags(IHDWaySegment segment, Map<String, String> tags) {
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
			} else {
				speedLimitStr = speedLimitOriginStr.trim();
				try {
					segment.setMaxSpeedTow(Short.parseShort(speedLimitStr));
				} catch (NumberFormatException e) {
					log.error("Could not parse speed limit: " + speedLimitOriginStr, e);
				}
			}
		}
	}
	
	private void setAccesses(IHDWaySegment segment, Map<String, String> tags) {
		Set<Access> accesses = new HashSet<>();
		convertAccesses(tags, Constants.LANELET_PARTICIPANT, "yes", accesses);
		segment.setAccessTow(accesses);
	}
	
	/**
	 * 
	 * @param tags
	 * @param keyPrefix
	 * @param valuePositives
	 * @param accesses returns all accesses if explicitly defined
	 * @return true if condition of "keyPrefix" found and value = "valuePositives" is true
	 */
	private boolean convertAccesses(Map<String, String> tags, String keyPrefix, String valuePositives, Set<Access> accesses) {
		boolean conditionTrue = false;
		for (String key : tags.keySet()) {
			if (key.contains(keyPrefix)) {
				if (tags.get(key).equals(valuePositives)) {
					conditionTrue = true;
					
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE)) {
						accesses = allVehiclesAccesses();
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_CAR)) {
						accesses.add(Access.PRIVATE_CAR);
						accesses.add(Access.ELECTRIC_CAR);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_CAR_COMBUSTION)) {
						accesses.add(Access.PRIVATE_CAR);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_CAR_ELECTRIC)) {
						accesses.add(Access.ELECTRIC_CAR);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_BUS)) {
						accesses.add(Access.PUBLIC_BUS);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_TRUCK)) {
						accesses.add(Access.TRUCK);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_MOTORCYCLE)) {
						accesses.add(Access.MOTORCYCLE);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_TAXI)) {
						accesses.add(Access.TAXI);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_VEHICLE_EMERGENCY)) {
						accesses.add(Access.EMERGENCY_VEHICLE);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_PEDESTRIAN)) {
						accesses.add(Access.PEDESTRIAN);
					} else
					if (key.equals(keyPrefix + ":" + Constants.LANELET_BYCICLE)) {
						accesses.add(Access.BIKE);
					}
				}
			}
		}
		return conditionTrue;
	}

}