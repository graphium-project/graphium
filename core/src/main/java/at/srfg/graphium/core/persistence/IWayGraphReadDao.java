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
package at.srfg.graphium.core.persistence;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.impl.GraphReadOrder;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.view.IWayGraphView;

public interface IWayGraphReadDao<T extends IBaseSegment> {

	/**
	 * retrieve segment with the given id. if not found null will be returned.
	 * 
	 * @param graphName name of the graph version the data has to be loaded from
	 * @param version version of the graph
	 * @param segmentId id to search for
	 * @param includeConnections should the result contain connections for each segment?
	 * @return segment with given id or null if not found
	 * @throws GraphNotExistsException 
	 */
	T getSegmentById(String graphName, String version, long segmentId,
			boolean includeConnections) throws GraphNotExistsException;
	
	/**
	 * get segments by ids.
	 * no order of the returned data is garantied. when no segment is found for a id in the id list the return list 
	 * will not contain any value for that id 
	 *
	 * @param graphName name of the graph version the data has to be loaded from
	 * @param version version of the graph
	 * @param segmentIds ids to search for
	 * @param includeConnections should the result contain connections for each segment?
	 * @return list of all found segments for the given ids
	 * @throws GraphNotExistsException 
	 */
	List<T> getSegmentsById(String graphName, String version, List<Long> segmentIds,
			boolean includeConnections) throws GraphNotExistsException;
	
	/**
	 * Fully read the graph with the given name into a queue. used for producer threads which have to iterate a graph
	 * for some batchjob. geometries will be included but no connection tables!
	 * 
	 * @param queue queue to pass the data in
	 * @param graphName name of the graph to read
	 * @param graphVersionId id of the graph version to read from
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void readStreetSegments(BlockingQueue<T> queue, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * Fully read the graph with the given name into a queue. used for producer threads which have to iterate a graph
	 * for some batchjob. geometries will be included but no connection tables!
	 * 
	 * @param queue queue to pass the data in
	 * @param graphName name of the graph to read
	 * @param graphVersionId id of the graph version to read from
	 * @param order order the graph should be read (single attribute / asc ord desc)
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void readStreetSegments(BlockingQueue<T> queue, String graphName, String version, GraphReadOrder order) throws GraphNotExistsException, WaySegmentSerializationException;
	
	/**
	 * @param outputFormat
	 * @param bounds
	 * @param viewName
	 * @param timestamp Timestamp which identifies graph version; if null current graph version will be taken
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamSegments(ISegmentOutputFormat<T> outputFormat, Polygon bounds, String viewName, Date timestamp) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * @param outputFormat
	 * @param bounds
	 * @param viewName
	 * @param version
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamSegments(ISegmentOutputFormat<T> outputFormat, Polygon bounds, String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

	/**
	 * @param outputFormat
	 * @param bounds
	 * @param viewName
	 * @param version
	 * @param order
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamSegments(ISegmentOutputFormat<T> outputFormat, Polygon bounds,
			String viewName, String version, GraphReadOrder order) throws GraphNotExistsException, WaySegmentSerializationException;
	
	/**
	 * @param outputFormat
	 * @param viewName
	 * @param version
	 * @param ids
	 * @throws GraphNotExistsException 
	 * @throws WaySegmentSerializationException 
	 */
	void streamSegments(ISegmentOutputFormat<T> outputFormat, String viewName, String version, Set<Long> ids) throws GraphNotExistsException, WaySegmentSerializationException;
	
	/**
	 * @param view
	 * @param timestamp
	 * @return
	 */
	String getGraphVersion(IWayGraphView view, Date timestamp);

	/**
	 * @param graphName
	 * @param version
	 * @param referencePoint
	 * @param radiusInKm
	 * @param maxNrOfSegments
	 * @return
	 * @throws GraphNotExistsException 
	 */
	List<T> findNearestSegments(String graphName, String version, Point referencePoint, double radiusInKm,
			int maxNrOfSegments) throws GraphNotExistsException;

	/**
	 * 
	 * @param viewName
	 * @param version
	 * @return
	 */
	List<T> getStreetSegments(String viewName, String version);

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