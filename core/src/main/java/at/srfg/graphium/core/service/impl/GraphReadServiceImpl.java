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
package at.srfg.graphium.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonSegmentOutputFormat;
import at.srfg.graphium.model.*;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.core.service.IGraphReadService;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;

public class GraphReadServiceImpl<T extends IBaseWaySegment> implements IGraphReadService<T> {
		
	private IWayGraphReadDao<T> readDao;
	
	@Override
	public T getSegmentById(String graphName, String version, long id,
			boolean includeConnections, boolean includeGeometries) throws GraphNotExistsException {
		return readDao.getSegmentById(graphName, version, id, includeConnections);
	}

	@Override
	public List<T> getSegmentsById(String graphName, String version, List<Long> ids,
			boolean includeConnections, boolean includeGeometries) throws GraphNotExistsException {
		return readDao.getSegmentsById(graphName, version, ids, includeConnections);
	}

	@Override
	public void streamStreetSegments(
			ISegmentOutputFormat<T> outputFormat,
			Polygon bounds, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException {
		// TODO: TIMESTAMP ist derzeit null => andere Methode benützen bzw. andere Methodensignatur deklarieren?
		readDao.streamSegments(outputFormat, bounds, graphName, version); //, includeConnectionTable, accessTypes);
	}

	@Override
	public void streamStreetSegments(
			ISegmentOutputFormat<T> outputFormat,
			Polygon bounds, String graphName, Date timestamp) throws GraphNotExistsException, WaySegmentSerializationException {
		
		readDao.streamSegments(outputFormat, bounds, graphName, timestamp);
	}

	@Override
	public void streamStreetSegments(ISegmentOutputFormat<T> outputFormat, Set<Long> ids, String graphName,
			String version) throws GraphNotExistsException, WaySegmentSerializationException {
		readDao.streamSegments(outputFormat, graphName, version, ids);
	}

	@Override
	public void readStreetSegments(BlockingQueue<T> queue, String graphName, String version) 
			throws GraphNotExistsException, WaySegmentSerializationException {
		readDao.readStreetSegments(queue, graphName, version);
	}
	
	@Override
	public void readStreetSegments(BlockingQueue<T> queue, String graphName, String version, GraphReadOrder order) 
			throws GraphNotExistsException, WaySegmentSerializationException {
		readDao.readStreetSegments(queue, graphName, version, order);
	}

	public IWayGraphReadDao<T> getReadDao() {
		return readDao;
	}

	public void setReadDao(IWayGraphReadDao<T> readDao) {
		this.readDao = readDao;
	}

}