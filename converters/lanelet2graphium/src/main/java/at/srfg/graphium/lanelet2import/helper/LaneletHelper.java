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
import com.vividsolutions.jts.linearref.LengthLocationMap;
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
	 * parses the type and optionally the subtype of a lanelet border 
	 * @param way
	 * @return
	 */
	public static SimpleEntry<String, String> parseLaneletBorderType(Way way) {
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
	 * parses the type and optionally the subtype of a lanelet border 
	 * @param way
	 * @return
	 */
	public static String[] parseBorderLaneChange(Way way) {
		String laneChange = null;
		String laneChangeLeft = null;
		String laneChangeRight = null;
		
		for (Tag tag : way.getTags()) {
			if (Constants.LANELET_LANE_CHANGE.equals(tag.getKey().toLowerCase())) {
				laneChange = tag.getValue();
			}
			if ((Constants.LANELET_LANE_CHANGE + ":left").equals(tag.getKey().toLowerCase())) {
				laneChangeLeft = tag.getValue();
			}
			if ((Constants.LANELET_LANE_CHANGE + ":right").equals(tag.getKey().toLowerCase())) {
				laneChangeRight = tag.getValue();
			}
		}
		if (laneChangeLeft == null) {
			laneChangeLeft = laneChange;
		}
		if (laneChangeRight == null) {
			laneChangeRight = laneChange;
		}
		
		return new String[] { laneChangeLeft, laneChangeRight };
	}
	
	/**
	 * creates the OSM way's linestring from its given node geometries
	 * @param way
	 * @param nodes
	 * @param srid
	 * @return
	 */
	public static LineString createLinestring(Way way, TLongObjectHashMap<Node> nodes, int srid) {
		if (way == null || way.getWayNodes() == null) {
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
	 * based on https://github.com/fzi-forschungszentrum-informatik/Lanelet2/blob/master/lanelet2_core/src/Lanelet.cpp
	 * @param segment
	 * @param lineDirections
	 * @return
	 */
	public static LineString calculateCenterline(IHDWaySegment segment, boolean[] lineDirections) {
		Coordinate[] coordsLeft = segment.getLeftBorderGeometry().getCoordinates();
		Coordinate[] coordsRight = segment.getRightBorderGeometry().getCoordinates();
		
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
		
		int indexLeft = 0;
		int indexRight = 0;
		
		if (coordsLeft[indexLeft].equals(coordsRight[indexRight])) {
			// equal coordinate
			indexLeft++;
		}
		
		while (indexLeft + 1 < coordsLeft.length || indexRight + 1 < coordsRight.length) {
			//Find nearest coordinate on opposite bounds
			int leftCandidateIndex = findNearestIndex(coordsLeft, coordsRight[indexRight], indexLeft + 1, centerlineCoordinates.get(centerlineCoordinates.size() - 1), segment);
			int rightCandidateIndex = findNearestIndex(coordsRight, coordsLeft[indexLeft], indexRight + 1, centerlineCoordinates.get(centerlineCoordinates.size() - 1), segment);
			
			Double leftCandidateDistance = null;
			Double rightCandidateDistance = null;
			if (leftCandidateIndex >= 0) {
				leftCandidateDistance = GeometryUtils.distanceAndoyer(coordsRight[indexRight], coordsLeft[leftCandidateIndex]) / 2;
			}
			if (rightCandidateIndex >= 0) {
				rightCandidateDistance = GeometryUtils.distanceAndoyer(coordsLeft[indexLeft], coordsRight[rightCandidateIndex]) / 2;
			}
			
			if (leftCandidateDistance != null && rightCandidateDistance != null) {
				// use left and right candidate to avoid steps
//				x = (coordsLeft[indexLeft].x + coordsRight[rightCandidateIndex].x
//						+ coordsLeft[leftCandidateIndex].x + coordsRight[indexRight].x) / 4;
//				y = (coordsLeft[indexLeft].y + coordsRight[indexRight].y
//						+ coordsLeft[leftCandidateIndex].y + coordsRight[rightCandidateIndex].y) / 4;
//				addToCenterline(centerlineCoordinates, x, y);
				x = (coordsLeft[leftCandidateIndex].x + coordsRight[rightCandidateIndex].x) / 2;
				y = (coordsLeft[leftCandidateIndex].y + coordsRight[rightCandidateIndex].y) / 2;
				addToCenterline(centerlineCoordinates, x, y);
				indexLeft = leftCandidateIndex;
				indexRight = rightCandidateIndex;
				
			} else if (leftCandidateDistance != null && (rightCandidateDistance == null || leftCandidateDistance <= rightCandidateDistance)) {
				// index on right bound is behind index on left bound
				for (int i=indexLeft; i<=leftCandidateIndex; i++) {
					x = (coordsLeft[i].x + coordsRight[indexRight].x) / 2;
					y = (coordsLeft[i].y + coordsRight[indexRight].y) / 2;
					addToCenterline(centerlineCoordinates, x, y);
				}
				indexLeft = leftCandidateIndex;
				
			} else if (rightCandidateDistance != null && (leftCandidateDistance == null || rightCandidateDistance < leftCandidateDistance)) {
				// index on left bound is behind index on right bound
				for (int i=indexRight; i<=rightCandidateIndex; i++) {
					x = (coordsLeft[indexLeft].x + coordsRight[i].x) / 2;
					y = (coordsLeft[indexLeft].y + coordsRight[i].y) / 2;
					addToCenterline(centerlineCoordinates, x, y);
				}
				indexRight = rightCandidateIndex;
				
			} else {
				break;
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

	/**
	 * 
	 * @param candidateCoordinates list of coordinates (e.g. line string coordinates) 
	 * @param otherCoordinate
	 * @param minIndex minimum index to return
	 * @param lastCenterlineCoordinate
	 * @return index of list item in coordinates with shortest distance to coordinate
	 */
	private static int findNearestIndex(Coordinate[] candidateCoordinates,
			Coordinate otherCoordinate, int minIndex, Coordinate lastCenterlineCoordinate,
			IHDWaySegment segment) {
		double minimumDistance = Double.MAX_VALUE;
		int closestCoordinateIndex = -1;
		double distanceLastOther = GeometryUtils.distanceAndoyer(otherCoordinate, lastCenterlineCoordinate);
		for (int j=minIndex; j<candidateCoordinates.length; j++) {
			double candidateDistance = GeometryUtils.distanceAndoyer(otherCoordinate, candidateCoordinates[j]) / 2;
			// triangle inequation to find a distance where we can not expect a closer coordinate
			if (candidateDistance - distanceLastOther > minimumDistance) {
				break;
			}
			if (candidateDistance < minimumDistance) {
				// check intersection
				double x = (candidateCoordinates[j].x + otherCoordinate.x) / 2;
				double y = (candidateCoordinates[j].y + otherCoordinate.y) / 2;
				Coordinate[] candidateArray = {lastCenterlineCoordinate, new Coordinate(x, y)};
				LineString centerline = GeometryUtils.createLineString(candidateArray, 4326);
				if (!centerline.intersects(segment.getLeftBorderGeometry()) &&
						!centerline.intersects(segment.getRightBorderGeometry())) {
					minimumDistance = candidateDistance;
					closestCoordinateIndex = j;
				}
			}
		}
		return closestCoordinateIndex;
	}

	/**
	 * 
	 * @param candidateCoordinates list of coordinates (e.g. line string coordinates) 
	 * @param otherCoordinate
	 * @param minIndex minimum index to return
	 * @return index of list item in coordinates with shortest distance to coordinate
	 */
	private static int findNearestIndex(Coordinate[] candidateCoordinates,
			Coordinate otherCoordinate, int minIndex) {
		double minimumDistance = Double.MAX_VALUE;
		int minimumDistanceIndex = -1;
		for (int j=minIndex; j<candidateCoordinates.length; j++) {
			double distance = GeometryUtils.distanceAndoyer(otherCoordinate, candidateCoordinates[j]);
			if (distance < minimumDistance) {
				minimumDistance = distance;
				minimumDistanceIndex = j;
			}
		}
		return minimumDistanceIndex;
	}

	/**
	 * determines the direction of a lanelet's borders
	 * @param segment
	 * @return boolean[]: 1st entry: true  if left borders linestring direction is in driving direction
	 * 								 false if left borders linestring direction is against driving direction
	 * 					  2nd entry: true  if right borders linestring direction is in driving direction
	 * 								 false if right borders linestring direction is against driving direction
	 */
	public static boolean[] checkLineDirections(IHDWaySegment segment) {
		Coordinate[] coordsLeft = segment.getLeftBorderGeometry().getCoordinates();
		Coordinate[] coordsRight = segment.getRightBorderGeometry().getCoordinates();
		
		if (coordsLeft[0].equals(coordsRight[0])) {
			if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[1]) > 0) {
				return new boolean[] { true, true };
			} else {
				return new boolean[] { false, false };
			}
		} else if (coordsLeft[0].equals(coordsRight[coordsRight.length-1])) {
			if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[0]) > 0) {
				return new boolean[] { true, false };
			} else {
				return new boolean[] { false, true };
			}
		} else if (coordsLeft[coordsLeft.length-1].equals(coordsRight[0])) {
			if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[1]) > 0) {
				return new boolean[] { true, false };
			} else {
				return new boolean[] { false, true };
			}
		} else if (coordsLeft[coordsLeft.length-1].equals(coordsRight[coordsRight.length-1])) {
			if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[0]) > 0) {
				return new boolean[] { true, true };
			} else {
				return new boolean[] { false, false };
			}
		} else {
			int leftIndex = -1, rightIndex = -1;
			Coordinate leftCoordinate = null, rightCoordinate = null;
			
			leftCoordinate = calculateLineCenter(segment.getLeftBorderGeometry());
			rightCoordinate = calculateLineCenter(segment.getRightBorderGeometry());
			leftIndex = findNearestIndex(segment.getLeftBorderGeometry().getCoordinates(), leftCoordinate, 0);
			rightIndex = findNearestIndex(segment.getRightBorderGeometry().getCoordinates(), rightCoordinate, 0);
			
			if (leftIndex + 1 >= segment.getLeftBorderGeometry().getCoordinates().length) {
				leftIndex = segment.getLeftBorderGeometry().getCoordinates().length - 2;
			}
			if (rightIndex + 1 >= segment.getRightBorderGeometry().getCoordinates().length) {
				rightIndex = segment.getRightBorderGeometry().getCoordinates().length - 2;
			}
			
			Coordinate[] coords0 = new Coordinate[] { coordsLeft[leftIndex], coordsRight[rightIndex] };
			Coordinate[] coords1 = new Coordinate[] { coordsLeft[leftIndex + 1], coordsRight[rightIndex + 1] };
			LineString line0 = GeometryUtils.createLineString(coords0, 4236);
			LineString line1 = GeometryUtils.createLineString(coords1, 4236);
			if (!line0.crosses(line1)
					&& !line0.crosses(segment.getLeftBorderGeometry())
					&& !line0.crosses(segment.getRightBorderGeometry())) {
				if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[rightIndex]) > 0) {
					return new boolean[] { true, true };
				} else {
					return new boolean[] { false, false };
				}
			} else {
				if (calculateOffset(segment.getLeftBorderGeometry(), coordsRight[rightIndex]) > 0) {
					return new boolean[] { true, false };
				} else {
					return new boolean[] { false, true };
				}
			}
		}
	}
	
	/**
	 * Calculates the distance between the linestring and the point
	 * 
	 * @param linestring
	 * @param coordinate
	 * @return distance between the linestring and the coordinate; negative value if
	 *         coordinate is left of linestring, positive value if coordinate is right of
	 *         linestring
	 */
	private static double calculateOffset(LineString linestring, Coordinate coordinate) {
		LinearLocation locationClosestPoint = new LocationIndexedLine(linestring).project(coordinate);
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
		double direction = (coordinate.x - a.x) * (b.y - a.y) - (coordinate.y - a.y) * (b.x - a.x);
		
		return (direction < 0) ? -1 : 1;
	}
	
	private static Coordinate calculateLineCenter(LineString lineString) {
		LocationIndexedLine indexedLine = new LocationIndexedLine(lineString);
		LinearLocation linearLocation = LengthLocationMap.getLocation(lineString, lineString.getLength() * 0.5);
		return indexedLine.extractPoint(linearLocation);
	}

}