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
package at.srfg.graphium.api.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

import com.vividsolutions.jts.geom.Polygon;

/**
 * @author mwimmer
 *
 */
public interface IGraphService<T extends IBaseWaySegment> {
	
	/**
	 * 
	 * @param metadata
	 * @param outputStream
	 * @throws IOException
	 * @throws WaySegmentSerializationException
	 */
	void streamGraphVersion(IWayGraphVersionMetadata metadata, OutputStream outputStream)
			throws IOException, WaySegmentSerializationException;

	/**
	 * 
	 * @param metadata
	 * @param outputStream
	 * @throws IOException
	 * @throws WaySegmentSerializationException
	 * @throws GraphNotExistsException 
	 */
	void streamGraphVersion(IWayGraphVersionMetadata metadata, OutputStream outputStream, Set<Long> ids)
			throws IOException, WaySegmentSerializationException, GraphNotExistsException;

	/**
	 * 
	 * @param graphName
	 * @param version
	 * @param overrideIfExists
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws GraphAlreadyExistException
	 * @throws GraphImportException
	 */
	IWayGraphVersionMetadata importGraph(String graphName, String version, boolean overrideIfExists, MultipartFile file) throws IOException, GraphAlreadyExistException, GraphImportException;

	/**
	 * @param outputFormat
	 * @param bounds
	 * @param graphName
	 * @param version
	 * @throws WaySegmentSerializationException
	 * @throws GraphNotExistsException 
	 */
	void streamStreetSegments(IWayGraphOutputFormat<T> outputFormat,
			Polygon bounds, String graphName, String version)
		throws WaySegmentSerializationException, GraphNotExistsException;

	/**
	 * Deletes a graph version. If keepMetadata is set to false the metadata entry will be kept and its state will be changed to DELETED. 
	 * Otherwise the metadata entry will be deleted, too.
	 * @param graphName		Name of the graph
	 * @param version		Name of the version
	 * @param keepMetadata	Keep (true) or delete (false) metadata entry
	 * @throws GraphNotExistsException 
	 */
	void deleteGraphVersion(String graphName, String version, boolean keepMetadata) throws GraphNotExistsException;

	/**
	 * @param outputFormat
	 * @param graphName
	 * @param version
	 * @param ids
	 * @throws WaySegmentSerializationException
	 * @throws GraphNotExistsException 
	 */
	void streamStreetSegments(IWayGraphOutputFormat<T> outputFormat, String graphName, String version, Set<Long> ids)
			throws WaySegmentSerializationException, GraphNotExistsException;
	
}
