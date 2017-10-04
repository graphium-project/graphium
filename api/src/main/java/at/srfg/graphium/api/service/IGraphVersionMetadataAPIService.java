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

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.api.exceptions.InconsistentStateException;
import at.srfg.graphium.api.exceptions.NotificationException;
import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.dto.IGraphVersionMetadataContainerDTO;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public interface IGraphVersionMetadataAPIService {

	Class getAllowedClass(String field);

	public List<String> getGraphs();

	@Transactional
	IGraphVersionMetadataDTO updateGraphVersion(IGraphVersionMetadataDTO dto) throws ValidationException;

	public IGraphVersionMetadataContainerDTO getCurrentVersion(String graphName);
	
	public IGraphVersionMetadataContainerDTO getVersions(
			String graphName, Date startTimestamp, Date endTimestamp);

	public IGraphVersionMetadataContainerDTO getVersionMetadata(String graphName, String version);

	public String checkUpdate(String graphName, String version);
	
	public void setGraphVersionState(String graphName, String version, State state);

	public String changeState(String graphName, String version, String state, Integer segmentsCount, String groupName)
			throws NotificationException, ResourceNotFoundException, InconsistentStateException, GraphNotExistsException;
	
}