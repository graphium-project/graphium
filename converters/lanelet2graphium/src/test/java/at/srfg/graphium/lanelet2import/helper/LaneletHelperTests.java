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

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDWaySegment;

public class LaneletHelperTests {
	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	private WKTReader wktReader = new WKTReader();

	@Test
	public void testCalculateCenterline() throws ParseException {
		// left bound in and right bound against driving direction
		log.info("Segment 1: left bound in and right bound against driving direction (true/false)");
		IHDWaySegment seg1 = new HDWaySegment();
		seg1.setId(1L);
		seg1.setLeftBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42321254246 49.01109735218, 8.42330239646 49.01111582478)"));
		seg1.setRightBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42332586579 49.01106788397, 8.42330026263 49.01105327604)"));
		
		// left and right bounds in driving direction
		log.info("Segment 2: left and right bounds in driving direction (true/true)");
		IHDWaySegment seg2 = new HDWaySegment();
		seg2.setId(2L);
		seg2.setLeftBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42321254246 49.01109735218, 8.42330239646 49.01111582478)"));
		seg2.setRightBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42330026263 49.01105327604, 8.42332586579 49.01106788397)"));
		
		// left and right bounds against driving direction
		log.info("Segment 3: left and right bounds against driving direction (false/false)");
		IHDWaySegment seg3 = new HDWaySegment();
		seg3.setId(3L);
		seg3.setLeftBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42330239646 49.01111582478, 8.42321254246 49.01109735218)"));
		seg3.setRightBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42332586579 49.01106788397, 8.42330026263 49.01105327604)"));
		
		// left bound against and right bound in driving direction
		log.info("Segment 4: left bound against and right bound in driving direction (false/true)");
		IHDWaySegment seg4 = new HDWaySegment();
		seg4.setId(4L);
		seg4.setLeftBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42330239646 49.01111582478, 8.42321254246 49.01109735218)"));
		seg4.setRightBoarderGeometry((LineString) wktReader.read("LINESTRING (8.42330026263 49.01105327604, 8.42332586579 49.01106788397)"));
		
		List<IHDWaySegment> segments = new ArrayList<>();
		segments.add(seg1);
		segments.add(seg2);
		segments.add(seg3);
		segments.add(seg4);
		
		processHDSegment(segments);
		
	}
	
	private void processHDSegment(List<IHDWaySegment> hdSegments) {
		for (IHDWaySegment hdSegment : hdSegments) {
			boolean[] lineDirections = LaneletHelper.checkLineDirections(hdSegment);
			hdSegment.setGeometry(LaneletHelper.calculateCenterline(hdSegment, lineDirections));
			System.out.println(hdSegment.getId() + ";" + lineDirections[0] + ";" + lineDirections[1] + ";" + hdSegment.getGeometry().toText());
		}
	}

}