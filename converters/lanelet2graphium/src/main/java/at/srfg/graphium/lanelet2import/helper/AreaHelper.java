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
package at.srfg.graphium.lanelet2import.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.geomutils.GeometryUtils;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class AreaHelper {
	private static Logger log = LoggerFactory.getLogger(AreaHelper.class);
	
	/**
	 * creates the OSM way's polygon from its given node geometries
	 * Currently only one polygon part without inner parts is supported
	 * TODO check if this can be replaced with third party library
	 * @param way
	 * @param nodes
	 * @param srid
	 * @return
	 */
	public static Polygon createPolygon(Relation relation, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes, int srid) {
		Set<Way> unassignedWays = new HashSet<Way>();
		
		List<Coordinate> coordinates = new ArrayList<>();
		WayNode nodeA = null;
		WayNode nodeB = null;
		
		for (RelationMember member : relation.getMembers()) {
			if (member.getMemberType().equals(EntityType.Way)) {
				if (member.getMemberRole().equals("outer")) { // TODO multiple polygon parts
					Way way = ways.get(member.getMemberId());
					
					if (way != null) {
						if (coordinates.size() == 0) {
							// set initial coordinates
							nodeA = way.getWayNodes().get(0);
							nodeB = way.getWayNodes().get(way.getWayNodes().size() - 1);
							coordinates.addAll(getCoordinates(nodes, way, false));
						} else {
							// add to unassigned ways
							unassignedWays.add(way);
						}
					} else {
						log.error("Relation " + relation.getId() + ": Way " + member.getMemberId() + " is null");
						return null;
					}
				} else {
					// TODO inner parts
				}
			} else {
				log.error("Relation " + relation.getId() + ": Member " + member.getMemberId() + " is not a Way");
				return null;
			}
		}
		
		while (nodeA.getNodeId() != nodeB.getNodeId()) {
			nodeB = findNextWay(nodeB, unassignedWays, nodes, coordinates);
			if (nodeB == null) {
				log.error("Relation " + relation.getId() + ": Ring not closed");
				break;
			}
		}
		
		if (coordinates.size() < 4) {
			return null;
		}
		
		return GeometryUtils.createPolygon(coordinates.toArray(new Coordinate[0]), srid);
	}

	private static WayNode findNextWay(WayNode node, Set<Way> unassignedWays, TLongObjectHashMap<Node> nodes, List<Coordinate> coordinates) {
		Way nextWay = null;
		boolean reversed = false;
		for (Way way : unassignedWays) {
			if (node.getNodeId() == way.getWayNodes().get(0).getNodeId()) {
				nextWay = way;
				break;
				
			} else if (node.getNodeId() == way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()) {
				nextWay = way;
				reversed = true;
				break;
			}
		}
		if (nextWay != null ) {
			unassignedWays.remove(nextWay);
			coordinates.addAll(getCoordinates(nodes, nextWay, reversed));
			if (!reversed) {
				return nextWay.getWayNodes().get(nextWay.getWayNodes().size() - 1);
			} else {
				return nextWay.getWayNodes().get(0);
			}
		}
		return null;
	}

	private static List<Coordinate> getCoordinates(TLongObjectHashMap<Node> nodes, Way way, boolean reversed) {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		if (!reversed) {
			for (WayNode wayNode : way.getWayNodes()) {
				Node node = nodes.get(wayNode.getNodeId());
				if (node != null) {
					coordinates.add(new Coordinate(node.getLongitude(), node.getLatitude()));
				} else {
					log.error("Cannot find node " + wayNode.getNodeId() + " for way " + way.getId());
					return null;
				}
			}
		} else {
			for (int j=way.getWayNodes().size()-1; j>=0; j--) {
				Node node = nodes.get(way.getWayNodes().get(j).getNodeId());
				if (node != null) {
					coordinates.add(new Coordinate(node.getLongitude(), node.getLatitude()));
				} else {
					log.error("Cannot find node " + way.getWayNodes().get(j).getNodeId() + " for way " + way.getId());
					return null;
				}
			}
		}
		return coordinates;
	}
}
