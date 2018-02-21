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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.srfg.graphium.ITestGraphiumPostgis;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.postgis.model.impl.XInfoTest;

/**
 * @author mwimmer
 *
 */

public class TestWayGraphWriteDaoImpl implements ITestGraphiumPostgis{

	@Autowired
	private IWayGraphWriteDao<IWaySegment> dao;

    @Value("${db.graphName}")
    String graphName;
    @Value("${db.version}")
    String version;
    @Value("${db.SRID}")
    int SRID;

	private static Logger log = LoggerFactory.getLogger(TestWayGraphWriteDaoImpl.class);

	@Transactional(readOnly=false)
	public void testReadSegments() throws GraphNotExistsException {
		WaySegment segment = new WaySegment();
		Coordinate c1 = new Coordinate(13.1, 47.1);
		Coordinate c2 = new Coordinate(13.2, 47.2);
		LineString geom = GeometryUtils.createLineString(new Coordinate[] {c1, c2}, SRID);
		segment.setId(Long.MAX_VALUE);
		segment.setGeometry(geom);
		segment.setMaxSpeedBkw((short)50);
		segment.setMaxSpeedTow((short)50);
		segment.setSpeedCalcBkw((short)50);
		segment.setSpeedCalcTow((short)50);
		segment.setLanesBkw((short)1);
		segment.setLanesTow((short)1);
		segment.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
		segment.setWayId(segment.getId());
		segment.setStartNodeId(1);
		segment.setStartNodeIndex(1);
		segment.setEndNodeId(2);
		segment.setEndNodeIndex(2);
		segment.setTimestamp(new Date());
		
		XInfoTest xInfo = new XInfoTest();
		xInfo.setDirectedId(1);
		xInfo.setDirectionTow(true);
		xInfo.setGraphId(1);
		xInfo.setSegmentId(segment.getId());
		segment.addXInfo(xInfo);
		
		List<IWaySegment> segments = new ArrayList<>();
		segments.add(segment);
		
		try {
			dao.saveSegments(segments, graphName, version);
		} catch (GraphStorageException e) {
			log.error("error during storing segments",e);
		}
	}

	@Override
	public void run() {
		try {
			testReadSegments();
		} catch (GraphNotExistsException e) {
			e.printStackTrace();
		}
	}
}
