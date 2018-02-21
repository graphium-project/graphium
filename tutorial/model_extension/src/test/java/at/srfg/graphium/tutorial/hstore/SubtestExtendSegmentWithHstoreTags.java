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
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.tutorial.hstore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import at.srfg.graphium.tutorial.ITestGraphiumModelExtension;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */

/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-tutorial_test.xml",
		"classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis-datasource.xml",
		"classpath:application-context-graphium-postgis-aliasing.xml"})
*/
public class SubtestExtendSegmentWithHstoreTags implements ITestGraphiumModelExtension{

	private static Logger log = LoggerFactory.getLogger(SubtestExtendSegmentWithHstoreTags.class);

	@Autowired
	private IWayGraphWriteDao<IWaySegment> writeDao;

	@Autowired
	private IWayGraphReadDao<IWaySegment> readDao;
	
	@Autowired
	private IWayGraphVersionMetadataDao metadataDao;

	@Autowired
	private ISourceDao sourceDao;

	@Qualifier("postgisWayGraphViewDao")
	@Autowired
	private IWayGraphViewDao viewDao;

	@Value("${db.graphName}")
	private String graphName;
	@Value("${db.graphVersion}")
	private String version;

	//@Test
	@Transactional(readOnly=false)
	//@Rollback(true)
	public void testExtendSegmentWithHstoreTags() throws GraphAlreadyExistException, GraphNotExistsException, InterruptedException, WaySegmentSerializationException {
		// store base data
		createBaseData(graphName, version);
		
		// create dummy segments
		List<IWaySegment> segments = new ArrayList<IWaySegment>();
		Set<Access> accesses = new HashSet<Access>();
		accesses.add(Access.PRIVATE_CAR);
		
		IWaySegment segment1 = new WaySegment();
		segment1.setId(1);
		segment1.setAccessBkw(accesses);
		segment1.setAccessTow(accesses);
		segment1.setFormOfWay(FormOfWay.PART_OF_MOTORWAY);
		segment1.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
		segment1.setMaxSpeedBkw((short) 130);
		segment1.setMaxSpeedTow((short) 130);
		segment1.setSpeedCalcBkw((short) 130);
		segment1.setSpeedCalcTow((short) 130);
		segment1.setBridge(false);
		segment1.setUrban(false);
		segment1.setTunnel(false);
		segment1.setGeometry(createDummyGeometry());
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("like", "true");
		segment1.setTags(tags);
		
		IWaySegment segment2 = new WaySegment();
		segment2.setId(2);
		segment2.setAccessBkw(accesses);
		segment2.setAccessTow(accesses);
		segment2.setFormOfWay(FormOfWay.PART_OF_ROUNDABOUT);
		segment2.setFrc(FuncRoadClass.LOCAL_ROAD);
		segment2.setSpeedCalcBkw((short) 30);
		segment2.setSpeedCalcTow((short) 30);
		segment2.setBridge(false);
		segment2.setUrban(false);
		segment2.setTunnel(false);
		segment2.setGeometry(createDummyGeometry());
		tags = new HashMap<String, String>();
		tags.put("like", "false");
		segment2.setTags(tags);
		
		segments.add(segment1);
		segments.add(segment2);
		
		// store segments
		try {
			writeDao.saveSegments(segments, graphName, version);
		} catch (GraphStorageException e) {
			log.error(e.getMessage(), e);
		} catch (GraphNotExistsException e) {
			log.error(e.getMessage(), e);
		}
		
		// read segments
		BlockingQueue<IWaySegment> segmentsQueue = new ArrayBlockingQueue<IWaySegment>(10);
		readDao.readStreetSegments(segmentsQueue, graphName, version);
		log.info("Stored segments:");
		while (!segmentsQueue.isEmpty()) {
			IWaySegment seg = segmentsQueue.poll(10, TimeUnit.MILLISECONDS);
			log.info(seg.toString());
		}
	
	}
	
	private LineString createDummyGeometry() {
		WKTReader wktReader = new WKTReader();
		LineString ls = null;
		try {
			ls = (LineString) wktReader.read("LINESTRING(14.0 47.0, 14.1 47.1)");
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		return ls;
	}

	private void createBaseData(String graphName, String version) throws GraphAlreadyExistException, GraphNotExistsException {
		
		ISource source = new Source(1, "testSource");
		sourceDao.save(source);
		
		Date now = new Date();
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("creator", "me");
		
		IWayGraph wayGraph = null;
		
		if (metadataDao.checkIfGraphExists(graphName)) {
			log.info("Waygraph '" + graphName + "' already exists");
			wayGraph = metadataDao.getGraph(graphName);
		} else {
			metadataDao.saveGraph(graphName);
			log.info("Waygraph '" + graphName + "' saved");
			wayGraph = metadataDao.getGraph(graphName);
			viewDao.saveDefaultView(wayGraph);
			log.info("Waygraph '" + graphName + "' saved");
		}
		
		IWayGraphVersionMetadata metadata;
		Set<Access> accessTypes = new HashSet<Access>();
		accessTypes.add(Access.PRIVATE_CAR);
		metadata = metadataDao.newWayGraphVersionMetadata(0, wayGraph.getId(), graphName, version, 
				graphName, "1.0_orig", State.INITIAL, now, null, getBoundsAustria(), 1000, 2000, accessTypes, tags, source, 
				"tutorial's graph ", "no description", now, now, "me", "https://www.salzburgresearch.at/");
		metadataDao.saveGraphVersion(metadata);
		log.info("\nWayGraphVersionMetadata saved");

		writeDao.createGraphVersion(graphName, version, true, false);

	}

	private Polygon getBoundsAustria() {
		WKTReader reader = new WKTReader();
		String poly = "POLYGON((9.5282778 46.3704647,9.5282778 49.023522,17.1625438 49.023522,17.1625438 46.3704647,9.5282778 46.3704647))";
		Polygon bounds = null;
		try {
			bounds = (Polygon) reader.read(poly);
			bounds.setSRID(4326);
		} catch (ParseException e) {
		}
		return bounds;
	}

	@Override
	public void run() {
		try {
			testExtendSegmentWithHstoreTags();
		} catch (GraphAlreadyExistException e) {
			e.printStackTrace();
		} catch (GraphNotExistsException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (WaySegmentSerializationException e) {
			e.printStackTrace();
		}
	}
}
