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
package at.srfg.graphium.postgis.persistence.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Point;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.postgis.persistence.resultsetextractors.ISegmentResultSetExtractorFactory;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-postgis_test.xml",
		"classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis-datasource.xml",
		"classpath:application-context-graphium-postgis-aliasing.xml"})
public class TestWayGraphReadDaoImpl {
	
	private static Logger log = LoggerFactory.getLogger(TestWayGraphReadDaoImpl.class);

	@Autowired
	private IWayGraphReadDao<IWaySegment> dao;

	@Resource(name="jacksonSegmentOutputFormatFactory")
	private ISegmentOutputFormatFactory segmentOutputFormatFactory;

	@Autowired
	private ISegmentResultSetExtractorFactory resultSetExtractorFactory;

	@Test
	@Transactional(readOnly=false)
	@Rollback(value=true)
	public void testReadSegments() {
		//gip_at_frc_0_15_10_151217
//		String viewName = "test_view_gip";
//		IWayGraph graph = metadataDao.getGraph(1);
//		String filter = "SELECT wayseg.* FROM graphs.waysegments_gip_at_frc_0_15_10_151217 wayseg";
//		IWayGraphView view = new WayGraphView(viewName, graph, filter, null, 100, 139, null);
//		viewDao.saveView(view);
		
		String viewName = "gip_at_frc_0_4";
		
		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
		try {
			graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);

			dao.streamSegments(graphOutputFormat, null, viewName, new Date());
		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
			log.error("",e);
		}

//		
//		
//		ISource source = new Source(1, "neue Source");
//		
//		dao.save(source);
//		
//		ISource savedSource = dao.getSource(1);
//		
//		Assert.notNull(savedSource);
//
//		System.out.println(source);
		
	}
		
	@Test
	@Transactional(readOnly=false)
	@Rollback(value=true)
	public void testReadSegmentsWithIdFilter() throws GraphNotExistsException, WaySegmentSerializationException {
		//gip_at_frc_0_15_10_151217
//		String viewName = "test_view_gip";
//		IWayGraph graph = metadataDao.getGraph(1);
//		String filter = "SELECT wayseg.* FROM graphs.waysegments_gip_at_frc_0_15_10_151217 wayseg";
//		IWayGraphView view = new WayGraphView(viewName, graph, filter, null, 100, 139, null);
//		viewDao.saveView(view);
		
		String viewName = "gip_at_frc_0_4";
		String version = "17_02_170627";
		Set<Long> ids = new HashSet<>();
		ids.add(901551169L);
		ids.add(901551109L);

		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
//		try {
			graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);
//			dao.streamStreetSegments(graphOutputFormat, null, "gip_at_frc_0", "15_10_151217", false, null);
			
			dao.streamSegments(graphOutputFormat, viewName, version, ids);
//		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
//			log.error("",e);
//		}

//		
//		
//		ISource source = new Source(1, "neue Source");
//		
//		dao.save(source);
//		
//		ISource savedSource = dao.getSource(1);
//		
//		Assert.notNull(savedSource);
//
//		System.out.println(source);
		
	}
		
	@Test
	@Transactional(readOnly=true)
	public void testGetSegmentById() {
		String viewName = "gip_at_frc_0_4";
		String version = "16_02_160229";
		long segmentId = 901551206;
		IWaySegment segment = null;
		try {
			segment = dao.getSegmentById(viewName, version, segmentId, true);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		Assert.assertNotNull(segment);
		Assert.assertEquals(segmentId, segment.getId());
	}
	
	@Test
	@Transactional(readOnly=true)
	public void testGetSegmentsById() {
		String viewName = "gip_at_frc_0_4";
		String version = "16_02_160229";
		long segmentId1 = 901551206;
		long segmentId2 = 901551190;
		List<Long> segmentIds = new ArrayList<>();
		segmentIds.add(segmentId1);
		segmentIds.add(segmentId2);
		List<IWaySegment> segments = null;
		try {
			segments = dao.getSegmentsById(viewName, version, segmentIds, true);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		Assert.assertNotNull(segments);
		Assert.assertEquals(2, segments.size());
	}
	
	@Test
	@Transactional(readOnly=true)
	public void testFindNearestSegments() {
		String viewName = "gip_at_frc_0_4";
		String version = "16_02_160229";
		Point referencePoint = GeometryUtils.createPoint2D(13.04378, 47.80464, 4326);
		float radiusInKm = 0.2f;
		int maxNrOfSegments = 10;
		List<IWaySegment> segments = null;
		try {
			segments = dao.findNearestSegments(viewName, version, referencePoint, radiusInKm, maxNrOfSegments);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		Assert.assertNotNull(segments);
		Long[] expectedSegmentIds = new Long[] {901418172L, 901418173L, 901413319L, 901411604L, 901413318L, 901456931L, 901411391L, 901456932L, 901410749L, 901415156L};
		int i = 0;
		for (IWaySegment segment : segments) {
			log.info("" + segment.getId());
			Assert.assertEquals(expectedSegmentIds[i++], (Long)segment.getId());
		}
	}
	
	@Test
	public void testParseTableAliases() {
		Set<String> tableAliases = null;
		
		tableAliases = parseTableAliases(
				"SELECT wayseg.* FROM graphs.waysegments AS wayseg"
				);
		log.info("====================================");
		log.info(StringUtils.join(tableAliases, ", "));
		log.info("====================================");
		Assert.assertEquals(tableAliases.size(), 1);
		Assert.assertEquals(tableAliases.iterator().next(), "wayseg");
		
		tableAliases = parseTableAliases(
				"SELECT wayseg.* FROM graphs.waysegments AS wayseg, graphs.xinfo AS xi WHERE xi.segment_id = wayseg.id"
				);
		log.info("====================================");
		log.info(StringUtils.join(tableAliases, ", "));
		log.info("====================================");
//		Assert.assertEquals(tableAliases.size(), 2);
//		Assert.assertEquals(tableAliases.iterator().next(), "wayseg");
//		Assert.assertEquals(tableAliases.iterator().next(), "xi");
		
		tableAliases = parseTableAliases(
				"SELECT wayseg.*, xi.* FROM graphs.waysegments AS wayseg LEFT OUTER JOIN graphs.xinfo AS xi ON xi.segment_id = wayseg.id"
				);
		log.info("====================================");
		log.info(StringUtils.join(tableAliases, ", "));
		log.info("====================================");

	}
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(value=true)
	public void testReadSegmentsWithXInfoTest() {
		
		String viewName = "gip_at_frc_0_4_xinfo_test";
		
		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
		graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);
		
		try {
			dao.streamSegments(graphOutputFormat, null, viewName, new Date());
		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
			log.error("",e);
		}
	
	}

	@Test
	public void testResultSetExtractorPrototypeInstantiation() {
		Set<String> tableAliases = new HashSet<>();
		tableAliases.add("wayseg");
		tableAliases.add("xit");
		ResultSetExtractor<? extends IBaseSegment> rsExtractor1 = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		ResultSetExtractor<? extends IBaseSegment> rsExtractor2 = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		try {
			rsExtractor1.extractData(null);
			rsExtractor2.extractData(null);
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotEquals(rsExtractor1, rsExtractor2);
	}
	
	private Set<String> parseTableAliases(String filter) {
		Set<String> tableAliases = new HashSet<>();
		List<String> keyWords = new ArrayList<>(Arrays.asList("INNER", "OUTER", "LEFT", "RIGHT", "FULL", "JOIN", "AS", "ON", "="));
		
		String[] tokens = filter.split("[ ,]");
		boolean fromClause = false;
		boolean finished = false;
		
		int i = 0;
		while (!finished && i < tokens.length) {
			if (tokens[i].toUpperCase().contains("FROM")) {
				fromClause = true;
			} else if (tokens[i].toUpperCase().contains("WHERE")) {
				fromClause = false;
				finished = true;
			} else {
				if (fromClause) {
					if (!keyWords.contains(tokens[i].toUpperCase()) &&
						((i+1) == tokens.length || !tokens[i+1].toUpperCase().equals("AS")) &&
						tokens[i].length() > 0 &&
						!tokens[i].contains(".")) {
						tableAliases.add(tokens[i]);
					}
				}
			}
			i++;
		}
		
		return tableAliases;
	}

}
