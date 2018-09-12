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
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.api.service.IBaseSegmentXInfoService;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.model.IBaseSegment;

/**
 * Controller for extended Information API
 *
 * Created by shennebe on 21.09.2016.
 */
@RestController
@RequestMapping(value = "/")
public class GraphXInfoController {

    private static Logger log = LoggerFactory.getLogger(GraphXInfoController.class);

    @Resource(name="baseSegmentXInfoService")
    private IBaseSegmentXInfoService<IBaseSegment> service;

    @Resource(name="graphVersionMetadataService")
    private IGraphVersionMetadataService metadataService;

	@GetMapping(value="/segments/graphs/{graph}/versions/{version}/xinfos/{type}")
    @ResponseStatus(HttpStatus.OK)
    public void getXInfosOfSegments(@PathVariable String graph,
                                     @PathVariable String version,
                                     @PathVariable String type,
                                     OutputStream outputStream) throws IOException, XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException {
		this.service.streamBaseSegmentXInfos(graph, this.getCurrentVersion(graph,version), outputStream, type);
    }


    @PostMapping(value="/segments/graphs/{graph}/versions/{version}/xinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public String addXInfoToSegments(@PathVariable String graph,
                                     @PathVariable String version,
//                                     InputStream stream,
                                     @RequestParam(value = "file") MultipartFile file) throws IOException, XInfoNotSupportedException, GraphImportException, GraphNotExistsException, GraphStorageException, ValidationException {
//        this.service.streamBaseSegmentXInfos(graph,this.getCurrentVersion(graph,version),stream);
    	if (!file.isEmpty()) {
    		log.info("Parsing file " + file.getName() + " with " + file.getSize() + " Bytes");
            InputStream inputStream = file.getInputStream();
            this.service.streamBaseSegmentXInfos(graph,this.getCurrentVersion(graph,version),inputStream);
        }
        return null;
    }

    @DeleteMapping(value="/segments/graphs/{graph}/versions/{version}/xinfos/{type}")
    public String deleteXInfoFromSegments(@PathVariable String graph,
                                          @PathVariable String version,
                                          @PathVariable String type) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException {
        this.service.deleteBaseSegmentXInfos(graph,this.getCurrentVersion(graph,version),type);
        //TODO return something more meaningful
        return "Deleted";
    }

    @GetMapping(value="/connections/graphs/{graph}/versions/{version}/xinfos/{type}")
    public void getXInfoToConnections(@PathVariable String graph,
                                        @PathVariable String version,
                                        @PathVariable String type,
                                        OutputStream outputStream) throws XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException {
        this.service.streamBaseConnectionXInfos(graph, this.getCurrentVersion(graph,version), outputStream, type);
    }

    @DeleteMapping(value="/connections/graphs/{graph}/versions/{version}/xinfos/{type}")
    public String deleteXInfoFromConnections(@PathVariable String graph,
                                             @PathVariable String version,
                                             @PathVariable String type) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException {
        this.service.deleteConnectionXInfos(graph,this.getCurrentVersion(graph,version),type);
        //TODO return something more meaningful
        return "Deleted";
    }

    @PostMapping(value="/connections/graphs/{graph}/versions/{version}/xinfos")
    public String addXInfosToConnections(@PathVariable String graph,
                                         @PathVariable String version,
                                         @RequestParam(value = "excludedXInfos", required = false) String excludedXInfos,
                                         @RequestParam(value = "file") MultipartFile file) throws XInfoNotSupportedException,
            GraphImportException, GraphStorageException, GraphNotExistsException, IOException, ValidationException {
        if (!file.isEmpty()) {
            log.info("Parsing file " + file.getName() + " with " + file.getSize() + " Bytes");
            InputStream inputStream = file.getInputStream();
            this.service.streamBaseConnectionXInfos(graph,this.getCurrentVersion(graph,version), excludedXInfos, inputStream);
        }
        return null;
    }

    private String getCurrentVersion(String graph, String version) {
        if (version.equalsIgnoreCase("current")) {
            version = this.metadataService.getCurrentWayGraphVersionMetadata(graph).getVersion();
        }
        return version;
    }
    
    public IBaseSegmentXInfoService<IBaseSegment> getService() {
  		return service;
  	}

  	public void setService(IBaseSegmentXInfoService<IBaseSegment> service) {
  		this.service = service;
  	}

    public IGraphVersionMetadataService getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(IGraphVersionMetadataService metadataService) {
        this.metadataService = metadataService;
    }
}
