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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import at.srfg.graphium.api.exceptions.InconsistentStateException;
import at.srfg.graphium.api.exceptions.NotificationException;
import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.api.service.IGraphVersionMetadataAPIService;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.dto.IGraphVersionMetadataContainerDTO;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;

/**
 * @author mwimmer
 *
 */
@Controller
@RequestMapping(value = "/metadata/graphs")
public class GraphVersionMetadataApiController {

	private static Logger log = LoggerFactory.getLogger(GraphVersionMetadataApiController.class);

	private IGraphVersionMetadataAPIService graphVersionMetadataService;
	
	private String charset = "UTF-8";
	
	/**
	 * Note for methods: the caller knows the view name, but per default view name is equal to graph name; so it is not required
	 * to read the view to get the graph's metadata - you can directly read the graph's metadata with the view name (only for default view).
	 */
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public List<String> listGraphs() {
		return graphVersionMetadataService.getGraphs();
	}

	@RequestMapping(value = "/{graph}/versions", method = RequestMethod.GET)
	@ResponseBody
	public List<IGraphVersionMetadataDTO> listVersions(
			@PathVariable(value = "graph") String graphName,
			@RequestParam(value = "startTimestamp", required = false) Long startTimestamp,
			@RequestParam(value = "endTimestamp", required = false) Long endTimestamp) {

		Date startTs = null;
		if (startTimestamp != null) {
			startTs = new Date(startTimestamp);
		}
		Date endTs = null;
		if (endTimestamp != null) {
			endTs = new Date(endTimestamp);
		}
		
		IGraphVersionMetadataContainerDTO metadataDto = graphVersionMetadataService.getVersions(graphName, startTs, endTs);
		if (metadataDto != null) {
			return metadataDto.getGraphVersionMetadata();
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/{graph}/versions/{version}", method = RequestMethod.GET)
	@ResponseBody
	public IGraphVersionMetadataDTO getVersionMetadata(
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String version) throws ResourceNotFoundException {

		return this.getVersionMetadataElement(graphName, version);

	}

	@RequestMapping(value = "/{graph}/versions/{version}/{fieldname}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getCurrentVersionId(
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String version,
			@PathVariable(value = "fieldname") String fieldName) throws ResourceNotFoundException,
			IntrospectionException, InvocationTargetException, IllegalAccessException {

		IGraphVersionMetadataDTO versionMetadataDTO = this.getVersionMetadata(graphName, version);
		Object value = new PropertyDescriptor(fieldName, versionMetadataDTO.getClass()).getReadMethod().invoke(versionMetadataDTO);
		Map<String, String> result = new HashMap<>();
		result.put(fieldName, String.valueOf(value));
		return result;
	}

	private IGraphVersionMetadataDTO getVersionMetadataElement(String graphName, String version) throws ResourceNotFoundException {
		IGraphVersionMetadataContainerDTO metadataDto = null;
		if (version.equalsIgnoreCase("current")) {
			metadataDto = graphVersionMetadataService.getCurrentVersion(graphName);
		} else {
			metadataDto = graphVersionMetadataService.getVersionMetadata(graphName, version);
		}
		if (metadataDto != null && !metadataDto.getGraphVersionMetadata().isEmpty()) {
			return metadataDto.getGraphVersionMetadata().get(0);
		}
		throw new ResourceNotFoundException("Graph " + graphName + " in version " + version + " not found");
	}

	@RequestMapping(value = "/{graph}/checkupdate", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> checkUpdate(
			@PathVariable(value = "graph") String graphName,
			@RequestParam(value = "lastImportedVersion", required = false) String version) {
		
		if (version != null && version.equals("null")) {
			version = null;
		}
		String result = graphVersionMetadataService.checkUpdate(graphName, version);
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("update",result);
		return resultMap;
	}

	@RequestMapping(value = "/{graph}/versions/{version}/{fieldname}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String,Object> deleteGraphMetadataFields(
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String version,
			@PathVariable(value = "fieldname") String fieldName
	) throws NotificationException, ResourceNotFoundException, InconsistentStateException,
			IntrospectionException, InvocationTargetException, IllegalAccessException, AccessException, ValidationException {

		Map<String,Object> resultMap = new HashMap<>();

		log.info("Got request for deleting property " +
				"of graphMetadata from graph " + graphName + " and version " + version + " named " + fieldName);
			Class type = graphVersionMetadataService.getAllowedClass(fieldName);
			if (type != null) {
				IGraphVersionMetadataDTO versionMetadataDTO = this.getVersionMetadataElement(graphName, version);
				Method setter = new PropertyDescriptor(fieldName, versionMetadataDTO.getClass()).getWriteMethod();
				if (setter.getParameterTypes()[0].equals(type)) {
					setter.invoke(versionMetadataDTO,new Object[]{null});
				}
				IGraphVersionMetadataDTO updated = graphVersionMetadataService.updateGraphVersion(versionMetadataDTO);
				Object updatedElement = new PropertyDescriptor(fieldName,updated.getClass()).getReadMethod().invoke(updated);
				resultMap.put(fieldName,updatedElement);
				return resultMap;
			}
			throw new AccessException("Delete request to field denied");
	}

	@RequestMapping(value = "/{graph}/versions/{version}/{fieldname}/{value}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String,Object> setGraphMetadataFields(
			@PathVariable(value = "graph") String graphName,
			@PathVariable(value = "version") String version,
			@PathVariable(value = "fieldname") String fieldName,
			@PathVariable(value = "value") String value,
			@RequestParam(value = "groupname", required = false) String groupName,	// only considered when setting state to PUBLISH or ACTIVE
			@RequestParam(value = "segmentscount", required = false) Integer segmentsCount	// only considered when setting state to ACTIVE
			) throws NotificationException, ResourceNotFoundException, InconsistentStateException,
			IntrospectionException, InvocationTargetException, IllegalAccessException, AccessException, ValidationException, GraphNotExistsException {

		log.info("Got request for updating property " +
				"of graphMetadata from graph " + graphName + " and version " + version + " named " + fieldName + " set to "
		+ value);

		Map<String,Object> resultMap = new HashMap<>();

		if (fieldName.equalsIgnoreCase("state")) {
			String newState = graphVersionMetadataService.changeState(graphName,version,value,segmentsCount,groupName);
			resultMap.put(fieldName,newState);
			return resultMap;
		} else {
			Class type = graphVersionMetadataService.getAllowedClass(fieldName);
			if (type != null) {
				Object objectToSet = value;
				if (type.equals(Long.class)) {
					objectToSet = Long.parseLong(value);
				}
				IGraphVersionMetadataDTO versionMetadataDTO = this.getVersionMetadataElement(graphName, version);
				Method setter = new PropertyDescriptor(fieldName, versionMetadataDTO.getClass()).getWriteMethod();
				if (setter.getParameterTypes()[0].equals(type)) {
					setter.invoke(versionMetadataDTO,objectToSet);
				}
				IGraphVersionMetadataDTO updated = graphVersionMetadataService.updateGraphVersion(versionMetadataDTO);
				Object updatedElement = new PropertyDescriptor(fieldName,updated.getClass()).getReadMethod().invoke(updated);
				resultMap.put(fieldName,updatedElement);
				return resultMap;
			}
			throw new AccessException("Update request to  field denied");
		}
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public IGraphVersionMetadataAPIService getGraphVersionMetadataService() {
		return graphVersionMetadataService;
	}

	public void setGraphVersionMetadataService(
			IGraphVersionMetadataAPIService graphVersionMetadataService) {
		this.graphVersionMetadataService = graphVersionMetadataService;
	}

}