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
package at.srfg.graphium.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;

import at.srfg.geomutils.GeometryUtils;
import at.srfg.graphium.model.impl.WayGraphModelFactory;
import at.srfg.graphium.model.impl.WaySegment;

public class TestGraphModel {

	IWayGraphModelFactory<IWaySegment> objectFactory = new WayGraphModelFactory(); 
		
	// see testgraph1.jpg
	public List<IWaySegment> getTestGraph1() throws ParseException {
		List<IWaySegment> segments = new ArrayList<IWaySegment>();
		IWaySegment segment;
		
		Date segTimestamp = new Date();
		Access[] accessArray = new Access[]{Access.PRIVATE_CAR, Access.PUBLIC_BUS, Access.TRUCK, Access.TAXI,
			Access.EMERGENCY_VEHICLE, Access.MOTOR_COACH, Access.TROLLY_BUS, Access.MOTORCYCLE};
		Set<Access> accessTypes = new HashSet<Access>(Arrays.asList(accessArray));
			
				
		// segment 1 
		// LINESTRING(13.05390136178 47.805660177764, 13.053729700403 47.806132246551, 13.053944277124 47.806561399993)
		LineString geometry = (LineString) GeometryUtils.createGeometryFromWkt("LINESTRING(13.05390136178 47.805660177764, "
				+ "13.053729700403 47.806132246551, 13.053944277124 47.806561399993)", 4326);

		List<IWaySegmentConnection> connectionsStartNode = new ArrayList<IWaySegmentConnection>();
		List<IWaySegmentConnection> connectionsEndNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode.add(objectFactory.newWaySegmentConnection(2, 1, 2, accessTypes));
		connectionsEndNode.add(objectFactory.newWaySegmentConnection(2, 1, 3, accessTypes));
		
		segment = objectFactory.newSegment(1l, geometry, (float)GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(geometry), "A-666",
				(short)120, (short)0, (short)110, (short)0, (short)2, (short)0, FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY, 
				FormOfWay.PART_OF_MOTORWAY, "highway",
				1, 1, 0, 2, 2, accessTypes, 
				null, false, false, false, segTimestamp, connectionsStartNode, connectionsEndNode);
		segments.add(segment);
				
	
		
		// segment 2
		//LINESTRING(13.053944277124 47.806561399993, 13.054416345911 47.806818892058)
		geometry = (LineString) GeometryUtils.createGeometryFromWkt("LINESTRING(13.053944277124 47.806561399993, "
				+ "13.054416345911 47.806818892058)", 4326);
		
		connectionsStartNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode = new ArrayList<IWaySegmentConnection>();
		
		segment = objectFactory.newSegment(1l, geometry, (float)GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(geometry), "A-666",
				(short)120, (short)0, (short)110, (short)0, (short)2, (short)0, FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY, FormOfWay.PART_OF_MOTORWAY, "highway",
				2, 2, 0, 3, 1, accessTypes, 
				null, false, false, false, segTimestamp, connectionsStartNode, connectionsEndNode);		
		segments.add(segment);
			
		// segment 3
//		LINESTRING(13.053944277124 47.806561399993, 13.053729700403 47.806947638091)
		geometry = (LineString) GeometryUtils.createGeometryFromWkt("LINESTRING(13.053944277124 47.806561399993, "
				+ "13.053729700403 47.806947638091)", 4326);
		
		connectionsStartNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode.add(objectFactory.newWaySegmentConnection(4, 3, 4, accessTypes));
		connectionsEndNode.add(objectFactory.newWaySegmentConnection(4, 3, 5, accessTypes));
		
		segment = objectFactory.newSegment(3l, geometry, (float)GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(geometry), "from A-666", 
				(short)70, (short)0, (short)60, (short)0, (short)1, (short)0, FuncRoadClass.MAJOR_ROAD_LESS_IMORTANT_THAN_MOTORWAY, FormOfWay.PART_OF_MOTORWAY, "highway_trunk",
				3, 2, 0, 4, 1, accessTypes, 
				null, false, false, false, segTimestamp,  connectionsStartNode, connectionsEndNode);
		segments.add(segment);
		
		// add more access Types
		accessTypes.add(Access.BIKE); accessTypes.add(Access.CAMPER); accessTypes.add(Access.GARBAGE_COLLECTION_VEHICLE); 
		
		// segment 4	
//		LINESTRING(13.053073095505 47.806884695892, 13.053729700403 47.806947638091)
		geometry = (LineString) GeometryUtils.createGeometryFromWkt("LINESTRING(13.053073095505 47.806884695892, "
				+ "13.053729700403 47.806947638091)", 4326);
			
		connectionsStartNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode.add(objectFactory.newWaySegmentConnection(4, 4, 5, accessTypes));
		
		segment = objectFactory.newSegment(4l, geometry, (float)GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(geometry), "Hauptstraße",
				(short)50, (short)50, (short)49, (short)46, (short)1, (short)1, FuncRoadClass.OTHER_MAJOR_ROAD, 
				FormOfWay.PART_OF_MULTI_CARRIAGEWAY_WHICH_IS_NOT_A_MOTORWAY, "primary",
				4, 5, 0, 4, 1, accessTypes, 
				accessTypes, false, false, true, segTimestamp, connectionsStartNode, connectionsEndNode);
		segments.add(segment);
		
		// segment 5
//		LINESTRING(13.053729700403 47.806947638091, 13.054244684534 47.807119299468)
		geometry = (LineString) GeometryUtils.createGeometryFromWkt("LINESTRING(13.053729700403 47.806947638091, "
				+ "13.054244684534 47.807119299468)", 4326);
		
		connectionsStartNode = new ArrayList<IWaySegmentConnection>();
		connectionsEndNode = new ArrayList<IWaySegmentConnection>();
		connectionsStartNode.add(objectFactory.newWaySegmentConnection(4, 5, 4, accessTypes));
		
		segment = objectFactory.newSegment(5l, geometry, (float)GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(geometry), "Hauptstraße",
				(short)50, (short)50, (short)47, (short)45, (short)1, (short)1, FuncRoadClass.OTHER_MAJOR_ROAD, 
				FormOfWay.PART_OF_MULTI_CARRIAGEWAY_WHICH_IS_NOT_A_MOTORWAY, "primary",
				5, 4, 0, 6, 1, accessTypes, 
				null, false, false, true, segTimestamp,  connectionsStartNode, connectionsEndNode);
		segments.add(segment);
				
		return segments;
	}
	
	@Test
	public void testCalcDuration() {
		IWaySegment seg = new WaySegment();
		seg.setMaxSpeedTow((short)50);
		seg.setLength(30);
		int minDuration = seg.getMinDuration(true);
		System.out.println("minDuration = " + minDuration);

		seg.setMaxSpeedTow((short)29);
		seg.setLength(22);
		minDuration = seg.getMinDuration(true);
		System.out.println("minDuration = " + minDuration);
	}
	
}
