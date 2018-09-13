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
package at.srfg.graphium.io.dto.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IBaseWaySegmentDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.inputformat.impl.jackson.JacksonLineStringDeserializer;
import at.srfg.graphium.io.outputformat.impl.jackson.JacksonGeometrySerializer;

/**
 * @author mwimmer
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseWaySegmentDTOImpl extends BaseSegmentDTOImpl implements IBaseWaySegmentDTO {

	protected LineString geometry;
	protected String name;
	protected String streetType;
	protected long wayId;
	protected long startNodeIndex;
	protected long startNodeId;
	protected long endNodeIndex;
	protected long endNodeId;
	protected Map<String, String> tags;
	
	public BaseWaySegmentDTOImpl(){};
	
	public BaseWaySegmentDTOImpl(long id, LineString geometry, String name,
			String streetType, long wayId, long startNodeIndex,
			long startNodeId, long endNodeIndex, long endNodeId,
			Map<String,String> tags,
			List<IBaseSegmentConnectionDTO> connection, Map<String,List<ISegmentXInfoDTO>> xInfo) {
		super(id,connection,xInfo);
		this.geometry = geometry;
		this.name = name;
		this.streetType = streetType;
		this.wayId = wayId;
		this.startNodeIndex = startNodeIndex;
		this.startNodeId = startNodeId;
		this.endNodeIndex = endNodeIndex;
		this.endNodeId = endNodeId;
		this.tags = tags;
	}

	public LineString getGeometry() {
		return geometry;
	}
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public void setGeometry(LineString geometry) {
		this.geometry = geometry;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreetType() {
		return streetType;
	}
	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}
	public long getWayId() {
		return wayId;
	}
	public void setWayId(long wayId) {
		this.wayId = wayId;
	}
	public long getStartNodeIndex() {
		return startNodeIndex;
	}
	public void setStartNodeIndex(long startNodeIndex) {
		this.startNodeIndex = startNodeIndex;
	}
	public long getStartNodeId() {
		return startNodeId;
	}
	public void setStartNodeId(long startNodeId) {
		this.startNodeId = startNodeId;
	}
	public long getEndNodeIndex() {
		return endNodeIndex;
	}
	public void setEndNodeIndex(long endNodeIndex) {
		this.endNodeIndex = endNodeIndex;
	}
	public long getEndNodeId() {
		return endNodeId;
	}
	public void setEndNodeId(long endNodeId) {
		this.endNodeId = endNodeId;
	}
	public Map<String, String> getTags() {
		return tags;
	}
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	@Override
	@JsonDeserialize(contentAs = SegmentConnectionDTOImpl.class)
	public List<IBaseSegmentConnectionDTO> getConnection() {
		return connection;
	}
}