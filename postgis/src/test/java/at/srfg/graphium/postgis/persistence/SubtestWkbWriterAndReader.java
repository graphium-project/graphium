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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.postgis.persistence;

import java.util.List;

import at.srfg.graphium.ITestGraphiumPostgis;
import org.junit.Assert;
import org.postgis.jts.JtsBinaryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.model.IWaySegment;

/**
 * @author mwimmer
 */

public class TestWkbWriterAndReader implements ITestGraphiumPostgis {

	@Autowired
	private IWayGraphReadDao<IWaySegment> dao;

	@Value("${db.graphName}")
	String graphName;
	@Value("${db.version}")
	String version;
	@Value("#{'${db.segmentId1}'.split(',')}") //using spEL
	List<Long> segmentIds;
	
	private WKBWriter wkbWriter = new WKBWriter();
	private WKBReader wkbReader = new WKBReader();
	private JtsBinaryParser bp = new JtsBinaryParser();

	private static Logger log = LoggerFactory.getLogger(TestWkbWriterAndReader.class);

	public void testWkbWriterAndReader() {
		List<IWaySegment> segments = null;
		try {
			segments = dao.getSegmentsById(graphName, version, segmentIds, false);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		
		Assert.assertNotNull(segments);
		Assert.assertNotEquals(0, segments.size());
		
		LineString geom = segments.get(0).getGeometry();
		
		byte[] geomBin = wkbWriter.write(geom);
		LineString geomFromBinary = null;
		try {
			geomFromBinary = (LineString) wkbReader.read(geomBin);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		
		Assert.assertNotNull(geomFromBinary);
		
		Assert.assertEquals(geom.toText(), geomFromBinary.toText());
		
		geomFromBinary = (LineString) bp.parse(geomBin);
		
		Assert.assertNotNull(geomFromBinary);
		
		Assert.assertEquals(geom.toText(), geomFromBinary.toText());
	}

	public void testWkbWriterAndReader2() {
		//TODO define correct linestring????
		String linestringWkt = "LINESTRING (13.045996812 47.810134412, 13.0460128 47.810128, 13.046028 47.8101184, 13.0460408 47.810112, "
				+ "13.0460504 47.810096, 13.046056 47.8100864, 13.0460576 47.8100736, 13.046056 47.8100576, 13.0460504 47.810048, "
				+ "13.046044 47.8100384)";
		WKTReader wktReader = new WKTReader();
		
		LineString geom = null;
		try {
			geom = (LineString) wktReader.read(linestringWkt);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		
		Assert.assertNotNull(geom);
		
		Assert.assertEquals(linestringWkt, geom.toText());
		
		byte[] geomBin = wkbWriter.write(geom);
		LineString geomFromBinary = null;
		try {
			geomFromBinary = (LineString) wkbReader.read(geomBin);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		
		Assert.assertNotNull(geomFromBinary);
		
		Assert.assertEquals(geom.toText(), geomFromBinary.toText());

		geomFromBinary = (LineString) bp.parse(geomBin);
		
		Assert.assertNotNull(geomFromBinary);
		
		Assert.assertEquals(geom.toText(), geomFromBinary.toText());
	}

	@Override
	public void run() {
		testWkbWriterAndReader();
		testWkbWriterAndReader2();
	}
}
