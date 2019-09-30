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
package at.srfg.graphium.core.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.impl.GraphReadOrder;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;

/**
 * Service for reading graph segments.
 * In consideration of requested filter attributes (e.g. timestamp) the valid graph version(s) will be selected (via metadata DAO).
 * Then the graph segments will be returned.
 * If there is a graph's view defined only valid segments will be returned.
 * 
 * @author mwimmer
 *
 */
public interface IGraphReadService<T extends IBaseSegment> {

	/**
	 * retrieve segment with the given id. if not found null will be returned.
	 * 
	 * @param graphName name of the graph the data has to be looked up
	 * @param version version of the graph
	 * @param ids ids to search for
	 * @param includeConnections should the result contain connections for each segment?
	 * @param includeGeometries should the result contain geometries for each segment?
	 * @return segment with given id or null if not found
	 * @throws GraphNotExistsException 
	 */
	T getSegmentById(String graphName, String version, long id,
			boolean includeConnections, boolean includeGeometries) throws GraphNotExistsException;
	
	/**
	 * get segments by ids.
	 * no order of the returned data is garantied. when no segment is found for a id in the id list the return list 
	 * will not contain any value for that id 
	 *
	 * @param graphName name of the graph the data has to be looked up
	 * @param version version of the graph
	 * @param ids ids to search for
	 * @param includeConnections should the result contain connections for each segment?
	 * @param includeGeometries should the result contain geometries for each segment?
	 * @return list of all found segments for the given ids
	 * @throws GraphNotExistsException 
	 */
	List<T> getSegmentsById(String graphName, String version, List<Long> ids,
			boolean includeConnections, boolean includeGeometries) throws GraphNotExistsException;

	/**
	 * central fetch method for graphs in a given geometry (e.g. Bounding box). 
	 * 
	 * can be used to retrieve data as objects stored in the @see IWaySegmentContainer or to stream the 
	 * data directly to an wrapped stream
	 * 
	 * @param outputFormat outputFormat for streaming
	 * @param bounds polygon used for geospatial restriction in query
	 * @param graphName name of the graph the data has to be loaded from
	 * @param version version of the graph
	 * @return stream of IWaySegments
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamStreetSegments(
			ISegmentOutputFormat<T> outputFormat,
			Polygon bounds, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException;
		
	/**
	 * fetch method for segments in a graph
	 * 
	 * @param outputFormat outputFormat for streaming
	 * @param ids IDs of way segments
	 * @param graphName name of the graph the data has to be loaded from
	 * @param version version of the graph
	 * @return stream of IWaySegments
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamStreetSegments(
			ISegmentOutputFormat<T> outputFormat,
			Set<Long> ids, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * Fully read the graph with the given name and version into a queue. used for producer threads which have to iterate a graph
	 * for some batchjob. geometries will be included but no connection tables!
	 * 
	 * @param queue queue to pass the data in
	 * @param graphName name of the graph to read from
	 * @param version version of the graph
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void readStreetSegments(BlockingQueue<T> queue, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * central fetch method for graphs in a given geometry (e.g. Bounding box). 
	 * 
	 * can be used to retrieve data as objects stored in the @see IWaySegmentContainer or to stream the 
	 * data directly to an wrapped stream
	 * 
	 * @param outputFormat outputFormat for streaming
	 * @param bounds polygon used for geospatial restriction in query
	 * @param graphName name of the graph the data has to be loaded from
	 * @param timestamp timestamp of the graph version
	 * @return stream of IWaySegments
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamStreetSegments(ISegmentOutputFormat<T> outputFormat, Polygon bounds, String graphName, Date timestamp) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * Fully read the graph with the given name and version into a queue. used for producer threads which have to iterate a graph
	 * for some batchjob. geometries will be included but no connection tables! Order can be specified using an GraphReadOrder object
	 * 
	 * @param queue queue to pass the data in
	 * @param graphName name of the graph to read from
	 * @param version version of the graph
	 * @param order order the graph segments will be read
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void readStreetSegments(BlockingQueue<T> queue, String graphName,
			String version, GraphReadOrder order) throws GraphNotExistsException, WaySegmentSerializationException;
	
	/**
	 * streams segments with incoming connected to segments with given ids
	 * 
	 * @param outputFormat format used for streaming
	 * @param graphName graph name
	 * @param version version of graph
	 * @param ids id set of segments where connected segments should be retrieved
	 * @throws WaySegmentSerializationException thrown on error during serialization
	 * @throws GraphNotExistsException thrown if graph name/version combination dosn´t exist
	 */
	void streamIncomingConnectedStreetSegments(ISegmentOutputFormat<T> outputFormat, String graphName, String version,
			Set<Long> ids)
			throws WaySegmentSerializationException, GraphNotExistsException;

	
}
