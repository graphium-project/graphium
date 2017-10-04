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
package at.srfg.geomutils;


import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.geomutils.GeometryUtils;

/**
 * @author mwimmer
 *
 */
public class TestGeometryUtils {

	@Test
	public void testDistanceOnLineString() {
		
		double lon1 = 13.0387869603598;
		double lat1 = 47.8201969276863;
		
		double lon2 = 13.05214066418;
		double lat2 = 47.8202307410127;
		
		double lon3 = 13.0387355892959;
		double lat3 = 47.8291906273261;
		
		// horizontal distance in sbg - itzling
		// Diese 3 Punkte bilden ein L, wobei top der Punkt über dem startpunkt ist!!!
		Point start = GeometryUtils.createPoint(lon1, lat1, 0, GeometryUtils.WGS84);
		Point end = GeometryUtils.createPoint(lon2, lat2, 0, GeometryUtils.WGS84);
		Point top = GeometryUtils.createPoint(lon3, lat3, 0, GeometryUtils.WGS84);
		
		Coordinate[] coords = new Coordinate[3];
		coords[0] = top.getCoordinate();
		coords[1] = start.getCoordinate();
		coords[2] = end.getCoordinate();
		LineString lineString = GeometryUtils.createLineString(coords, GeometryUtils.WGS84);
		
		double distance = GeometryUtils.distanceOnLineStringInMeter(top, lineString);
		Assert.assertEquals(0, distance, 0.1);
//		System.out.println("distance from top = " + distance);
		
		distance = GeometryUtils.distanceOnLineStringInMeter(start, lineString);
		Assert.assertEquals(1000, distance, 0.1);
//		System.out.println("distance from start = " + distance);
		
		distance = GeometryUtils.distanceOnLineStringInMeter(end, lineString);
		Assert.assertEquals(2000, distance, 0.1);
//		System.out.println("distance from end = " + distance);
		
		
		
		// Tests für NaN Problem Juni 2016
		Point outLier =  GeometryUtils.createPoint(lon2 + 0.1, lat2, 0, GeometryUtils.WGS84);
		distance = GeometryUtils.distanceOnLineStringInMeter(outLier, lineString);
		System.out.println("outlier distance " + distance);
		
		
		Random r = new Random();
		
		for (int i = 0; i < 1000; i++) {
			
			double rand = r.nextDouble() - 0.5;
			
			double rand1  = r.nextDouble() - 0.5;
			
			Point outLier1 =  GeometryUtils.createPoint(lon2 + rand, lat2 + rand1, 0, GeometryUtils.WGS84);
			distance = GeometryUtils.distanceOnLineStringInMeter(outLier, lineString);
			
			Assert.assertTrue("Point " + outLier1 + " produces NaN", !Double.isNaN(distance));
			//System.out.println("outlier distance " + distance);
				
		}

		// Beispiel mit NaN: Man beachte: zwei identische Punkte im Linestring
		// JIRA Ticket ITSAW-455 
		//LINESTRING(13.1194984 47.996928, 13.1194984 47.996928, 13.1189824 47.9966496, 13.118888 47.9966016)
		//-- point as wkt
		// POINT(13.11965 47.99684)
		
		WKTReader reader = new WKTReader();
		try {
			LineString ls = (LineString) reader.read("LINESTRING(13.1194984 47.996928, 13.1194984 47.996928, 13.1189824 47.9966496, 13.118888 47.9966016)");			
			Point point = (Point) reader.read("POINT(13.11965 47.99684)");
			distance = GeometryUtils.distanceOnLineStringInMeter(point, ls);
			Assert.assertTrue("Point " + point + " produces NaN", !Double.isNaN(distance));
		} catch (ParseException e) {
			System.err.println(e);
		}
		
		
		Point pointInBetween = GeometryUtils.createPoint(13.038748, 47.825431, 0, GeometryUtils.WGS84);
		distance = GeometryUtils.distanceOnLineStringInMeter(pointInBetween, lineString);
//		Assert.assertEquals(2000, distance, 5);
		System.out.println("distance from point in between = " + distance);
		
		Point ppointInBetween = GeometryUtils.projectPointOnLineString(pointInBetween, lineString);
		System.out.println("point in between = " + ppointInBetween.toString());
		distance = GeometryUtils.distanceOnLineStringInMeter(end, lineString);
//		assertEquals(2000, distance, 5);
		System.out.println("distance from point in between = " + distance);

		double distand = GeometryUtils.distanceAndoyer(start,end);
		System.out.println("Andoyer Distance = " + distand);
	}

	@Test
	public void testDistanceToLineString() {
		LineString lsNear;
		LineString lsFar;
		WKTReader reader = new WKTReader();
		try {
			lsNear = (LineString) reader.read("LINESTRING(12.8417123 48.1556367,12.841558 48.1557429,12.8413746 48.1558519)");	
			lsNear.setSRID(GeometryUtils.WGS84);
			lsFar = (LineString) reader.read("LINESTRING(12.841411800000001 48.1557631, 12.8413746 48.1558519)");
			lsFar.setSRID(GeometryUtils.WGS84);
			
			Point point = GeometryUtils.createPoint(12.8412583, 48.1560266, 0, GeometryUtils.WGS84);
			Point pointOnLsNear = GeometryUtils.projectPointOnLineString(point, lsNear);
			double distance = GeometryUtils.distanceAndoyer(point, pointOnLsNear);
			System.out.println("projected point on near linestring = " + pointOnLsNear.toText());
			System.out.println("distance from point to near linestring = " + distance);
			
			Point pointOnLsFar = GeometryUtils.projectPointOnLineString(point, lsFar);
			distance = GeometryUtils.distanceAndoyer(point, pointOnLsFar);
			System.out.println("projected point on far linestring = " + pointOnLsFar.toText());
			System.out.println("distance from point to far linestring = " + distance);
		} catch (ParseException e) {
			System.err.println(e);
		}

	}

	@Test
	public void testDistanceAndoyer() {
		double lon1 = 13.0387869603598;
		double lat1 = 47.8201969276863;

		double lon2 = 13.05214066418;
		double lat2 = 47.8202307410127;

		// horizontal distance in sbg - itzling
		Point start = GeometryUtils.createPoint(lon1, lat1, 0, GeometryUtils.WGS84);
		Point end = GeometryUtils.createPoint(lon2 , lat2, 0, GeometryUtils.WGS84);

		double distand = GeometryUtils.distanceAndoyer(start,end);
		System.out.println("Andoyer Distance = " + distand);
	}
	
}
