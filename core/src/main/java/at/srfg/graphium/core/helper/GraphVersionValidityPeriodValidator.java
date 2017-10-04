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
package at.srfg.graphium.core.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public class GraphVersionValidityPeriodValidator {

	private IGraphVersionMetadataService metadataService;

	/**
	 * Validates validity period of a graph version's metadata
	 * @param newGraphVersion
	 * @return List of error messages; if list is null validation is OK 
	 */
	public List<String> validateValidityPeriod(IWayGraphVersionMetadata newGraphVersion) {
		List<String> messages = new ArrayList<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<IWayGraphVersionMetadata> graphVersions = metadataService.getWayGraphVersionMetadataList(newGraphVersion.getGraphName());
		
		if (graphVersions != null && !graphVersions.isEmpty()) {
			for (IWayGraphVersionMetadata existingGraphVersion : graphVersions) {
				if (!existingGraphVersion.getVersion().equals(newGraphVersion.getVersion()) && // ignore same version
					!existingGraphVersion.getState().equals(State.DELETED)) {				   // ignore deleted versions

					if (newGraphVersion.getValidTo() == null) {

						// newGraphVersion:        |===========     
						// existingGraphVersion:       |=======
						
						if (existingGraphVersion.getValidTo() == null) {
							if (newGraphVersion.getValidFrom().getTime() <= existingGraphVersion.getValidFrom().getTime()) {
								messages.add("new version (" + df.format(newGraphVersion.getValidFrom()) + " - OPEN) overlaps with existing version " + 
										existingGraphVersion.getVersion() + " (" + df.format(existingGraphVersion.getValidFrom()) + " - OPEN)");
							}
						} else {

							// newGraphVersion:            |========   OR   |=============== 
							// existingGraphVersion:   |=========|              |========|
							
							if (newGraphVersion.getValidFrom().getTime() < existingGraphVersion.getValidTo().getTime()) {
								messages.add("new version (" + df.format(newGraphVersion.getValidFrom()) + " - OPEN) overlaps with existing version " + 
										existingGraphVersion.getVersion() + " (" + df.format(existingGraphVersion.getValidFrom()) + 
										" - " + df.format(existingGraphVersion.getValidTo()) + ")");
							}
						}
					} else {
						
						// newGraphVersion:           |=====|       OR       |============|     
						// existingGraphVersion:   |===========|          |===========|
						
						if (existingGraphVersion.getValidTo() != null) {
							if (existingGraphVersion.getValidFrom().getTime() <= newGraphVersion.getValidFrom().getTime() &&
								newGraphVersion.getValidFrom().getTime() < existingGraphVersion.getValidTo().getTime()) {
								messages.add("new version (" + df.format(newGraphVersion.getValidFrom()) + " - " + df.format(newGraphVersion.getValidTo()) + 
										") overlaps with existing version " + existingGraphVersion.getVersion() + 
										" (" + df.format(existingGraphVersion.getValidFrom()) + " - " + df.format(existingGraphVersion.getValidTo()) + ")");
							}
						}

						// newGraphVersion:        |=======|       OR   |=======|     
						// existingGraphVersion:       |========|           |===========
						
						if (newGraphVersion.getValidFrom().getTime() <= existingGraphVersion.getValidFrom().getTime() &&
							existingGraphVersion.getValidFrom().getTime() < newGraphVersion.getValidTo().getTime()) {
							messages.add("new version (" + df.format(newGraphVersion.getValidFrom()) + " - " + df.format(newGraphVersion.getValidTo()) + 
									") overlaps with existing version " + existingGraphVersion.getVersion() + 
									" (" + df.format(existingGraphVersion.getValidFrom()) + " - " + 
									(existingGraphVersion.getValidTo() == null ? "OPEN" : df.format(existingGraphVersion.getValidTo())) + ")");
						}
					}

				}
			}
		}
		
		if (messages.isEmpty()) {
			return null;
		} else {
			return messages;
		}
	}
	
	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

}
