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

import at.srfg.graphium.ITestGraphiumPostgis;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
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

public class SubtestWayGraphReadDaoImpl implements ITestGraphiumPostgis{
	
	private static Logger log = LoggerFactory.getLogger(SubtestWayGraphReadDaoImpl.class);

	@Autowired
	private IWayGraphReadDao<IWaySegment> dao;

	@Resource(name="jacksonSegmentOutputFormatFactory")
	private ISegmentOutputFormatFactory segmentOutputFormatFactory;

	@Autowired
	private ISegmentResultSetExtractorFactory resultSetExtractorFactory;

	@Value("${db.viewName}")
	String viewName;
	@Value("${db.version}")
	String version;
	@Value("#{'${db.ids}'.split(',')}") //using spEL
	Set<Long> ids;
	@Value("${db.segmentId1}")
	long segmentId1;
	@Value("${db.segmentId2}")
	long segmentId2;

	@Value("${db.point.xKoord}")
	float xKoord;
	@Value("${db.point.yKoord}")
	float yKoord;
	@Value("${db.point.SRID}")
	int SRID;
	@Value("${db.radius}")
	float radiusInKm;
	@Value("${db.maxNrOfSegments}")
	int maxNrOfSegments;

	@Transactional(readOnly=false)
	public void testReadSegments() {
		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
		try {
			graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);

			dao.streamSegments(graphOutputFormat, null, viewName, new Date());
		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
			log.error("",e);
		}
	}

	@Transactional(readOnly=false)
	public void testReadSegmentsWithIdFilter() {
		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
		try {
			graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);
			dao.streamSegments(graphOutputFormat, viewName, version, ids);
		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
			log.error("",e);
		}
	}

	@Transactional(readOnly=true)
	public void testGetSegmentById() {
		IWaySegment segment = null;
		try {
			segment = dao.getSegmentById(viewName, version, segmentId1, true);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		Assert.assertNotNull(segment);
		Assert.assertEquals(segmentId1, segment.getId());
	}

	@Transactional(readOnly=true)
	public void testGetSegmentsById() {
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

	@Transactional(readOnly=true)
	public void testFindNearestSegments() {
		Point referencePoint = GeometryUtils.createPoint2D(xKoord, yKoord, SRID);
		List<IWaySegment> segments = null;
		try {
			segments = dao.findNearestSegments(viewName, version, referencePoint, radiusInKm, maxNrOfSegments);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		Assert.assertNotNull(segments);
		Long[] expectedSegmentIds = new Long[] {9223372036854775807L};
		int i = 0;
		for (IWaySegment segment : segments) {
			log.info("" + segment.getId());
			Assert.assertEquals(expectedSegmentIds[i++], (Long)segment.getId());
		}
	}

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
		
		tableAliases = parseTableAliases(
				"SELECT wayseg.*, xi.* FROM graphs.waysegments AS wayseg LEFT OUTER JOIN graphs.xinfo AS xi ON xi.segment_id = wayseg.id"
				);
		log.info("====================================");
		log.info(StringUtils.join(tableAliases, ", "));
		log.info("====================================");

	}

	@Transactional(readOnly=false)
	public void testReadSegmentsWithXInfoTest() {
		OutputStream os = new ByteArrayOutputStream();
		ISegmentOutputFormat<IWaySegment> graphOutputFormat;
		graphOutputFormat = (ISegmentOutputFormat<IWaySegment>) segmentOutputFormatFactory.getSegmentOutputFormat(os);
		
		try {
			dao.streamSegments(graphOutputFormat, null, viewName, new Date());
		} catch (GraphNotExistsException | WaySegmentSerializationException e) {
			log.error("",e);
		}
	
	}

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
			e.printStackTrace();
		}
		Assert.assertEquals(rsExtractor1, rsExtractor2);
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

	@Override
	public void run() {
		testReadSegments();
		testReadSegmentsWithIdFilter();
		testGetSegmentById();
		testGetSegmentsById();
		testFindNearestSegments();
		testParseTableAliases();
		testReadSegmentsWithXInfoTest();
		testResultSetExtractorPrototypeInstantiation();
	}
}
