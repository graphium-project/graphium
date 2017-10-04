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
package at.srfg.graphium.io.adapter.impl;

import java.util.HashSet;
import java.util.Set;

import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public class GraphVersionMetadata2GraphVersionMetadataDTOAdapter 
	implements IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> {
	
	@Override
	public IGraphVersionMetadataDTO adapt(IWayGraphVersionMetadata metadata) {
		Set<Access> accesses = metadata.getAccessTypes();
		Set<String> accessNames = new HashSet<>(Access.values().length);
		if (accesses != null) {
			for (Access access : accesses) {
				accessNames.add(access.name());
			}
		}
		
		IGraphVersionMetadataDTO metadataDto = new GraphVersionMetadataDTOImpl(
				metadata.getId(), 
				metadata.getGraphName(), 
				metadata.getVersion(), 
				metadata.getOriginGraphName(), 
				metadata.getOriginVersion(), 
				(metadata.getState() != null ? metadata.getState().name() : State.INITIAL.name()), 
				metadata.getValidFrom().getTime(), 
				(metadata.getValidTo() == null ? null : metadata.getValidTo().getTime()), 
				(metadata.getCoveredArea() != null ? metadata.getCoveredArea().toText() : null), 
				metadata.getSegmentsCount(), 
				metadata.getConnectionsCount(), 
				accessNames, 
				metadata.getTags(), 
				metadata.getSource().getName(), 
				metadata.getType(), 
				metadata.getDescription(), 
				(metadata.getCreationTimestamp() != null ? metadata.getCreationTimestamp().getTime() : 0), 
				(metadata.getStorageTimestamp() != null ? metadata.getStorageTimestamp().getTime() : 0), 
				metadata.getCreator(), 
				metadata.getOriginUrl());

		return metadataDto;
	}

}
