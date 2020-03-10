/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.persistence.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.core.service.impl.GraphReadOrder;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */
public class MockReadDaoImpl implements IWayGraphReadDao<IWaySegment> {

	@Override
	public IWaySegment getSegmentById(String graphName, String version, long segmentId, boolean includeConnections)
			throws GraphNotExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IWaySegment> getSegmentsById(String graphName, String version, List<Long> segmentIds, boolean includeConnections)
			throws GraphNotExistsException {
		List<IWaySegment> segments = new ArrayList<>(segmentIds.size());
		
		for (Long id : segmentIds) {
			IWaySegment segment = new WaySegment();
			segment.setId(id);
			segment.setLength(10);
			segment.setMaxSpeedTow((short)50);
			segment.setMaxSpeedBkw((short)50);
			Coordinate[] coords = new Coordinate[2];
			if (id == 1) {
				coords[0] = new Coordinate(46.99, 11.00);
				coords[1] = new Coordinate(47.01, 11.00);
			} else if (id == 2) {
				coords[0] = new Coordinate(47.01, 11.00);
				coords[1] = new Coordinate(47.03, 11.00);
			} else if (id == 3) {
				coords[0] = new Coordinate(47.03, 11.00);
				coords[1] = new Coordinate(47.05, 11.00);
			}
			segment.setGeometry(GeometryUtils.createLineString(coords, 4326));
			
			segments.add(segment);
		}
		
		return segments;
	}

	@Override
	public void readStreetSegments(BlockingQueue<IWaySegment> queue, String graphName, String version)
			throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readStreetSegments(BlockingQueue<IWaySegment> queue, String graphName, String version, GraphReadOrder order)
			throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamSegments(ISegmentOutputFormat<IWaySegment> outputFormat, Polygon bounds, String viewName, Date timestamp)
			throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamSegments(ISegmentOutputFormat<IWaySegment> outputFormat, Polygon bounds, String viewName, String version)
			throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamSegments(ISegmentOutputFormat<IWaySegment> outputFormat, Polygon bounds, String viewName, String version,
			GraphReadOrder order) throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamSegments(ISegmentOutputFormat<IWaySegment> outputFormat, String viewName, String version, Set<Long> ids)
			throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getGraphVersion(IWayGraphView view, Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IWaySegment> findNearestSegments(String graphName, String version, Point referencePoint, double radiusInKm,
			int maxNrOfSegments) throws GraphNotExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IWaySegment> getStreetSegments(String viewName, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void streamIncomingConnectedStreetSegments(ISegmentOutputFormat<IWaySegment> outputFormat, String graphName,
			String version, Set<Long> ids) throws WaySegmentSerializationException, GraphNotExistsException {
		// TODO Auto-generated method stub
		
	}

}
