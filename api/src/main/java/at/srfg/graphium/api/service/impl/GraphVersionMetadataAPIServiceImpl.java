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
package at.srfg.graphium.api.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.api.events.notifier.IGraphVersionImportNotifier;
import at.srfg.graphium.api.exceptions.InconsistentStateException;
import at.srfg.graphium.api.exceptions.NotificationException;
import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.api.service.IGraphVersionMetadataAPIService;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.helper.GraphVersionValidityPeriodValidator;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataContainerDTO;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataContainerDTOImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public class GraphVersionMetadataAPIServiceImpl implements IGraphVersionMetadataAPIService {

	private static Logger log = LoggerFactory.getLogger(GraphVersionMetadataAPIServiceImpl.class);

	private IGraphVersionImportNotifier importNotifier;
	private IGraphVersionMetadataService metadataService;
	private IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter;
	private GraphVersionValidityPeriodValidator validityPeriodValidator;

	public Map<String,Class> allowedFieldsToEdit;

	@PostConstruct
	public void init() {
		this.allowedFieldsToEdit = new HashMap<>();
		this.allowedFieldsToEdit.put("type",String.class);
		this.allowedFieldsToEdit.put("description",String.class);
		this.allowedFieldsToEdit.put("validFrom",Long.class);
		this.allowedFieldsToEdit.put("validTo",Long.class);
	}

	@Override
	public Class getAllowedClass(String field) {
		return this.allowedFieldsToEdit.get(field);
	}
	
	public List<String> getGraphs() {
		return metadataService.getViews();
	}

	@Override
	@Transactional
	public IGraphVersionMetadataDTO updateGraphVersion(IGraphVersionMetadataDTO dto) throws ValidationException {
		this.validate(dto);
		
		IWayGraphVersionMetadata versionedMetaData = metadataService.getWayGraphVersionMetadata(dto.getGraphName(), dto.getVersion());
		versionedMetaData.setType(dto.getType());
		versionedMetaData.setDescription(dto.getDescription());
		if (dto.getValidFrom() != null) {
			versionedMetaData.setValidFrom(new Date(dto.getValidFrom()));
		} else {
			versionedMetaData.setValidFrom(null);
		}
		if (dto.getValidTo() != null) {
			versionedMetaData.setValidTo(new Date(dto.getValidTo()));
		} else {
			versionedMetaData.setValidTo(null);
		}
		
		// validate validity period
		List<String> errorMessages = validityPeriodValidator.validateValidityPeriod(versionedMetaData);
		if (errorMessages != null) {
			String msg = StringUtils.join(errorMessages, "; ");
			log.error(msg);			
			throw new ValidationException(msg);
		}
		
		metadataService.updateGraphVersion(versionedMetaData);
		return this.adapter.adapt(versionedMetaData);
	}

	@Transactional(readOnly = true)
	public IGraphVersionMetadataContainerDTO getCurrentVersion(String graphName) {
		IWayGraphVersionMetadata metadata = metadataService.getCurrentWayGraphVersionMetadata(graphName);
		
		IGraphVersionMetadataContainerDTO container = null;
		if (metadata != null) {
			List<IGraphVersionMetadataDTO> metadataDtoList = new ArrayList<IGraphVersionMetadataDTO>();
			metadataDtoList.add(adapter.adapt(metadata));
			container = new GraphVersionMetadataContainerDTOImpl(metadataDtoList);
		}
		
		return container;
	}

	@Transactional(readOnly = true)
	public IGraphVersionMetadataContainerDTO getVersions(String graphName, Date startTimestamp, Date endTimestamp) {
		List<IWayGraphVersionMetadata> metadataList = metadataService.getWayGraphVersionMetadataList(
															graphName, null, startTimestamp, endTimestamp, null);
		IGraphVersionMetadataContainerDTO container = null;
		if (metadataList != null && !metadataList.isEmpty()) {
			List<IGraphVersionMetadataDTO> metadataDtoList = new ArrayList<IGraphVersionMetadataDTO>();
			for (IWayGraphVersionMetadata metadata : metadataList) {
				metadataDtoList.add(adapter.adapt(metadata));
			}
			container = new GraphVersionMetadataContainerDTOImpl(metadataDtoList);
		}
		
		return container;
	}

	@Transactional(readOnly = true)
	public IGraphVersionMetadataContainerDTO getVersionMetadata(String graphName, String version) {
		IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(graphName, version);
		IGraphVersionMetadataContainerDTO container = null;
		if (metadata != null) {
			List<IGraphVersionMetadataDTO> metadataDtoList = new ArrayList<IGraphVersionMetadataDTO>();
			metadataDtoList.add(adapter.adapt(metadata));
			container = new GraphVersionMetadataContainerDTOImpl(metadataDtoList);
		}
 		
		return container;
	}
	
	@Transactional
	public void setGraphVersionState(String graphName, String version, State state) {
		metadataService.setGraphVersionState(graphName, version, state);
	}

	//Moved to service to run in an own transaction. So Rollback is performed in case of an error
	@Override
	@Transactional
	public String changeState(String graphName, String version, String state, Integer segmentsCount, String groupName) throws NotificationException, ResourceNotFoundException, InconsistentStateException, GraphNotExistsException {
		IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(graphName, version);
		if (metadata == null) {
			throw new GraphNotExistsException("Graph version " + version + " does not exist", graphName);
		}
		
		if (State.valueOf(state).equals(State.PUBLISH)) {

			boolean ok = true;

			// set state to PUBLISH (if not already ACTIVE)
			if (!metadata.getState().equals(State.ACTIVE)) {
				this.setGraphVersionState(graphName, version, State.valueOf(state));
			}
			
			if (importNotifier != null) {
				// notify registered graph servers
				ok = importNotifier.notifyRegisteredServersOfPublishing(graphName, version, groupName);
			}
			if (!ok) {
				throw new NotificationException("Changed State to " + state + " could not be published to registered servers");
			}

		} else if (State.valueOf(state).equals(State.ACTIVE)) {

			boolean allActivated = false;

			// check metadata
			if (segmentsCount == null || segmentsCount == 0 || (metadata.getSegmentsCount() == segmentsCount)) {

				allActivated = true;
				if (importNotifier != null) {
					// notify registered graph servers

					// TODO: segmentsCount could be set to enforce validation on subscribed server side; BUT segmentCount has to be set for each view
					//		 individually (e.g. View with LIMIT...)!
//					if (segmentsCount == null) {
//						// set segmentsCount so registered servers have to be validated
//						segmentsCount = metadataDTO.get(0).getSegmentsCount();
//					}
					allActivated = importNotifier.notifyRegisteredServersOfActivating(graphName, version, groupName, segmentsCount);

					if (allActivated) {
						// notify registered graph servers of successful activation (fire and forget)
						importNotifier.notifyRegisteredServersOfSuccessfullyActivating(graphName, version, groupName, segmentsCount);
						// set local state to activated
						this.setGraphVersionState(graphName, version, State.valueOf(state));
					} else {
						// notify registered graph servers of failed activation
						importNotifier.notifyRegisteredServersOfFailedActivating(graphName, version, groupName, segmentsCount);
						throw new NotificationException("State of " + state + " could not be published to all servers");
					}
				} else {
					// set local state to activated
					this.setGraphVersionState(graphName, version, State.valueOf(state));
				}
			} else {
				throw new InconsistentStateException(("Local activation failed - segments count not equal: locally stored " + metadata.getSegmentsCount() +
							" segments but should be " + segmentsCount));
			}
		} else {
			this.setGraphVersionState(graphName, version, State.valueOf(state));
		}
		//Finally return the updated metadata object as validation
		return this.getVersionMetadata(graphName,version).getGraphVersionMetadata().get(0).getState();
	}

	private void validate(IGraphVersionMetadataDTO dto) throws ValidationException {
		boolean valid = dto != null
				&& dto.getState() != null
				&& dto.getValidFrom() != null
				&& dto.getVersion() != null
				&& dto.getGraphName() != null
				&& (dto.getValidTo() == null || dto.getValidTo() > dto.getValidFrom());
		if (!valid) {
			throw new ValidationException("Change to metadata is not allowed");
		}
	}

	@Transactional(readOnly = true)
	public String checkUpdate(String graphName, String version) {
		return metadataService.checkNewerVersionAvailable(graphName, version);
	}
	
	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> getAdapter() {
		return adapter;
	}

	public void setAdapter(
			IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter) {
		this.adapter = adapter;
	}

	public IGraphVersionImportNotifier getImportNotifier() {
		return importNotifier;
	}

	public void setImportNotifier(IGraphVersionImportNotifier importNotifier) {
		this.importNotifier = importNotifier;
	}

	public GraphVersionValidityPeriodValidator getValidityPeriodValidator() {
		return validityPeriodValidator;
	}

	public void setValidityPeriodValidator(GraphVersionValidityPeriodValidator validityPeriodValidator) {
		this.validityPeriodValidator = validityPeriodValidator;
	}
	
}
