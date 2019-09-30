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
package at.srfg.graphium.api.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.api.service.IGraphService;
import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author anwagner & mwimmer & 
 *
 * add controller and path in concrete implementing class. Path has to be different for different types
 * 
 * //@Controller
 * //@RequestMapping(value = "/segments")
 */
public abstract class AbstractGraphApiController<T extends IBaseWaySegment> { 

	private static Logger log = LoggerFactory.getLogger(GraphApiController.class);

	private IGraphService<T> graphApiService;
	private IGraphVersionMetadataService metadataService;
	private IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter;
	
	private String charset = "UTF-8";

	//	curl -X POST "http://localhost:8080/graphium-central-server/api/import?graphName=gip_at_frc_0_4&version=15_02_150414&originGraphName=gip_at&originVersion=15_02_150331&validFrom=1428932718000&source=GIP&overrideIfExists=true&creator=Michi&originUrl=http://gip.at" -F "file=@D:/development/project_data/graphserver/gip_15_02_frc0_4_private_car_small.json"
	@RequestMapping(value="/graphs/{graph}/versions/{version}", method=RequestMethod.POST)
	@ResponseBody
    public IGraphVersionMetadataDTO importGraphVersion(
    		@PathVariable(value = "graph") String graphName,
    		@PathVariable(value = "version") String version,
    		@RequestParam(value = "overrideIfExists", required = false, defaultValue = "false") boolean overrideIfExists,
            @RequestParam(value = "excludedXInfos", required = false) String excludedXInfos,
            @RequestParam(value = "file") MultipartFile file)
			throws GraphAlreadyExistException, GraphImportException, IOException, ValidationException {
    	
		return this.importGraph(graphName,version,overrideIfExists,excludedXInfos, file);
    }

    //private IGraphVersionMetadataDTO importGraph(String graphName, String version, boolean overrideIfExists, MultipartFile file)
    private IGraphVersionMetadataDTO importGraph(String graphName, String version, boolean overrideIfExists, String excludedXInfos, MultipartFile file)
			throws GraphAlreadyExistException, GraphImportException, IOException, ValidationException {
    	
		IWayGraphVersionMetadata metadata = this.graphApiService.importGraph(graphName, version, overrideIfExists, excludedXInfos, file);

		log.info("Import finished");
		return this.adapter.adapt(metadata);
	}
	
	// TODO: Sollte nur ein SuperUser ausführen dürfen!
	@RequestMapping(value = "/graphs/{graph}/versions/{version}", method = RequestMethod.DELETE)
	@ResponseBody
	public IGraphVersionMetadataDTO deleteGraphVersion(
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String version,
			@RequestParam(value = "keepMetadata", required = false, defaultValue = "true") Boolean keepMetadata) throws GraphNotExistsException {
		
		graphApiService.deleteGraphVersion(graphName, version, keepMetadata);
		if (keepMetadata) {
			return this.adapter.adapt(metadataService.getWayGraphVersionMetadata(graphName, version));
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/graphs/{graph}/versions/{version}", method = RequestMethod.GET)
	public void getGraphVersion(
			@RequestHeader(value = "Accept", required = false, defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptTypes,
			@RequestHeader(value = "Accept-Charset", required = false) String charset,
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String versionName,
			@RequestParam(value = "ids", required = false) List<Long> ids,
			@RequestParam(value = "compress", required = false, defaultValue = "false") boolean compress,
			OutputStream outputStream)
			throws ResourceNotFoundException, HttpMediaTypeNotAcceptableException, GraphNotExistsException, 
			IOException, WaySegmentSerializationException {

		IWayGraphVersionMetadata metadata = getMetadata(graphName, versionName);
		
		if (compress) {
			outputStream = new GZIPOutputStream(outputStream);
		}
		
		if (ids == null) {
			graphApiService.streamGraphVersion(metadata, outputStream);
		} else {
			Set<Long> idSet = new HashSet<>(ids);
			graphApiService.streamGraphVersion(metadata, outputStream, idSet);
		}
	}

	// eigener Endpoint //graphs/{graph}/versions/{version}/connected?ids=MANDATORY&incomming=OPTIONAL_DEFAULT_TRUE&outgoing=OPTIONAL_DEFAULT_TRUE
	@RequestMapping(value = "/graphs/{graph}/versions/{version}/incomingconnected", method = RequestMethod.GET)
	public void getConnectedSegments(
			@RequestHeader(value = "Accept", required = false, defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptTypes,
			@RequestHeader(value = "Accept-Charset", required = false) String charset,
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String versionName,
			@RequestParam(value = "ids", required = true) List<Long> ids,
			OutputStream outputStream)
			throws ResourceNotFoundException, HttpMediaTypeNotAcceptableException, GraphNotExistsException, 
			IOException, WaySegmentSerializationException {

		IWayGraphVersionMetadata metadata = getMetadata(graphName, versionName);
		
		graphApiService.streamIncomingConnectedStreetSegments(metadata, outputStream, new HashSet<>(ids));
	}
	
	protected IWayGraphVersionMetadata getMetadata(String graphName, String versionName) throws GraphNotExistsException {
		IWayGraphVersionMetadata metadata;

		if (versionName.equalsIgnoreCase("current")) {
			metadata = metadataService.getCurrentWayGraphVersionMetadata(graphName);
		} else {
			metadata = metadataService.getWayGraphVersionMetadata(graphName, versionName);
		}
		if (metadata == null) {
			throw new GraphNotExistsException("No graph with name " + graphName + " and version " + versionName + " exists", graphName);
		}
		return metadata;
	}
	
	public IGraphService<T> getGraphApiReadService() {
		return graphApiService;
	}

	public void setGraphApiReadService(IGraphService<T> graphApiReadService) {
		this.graphApiService = graphApiReadService;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}


	public IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> getAdapter() {
		return adapter;
	}

	public void setAdapter(IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter) {
		this.adapter = adapter;
	}

	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public void setGraphApiService(IGraphService<T> graphApiService) {
		this.graphApiService = graphApiService;
	}

	public IGraphService<T> getGraphApiService() {
		return graphApiService;
	}
}