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
package at.srfg.graphium.lanelet2import.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.model.hd.IHDWaySegment;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletHelper {

	/**
	 * extracts the type of an OSM entity
	 * @param entity
	 * @return
	 */
	public static String getType(Entity entity) {
		String type = null;
		
		for (Tag tag : entity.getTags()) {
			if ("type".equals(tag.getKey().toLowerCase())) {
				type = tag.getValue();
			}
		}
		
		return type;
	}
	
	/**
	 * extracts the subtype of an OSM entity
	 * @param entity
	 * @return
	 */
	public static String getSubtype(Entity entity) {
		String type = null;
		
		for (Tag tag : entity.getTags()) {
			if ("subtype".equals(tag.getKey().toLowerCase())) {
				type = tag.getValue();
			}
		}
		
		return type;
	}
	
	/**
	 * parses the type and optionally the subtype of a lanelet boarder 
	 * @param way
	 * @return
	 */
	public static SimpleEntry<String, String> parseLaneletBoarderType(Way way) {
		String type = null;
		String subType = null;
		SimpleEntry<String, String> pair = null;
		
		for (Tag tag : way.getTags()) {
			if ("type".equals(tag.getKey().toLowerCase())) {
				type = tag.getValue();
			}
			if ("subtype".equals(tag.getKey().toLowerCase())) {
				subType = tag.getValue();
			}
		}
		
		if (type != null) {
			pair = new SimpleEntry<>(type, subType);
		}

		return pair;
	}
	
	/**
	 * creates the OSM way's linestring from its given node geometries
	 * @param way
	 * @param nodes
	 * @param srid
	 * @return
	 */
	public static LineString createLinestring(Way way, TLongObjectHashMap<Node> nodes, int srid) {
		if (way.getWayNodes() == null) {
			return null;
		}
		
		List<Coordinate> coords = new ArrayList<>();
		int i = 0;
		for (WayNode wayNode : way.getWayNodes()) {
			Node node = nodes.get(wayNode.getNodeId());
			if (node != null) {
				coords.add(new Coordinate(node.getLongitude(), node.getLatitude()));
				i++;
			} else {
				return null;
			}
		}
		
		if (i < 2) {
			return null;
		}
		
		return GeometryUtils.createLineString(coords.toArray(new Coordinate[0]), srid);
	}
	
	/**
	 * calculates the centerline of a lanelet according to left and right bounds linestrings
	 * @param segment
	 * @param lineDirections
	 * @return
	 */
	public static LineString calculateCenterline(IHDWaySegment segment, boolean[] lineDirections) {
		Coordinate[] coordsLeft = segment.getLeftBoarderGeometry().getCoordinates();
		Coordinate[] coordsRight = segment.getRightBoarderGeometry().getCoordinates();
		
		if (!lineDirections[0]) {
			coordsLeft = reverseCoordinates(coordsLeft);
		}
		if (!lineDirections[1]) {
			coordsRight = reverseCoordinates(coordsRight);
		}
		
		List<Coordinate> centerlineCoordinates = new ArrayList<Coordinate>();
		
		// Add first coordinate
		double x = (coordsLeft[0].x + coordsRight[0].x) / 2;
		double y = (coordsLeft[0].y + coordsRight[0].y) / 2;
		addToCenterline(centerlineCoordinates, x, y);
		
		int indexLeft = 1;
		int indexRight = 1;
		
		if (coordsLeft[indexLeft].equals(coordsRight[indexRight])) {
			// equal coordinate
			indexRight++;
		}
		
//		for(int i=1; i<coords1.length-1; i++) {
		while (indexLeft < coordsLeft.length && indexRight < coordsRight.length) {
			int indexOnRight = findNearestPartner(coordsRight, coordsLeft[indexLeft]);
			int indexOnLeft = findNearestPartner(coordsLeft, coordsRight[indexRight]);
			
			if (indexOnRight > indexRight) {
				x = (coordsRight[indexRight].x + coordsLeft[indexOnLeft].x) / 2;
				y = (coordsRight[indexRight].y + coordsLeft[indexOnLeft].y) / 2;
				addToCenterline(centerlineCoordinates, x, y);
				indexRight++;
				if (indexLeft < indexOnLeft) {
					indexLeft = indexOnLeft;
				}
			} else if (indexOnLeft > indexLeft) {
				x = (coordsLeft[indexLeft].x + coordsRight[indexOnRight].x) / 2;
				y = (coordsLeft[indexLeft].y + coordsRight[indexOnRight].y) / 2;
				addToCenterline(centerlineCoordinates, x, y);
				indexLeft++;
				if (indexLeft < indexOnLeft) {
					indexLeft = indexOnLeft;
				}
			} else {
				x = (coordsLeft[indexLeft].x + coordsRight[indexRight].x) / 2;
				y = (coordsLeft[indexLeft].y + coordsRight[indexRight].y) / 2;
				addToCenterline(centerlineCoordinates, x, y);
				indexLeft++;
				indexRight++;
			}
		}
		
		// Add last coordinate
		x = (coordsLeft[coordsLeft.length-1].x + coordsRight[coordsRight.length-1].x) / 2;
		y = (coordsLeft[coordsLeft.length-1].y + coordsRight[coordsRight.length-1].y) / 2;
		addToCenterline(centerlineCoordinates, x, y);
		
		//Create centerline
		Coordinate[] centerlineCoordinateArray = centerlineCoordinates.toArray(new Coordinate[centerlineCoordinates.size()]);
		LineString centerline = GeometryUtils.createLineString(centerlineCoordinateArray, 4326);
		return centerline;
	}

	private static Coordinate[] reverseCoordinates(Coordinate[] coords1) {
		Coordinate[] coords = new Coordinate[coords1.length];
		for (int i=coords.length-1;i>=0;i--) {
			coords[i] = coords1[coords1.length - i - 1];
		}
		return coords;
	}

	private static void addToCenterline(List<Coordinate> centerlineCoordinates, double x, double y) {
		if (centerlineCoordinates.size() > 0) {
			Coordinate lastCoord = centerlineCoordinates.get(centerlineCoordinates.size() - 1);
			if (lastCoord.x == x && lastCoord.y == y) {
//				log.info("skip coordinate");
				return;
			}
		}
		centerlineCoordinates.add(new Coordinate(x ,y));
	}

	private static int findNearestPartner(Coordinate[] coordinates, Coordinate coordinate) {
		double minimumDistance = Double.MAX_VALUE;
		int minimumDistanceIndex = -1;
		for (int j=0; j<coordinates.length; j++) {
			double distance = GeometryUtils.distanceAndoyer(coordinate, coordinates[j]);
			if (distance < minimumDistance) {
				minimumDistance = distance;
				minimumDistanceIndex = j;
			}
		}
		return minimumDistanceIndex;
	}

	/**
	 * determines the direction of a lanelet's boarders
	 * @param segment
	 * @return boolean[]: 1st entry: true  if left boarders linestring direction is in driving direction
	 * 								 false if left boarders linestring direction is against driving direction
	 * 					  2nd entry: true  if right boarders linestring direction is in driving direction
	 * 								 false if right boarders linestring direction is against driving direction
	 */
	public static boolean[] checkLineDirections(IHDWaySegment segment) {
		Coordinate[] coordsLeft = segment.getLeftBoarderGeometry().getCoordinates();
		Coordinate[] coordsRight = segment.getRightBoarderGeometry().getCoordinates();
		
		Coordinate[] coords1 = new Coordinate[] {coordsLeft[0], coordsRight[0]};
		Coordinate[] coords2 = new Coordinate[] {coordsLeft[coordsLeft.length-1], coordsRight[coordsRight.length-1]};
		LineString line1 = GeometryUtils.createLineString(coords1, 4236);
		LineString line2 = GeometryUtils.createLineString(coords2, 4236);
		if (!line1.intersects(line2)) {
			if (calculateOffset(segment.getLeftBoarderGeometry(), segment.getRightBoarderGeometry().getStartPoint()) > 0) {
				return new boolean[] { true, true };
			} else {
				return new boolean[] { false, false };
			}
		} else {
			if (calculateOffset(segment.getLeftBoarderGeometry(), segment.getRightBoarderGeometry().getEndPoint()) > 0) {
				return new boolean[] { true, false };
			} else {
				return new boolean[] { false, true };
			}
		}
	}
	
	private static double calculateOffset(LineString linestring, Point point) {
		LinearLocation locationClosestPoint = new LocationIndexedLine(linestring).project(point.getCoordinate());
		Coordinate a = null;
		Coordinate b = null;
		if (locationClosestPoint.getSegmentIndex() < linestring.getCoordinates().length - 1) {
			a = linestring.getCoordinateN(locationClosestPoint.getSegmentIndex());
			b = linestring.getCoordinateN(locationClosestPoint.getSegmentIndex() + 1);
		} else {
			a = linestring.getCoordinateN(locationClosestPoint.getSegmentIndex() - 1);
			b = linestring.getCoordinateN(locationClosestPoint.getSegmentIndex());
		}
		
		//https://math.stackexchange.com/a/274728
		double direction = (point.getCoordinate().x - a.x) * (b.y - a.y) - (point.getCoordinate().y - a.y) * (b.x - a.x);
		double distance = GeometryUtils.distanceMeters(linestring, point);
		
		return ((direction < 0) ? -1 : 1) * distance;
	}

}