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

import java.util.List;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegmentConnection;

public interface IWayGraphWriteDao<T extends IBaseWaySegment> extends IBaseSegmentWriteDao {

	/**
	 * creates an empty graph in storage (e.g. create database tables)
	 *
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @param overrideGraphIfExsists should the method override an existing graph with same name? 
	 * @throws GraphAlreadyExistException if graph exists and overrideGraphIfExsists is not true
	 * @throws GraphNotExistsException 
	 */
	void createGraph(String graphName, String version,
			boolean overrideGraphIfExsists) throws GraphAlreadyExistException, GraphNotExistsException;

	/**
	 * creates an empty graph in storage (e.g. create database tables) but allows to 
	 * skip creation of connection constraints if underlaying storage system schema supports constraints (e.g. RDBMS)
	 *
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @param overrideGraphIfExsists should the method override an existing graph with same name?
	 * @param createConnectionConstraint true if connection constraints should be created (then method semantic is the same then createGraph
	 * 		  without parameters), false if constraints should not be created
	 * 			
	 * @throws GraphAlreadyExistException if graph exists and overrideGraphIfExsists is not true
	 * @throws GraphNotExistsException 
	 */
	void createGraphVersion(String graphName, String version,
			boolean overrideGraphIfExsists, boolean createConnectionConstraint) throws GraphAlreadyExistException, GraphNotExistsException;

	void createConnectionContstraints(String graphVersionName) throws GraphNotExistsException;
	
	/**
	 * save segments to the graph with the given name
	 * 
	 * @param segments segments to save
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @throws GraphStorageException if problems during persisting graph occurred
	 */
	void saveSegments(List<T> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;
	
	/**
 	 * update all attributes (not connections or geometries) of the segments in the graph with the given name
	 * 
	 * @param segments segments to save
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @throws GraphStorageException if problems during persisting graph occurred
	 */
	long updateSegmentAttributes(List<T> segments,
			String graphName, String version) throws GraphStorageException, GraphNotExistsException;;

	/**
	 * save connections to the graph. all connections on segments passed in will be saved. the segments themselve will 
	 * only be saved if param saveSegments is true. 
	 * 
	 * @param segmentsWithConnections
	 * @param saveSegments 
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @return count of saved connections
	 * @throws GraphStorageException 
	 */
	long saveConnectionsOnSegments(List<T> segmentsWithConnections, boolean saveSegments,
			String graphName, String version) throws GraphStorageException, GraphNotExistsException;

	/**
	 * save connections to the graph. 
	 * 
	 * @param segmentsWithConnections
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @return count of saved connections
	 */
	long saveConnections(List<IWaySegmentConnection> connections, String graphName, String version);

	/**
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 */
	void createIndexes(String graphName, String version);
	
	/**
	 * 
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @throws GraphNotExistsException 
	 */
	void deleteSegments(String graphName, String version) throws GraphNotExistsException;

	/**
	 * @param segments segments to update
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @throws GraphStorageException if problems during persisting graph occurred	
	 */
	void updateSegments(List<T> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;

	/**
	 * @param segments segments with connections to update
	 * @param graphName name of the graph
	 * @param version name of the graph's version
	 * @return
	 */
	long updateConnections(List<T> segments, String graphName, String version);

	/**
	 * method triggered after graph is imported and metadata is persisted but method should execute
	 * in same transaction. can be used to e.g. create constraints, checks, ... 
	 * 
	 * @param graphVersionMeta metadata object of stored graph
	 */
	void postCreateGraph(IWayGraphVersionMetadata graphVersionMeta);
	
}