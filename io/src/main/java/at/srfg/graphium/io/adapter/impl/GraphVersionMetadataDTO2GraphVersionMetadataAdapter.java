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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
public class GraphVersionMetadataDTO2GraphVersionMetadataAdapter 
	implements IAdapter<IWayGraphVersionMetadata, IGraphVersionMetadataDTO> {
	

	private static Logger log = LoggerFactory.getLogger(GraphVersionMetadataDTO2GraphVersionMetadataAdapter.class);
	@Override
	public IWayGraphVersionMetadata adapt(IGraphVersionMetadataDTO dto) {
		Set<Access> accesses = new HashSet<Access>();
		if (dto.getAccessTypes() != null) {
			for (String access : dto.getAccessTypes()) {
				accesses.add(Access.valueOf(access));
			}
		}
		WKTReader wktReader = new WKTReader();
		IWayGraphVersionMetadata metadata = null;
		try {
			metadata = new WayGraphVersionMetadata(
					dto.getId(), 				
					-1l,
					dto.getGraphName(), 
					dto.getVersion(), 
					dto.getOriginGraphName(), 
					dto.getOriginVersion(), 
					State.valueOf(dto.getState()),
					new Date(dto.getValidFrom()), 					
					dto.getValidTo() == null ? null : new Date(dto.getValidTo()), 
					(dto.getCoveredArea() != null ? (Polygon) wktReader.read(dto.getCoveredArea()) : null), 
					dto.getSegmentsCount(), 
					dto.getConnectionsCount(), 
					accesses, 
					dto.getTags(), 
					new Source(-1, dto.getSource()),
					dto.getType(), 
					dto.getDescription(), 
					new Date(dto.getCreationTimestamp()), 
					new Date(dto.getStorageTimestamp()), 
					dto.getCreator(), 
					dto.getOriginUrl());
		} catch (ParseException e) {
			log.error("error parsing wkt string",e); 
		}

		return metadata;
	}

}
