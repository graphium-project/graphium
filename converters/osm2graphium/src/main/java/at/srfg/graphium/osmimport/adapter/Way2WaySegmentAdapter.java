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
package at.srfg.graphium.osmimport.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.geomutils.GeometryUtils;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IWayGraphModelFactory;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WayGraphModelFactory;
import at.srfg.graphium.osmimport.helper.WayHelper;
import at.srfg.graphium.osmimport.model.impl.NodeCoord;
import at.srfg.graphium.osmimport.segmentation.WaySegmenter;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class Way2WaySegmentAdapter {
	
	private static Logger log = LoggerFactory.getLogger(Way2WaySegmentAdapter.class);

	private IWayGraphModelFactory<IWaySegment> segmentFactory;
	private int srid = 4326;
	private Set<Access> defaultAccesses = null;
	
	public Way2WaySegmentAdapter() {
		segmentFactory = new WayGraphModelFactory();
		defaultAccesses = Access.getAccessTypes(new int[]{3});
	}
	
	public IWaySegment adapt(Way way, TLongObjectHashMap<NodeCoord> nodes) {
		
		if (way == null) {
			log.error("Way is null");
			return null;
		}
		if (way.getWayNodes() == null) {
			log.error("Way " + way.getId() + " has no way nodes");
			return null;
		}
		
		List<Coordinate> coords = new ArrayList<>();
		int i = 0;
		for (WayNode wayNode : way.getWayNodes()) {
			NodeCoord node = nodes.get(wayNode.getNodeId());
			if (node != null) {
				coords.add(new Coordinate(node.getX(), node.getY()));
				i++;
			} else {
				log.error("Node " + wayNode.getNodeId() + " not found in cache");
			}
		}
		
		if (i < 2) {
			log.error("Node " + way.getId() + " has no valid geometry");
			return null;
		}
		
		LineString geometry = GeometryUtils.createLineString(coords.toArray(new Coordinate[0]), srid);
		
		Map<String, String> tags = WayHelper.createTagMap(way);
		
		Long originalWayId;
		try {
			originalWayId = Long.parseLong(tags.get(WaySegmenter.ORIG_WAY_ID));
		} catch (NumberFormatException e) {
			originalWayId = way.getId();
		}
		
		IWaySegment segment = segmentFactory.newSegment(
				way.getId(), 
				geometry,
				(float)geometry.getLength(),
				null,
				null,
				originalWayId,
				way.getWayNodes().get(0).getNodeId(),
				// TODO: startNodeIndex,
				0,
				way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId(),
				// TODO: endNodeIndex,
				way.getWayNodes().size()-1,
				null,
				null,
				null);
		
		setAccess(segment, tags);
		setName(segment, tags);
		setFrc(segment, tags);
		setFormOfWay(segment, tags);
		setSpeed(segment, tags);
		setLanes(segment, tags);
		setBridge(segment, tags);
		setTunnel(segment, tags);
		setUrban(segment, tags);
		
		// TODO: urban
				
		return segment;
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
		
	private void setAccess(IWaySegment segment, Map<String, String> tags) {
		// check oneway
		byte oneway = WayHelper.checkOneway(tags);
		if (oneway == 1) {
			segment.setAccessBkw(null);
			segment.setAccessTow(defaultAccesses);
		} else if (oneway == 2) {
			segment.setAccessTow(null);
			segment.setAccessBkw(defaultAccesses);
		} else {
			segment.setAccessTow(defaultAccesses);
			segment.setAccessBkw(defaultAccesses);
		}
	}

	private void setFormOfWay(IWaySegment segment, Map<String, String> tags) {
		if (tags.containsKey("junction") && tags.get("junction").equals("roundabout")) {
			segment.setFormOfWay(FormOfWay.PART_OF_ROUNDABOUT);
		} else if (tags.containsKey("highway")) { 
			String highway = tags.get("highway");
			if (highway.equals("motorway")) {
				segment.setFormOfWay(FormOfWay.PART_OF_MOTORWAY);
			} else if (highway.equals("trunk")) {
				segment.setFormOfWay(FormOfWay.PART_OF_MULTI_CARRIAGEWAY_WHICH_IS_NOT_A_MOTORWAY);
			} else if (highway.equals("primary") || highway.equals("secondary") || highway.equals("tertiary")) {
				segment.setFormOfWay(FormOfWay.PART_OF_SINGLE_CARRIAGEWAY);
			} else if (highway.equals("motorway_link") || highway.equals("primary_link") || 
					   highway.equals("secondary_link") || highway.equals("tertiary_link")) {
				segment.setFormOfWay(FormOfWay.PART_OF_A_SLIP_ROAD);
			} else {
				segment.setFormOfWay(FormOfWay.NOT_APPLICABLE);
			}
		} else {
			segment.setFormOfWay(FormOfWay.NOT_APPLICABLE);
		}
	}

	private void setFrc(IWaySegment segment, Map<String, String> tags) {
		if (tags.containsKey("highway")) { 
			String highway = tags.get("highway");
			if (highway.equals("motorway") || highway.equals("motorway_link")) {
				segment.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
			} else if (highway.equals("trunk") || highway.equals("trunk_link")) {
				segment.setFrc(FuncRoadClass.MAJOR_ROAD_LESS_IMORTANT_THAN_MOTORWAY);
			} else if (highway.equals("primary") || highway.equals("primary_link")) {
				segment.setFrc(FuncRoadClass.OTHER_MAJOR_ROAD);
			} else if (highway.equals("secondary") || highway.equals("secondary_link")) {
				segment.setFrc(FuncRoadClass.SECONDARY_ROAD);
			} else if (highway.equals("tertiary") || highway.equals("tertiary_link")) {
				segment.setFrc(FuncRoadClass.LOCAL_CONNECTING_ROAD);
			} else {
				segment.setFrc(FuncRoadClass.SONSTIGE_STRASSEN);
			}
		} else {
			segment.setFrc(FuncRoadClass.NOT_APPLICABLE);
		}
	}

	private void setSpeed(IWaySegment segment, Map<String, String> tags) {
		// default
		short maxSpeedTow;
		short maxSpeedBkw;
		if (segment.getFrc().equals(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY)) {
			maxSpeedTow = (short)130;
			maxSpeedBkw = (short)130;
		} else if (segment.getFrc().equals(FuncRoadClass.MAJOR_ROAD_LESS_IMORTANT_THAN_MOTORWAY)) {
			maxSpeedTow = (short)100;
			maxSpeedBkw = (short)100;
		} else if (segment.getFrc().equals(FuncRoadClass.OTHER_MAJOR_ROAD)) {
			maxSpeedTow = (short)100;
			maxSpeedBkw = (short)100;
		} else {
			maxSpeedTow = (short)50;
			maxSpeedBkw = (short)50;
		}

		if (tags.containsKey("maxspeed")) {
			short maxSpeed = 50;
			try {
				maxSpeed = Short.parseShort(tags.get("maxspeed"));
			} catch (NumberFormatException e) {
				switch (tags.get("maxspeed")) {
				case "AT:urban":
					maxSpeed = 50;
					break;
				case "AT:rural":
					maxSpeed = 100;
					break;
				case "AT:trunk":
					maxSpeed = 100;
					break;
				case "AT:motorway":
					maxSpeed = 130;
					break;
				case "CH:urban":
					maxSpeed = 50;
					break;
				case "CH:rural":
					maxSpeed = 80;
					break;
				case "CH:trunk":
					maxSpeed = 100;
					break;
				case "CH:motorway":
					maxSpeed = 120;
					break;
				case "DE:livingstreet":
					maxSpeed = 7;
					break;
				case "DE:urban":
					maxSpeed = 50;
					break;
				case "DE:rural":
					maxSpeed = 100;
					break;
				case "DE:motorway":
					maxSpeed = 130; // officially unlimited
					break;
				case "IT:urban":
					maxSpeed = 50;
					break;
				case "IT:rural":
					maxSpeed = 90;
					break;
				case "IT:trunk":
					maxSpeed = 110;
					break;
				case "IT:motorway":
					maxSpeed = 130;
					break;

				default:
					break;
				}
			}
			maxSpeedTow = maxSpeed;
			maxSpeedBkw = maxSpeed;
		}
		if (tags.containsKey("maxspeed:forward")) {
			try {
				maxSpeedTow = Short.parseShort(tags.get("maxspeed:forward"));
			} catch (NumberFormatException e) {
			}
		}
		if (tags.containsKey("maxspeed:backward")) {
			try {
				maxSpeedBkw = Short.parseShort(tags.get("maxspeed:backward"));
			} catch (NumberFormatException e) {
			}
		}
		
		if (segment.getAccessTow() == null) {
			segment.setMaxSpeedTow((short) -1);
			segment.setSpeedCalcTow((short) -1);
		} else {
			segment.setMaxSpeedTow(maxSpeedTow);
			segment.setSpeedCalcTow(maxSpeedTow);
		}
		
		if (segment.getAccessBkw() == null) {
			segment.setMaxSpeedBkw((short) -1);
			segment.setSpeedCalcBkw((short) -1);
		} else {
			segment.setMaxSpeedBkw(maxSpeedBkw);
			segment.setSpeedCalcBkw(maxSpeedBkw);
		}

	}

	private void setLanes(IWaySegment segment, Map<String, String> tags) {
		short lanesTow = 1;
		short lanesBkw = 1;
		if (tags.containsKey("lanes")) {
			try {
				short lanes = Short.parseShort(tags.get("lanes"));
				lanes = (short) (lanes / 2);
				if (lanes == 0) {
					lanes = 1;
				}
				lanesTow = lanes;
				lanesBkw = lanes;
			} catch (NumberFormatException e) {
			}
		}
		if (tags.containsKey("lanes:forward")) {
			try {
				lanesTow = Short.parseShort(tags.get("lanes:forward"));
			} catch (NumberFormatException e) {
			}
		}
		if (tags.containsKey("lanes:backward")) {
			try {
				lanesBkw = Short.parseShort(tags.get("lanes:backward"));
			} catch (NumberFormatException e) {
			}
		}
		
		if (segment.getAccessTow() == null) {
			segment.setLanesTow((short) -1);
			segment.setLanesTow((short) -1);
		} else {
			segment.setLanesTow(lanesTow);
			segment.setLanesTow(lanesTow);
		}
		
		if (segment.getAccessBkw() == null) {
			segment.setLanesBkw((short) -1);
			segment.setLanesBkw((short) -1);
		} else {
			segment.setLanesBkw(lanesBkw);
			segment.setLanesBkw(lanesBkw);
		}
	}

	private void setBridge(IWaySegment segment, Map<String, String> tags) {
		if (tags.containsKey("bridge")) {
			segment.setBridge(true);
		} else {
			segment.setBridge(false);
		}
	}

	private void setTunnel(IWaySegment segment, Map<String, String> tags) {
		if (tags.containsKey("tunnel")) {
			segment.setTunnel(true);
		} else {
			segment.setTunnel(false);
		}
	}

	private void setUrban(IWaySegment segment, Map<String, String> tags) {
		segment.setUrban(false);
	}

	public IWayGraphModelFactory<IWaySegment> getSegmentFactory() {
		return segmentFactory;
	}

	public void setSegmentFactory(IWayGraphModelFactory<IWaySegment> segmentFactory) {
		this.segmentFactory = segmentFactory;
	}

	public int getSrid() {
		return srid;
	}

	public void setSrid(int srid) {
		this.srid = srid;
	}
	
}
