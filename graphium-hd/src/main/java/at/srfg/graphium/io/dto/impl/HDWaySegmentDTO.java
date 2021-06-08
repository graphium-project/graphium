/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.io.dto.IHDWaySegmentDTO;
import at.srfg.graphium.io.inputformat.impl.jackson.JacksonLineStringDeserializer;
import at.srfg.graphium.io.outputformat.impl.jackson.JacksonGeometrySerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDWaySegmentDTO extends WaySegmentDTOImpl implements IHDWaySegmentDTO {
	
	private LineString leftBorderGeometry;
	private long leftBorderStartNodeId;
	private long leftBorderEndNodeId;
	private LineString rightBorderGeometry;
	private long rightBorderStartNodeId;
	private long rightBorderEndNodeId;
	
	public HDWaySegmentDTO() {}
	
	public HDWaySegmentDTO(LineString leftBorderGeometry, long leftBorderStartNodeId, long leftBorderEndNodeId,
			LineString rightBorderGeometry, long rightBorderStartNodeId, long rightBorderEndNodeId) {
		super();
		this.leftBorderGeometry = leftBorderGeometry;
		this.leftBorderStartNodeId = leftBorderStartNodeId;
		this.leftBorderEndNodeId = leftBorderEndNodeId;
		this.rightBorderGeometry = rightBorderGeometry;
		this.rightBorderStartNodeId = rightBorderStartNodeId;
		this.rightBorderEndNodeId = rightBorderEndNodeId;
	}

	@Override
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public LineString getLeftBorderGeometry() {
		return leftBorderGeometry;
	}
	
	@Override
	public void setLeftBorderGeometry(LineString geometry) {
		leftBorderGeometry = geometry;
	}
	
	@Override
	public long getLeftBorderStartNodeId() {
		return leftBorderStartNodeId;
	}
	
	@Override
	public void setLeftBorderStartNodeId(long startNodeId) {
		leftBorderStartNodeId = startNodeId;
	}
	
	@Override
	public long getLeftBorderEndNodeId() {
		return leftBorderEndNodeId;
	}
	
	@Override
	public void setLeftBorderEndNodeId(long endNodeId) {
		leftBorderEndNodeId = endNodeId;
	}
	
	@Override
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public LineString getRightBorderGeometry() {
		return rightBorderGeometry;
	}
	
	@Override
	public void setRightBorderGeometry(LineString geometry) {
		rightBorderGeometry = geometry;
	}
	
	@Override
	public long getRightBorderStartNodeId() {
		return rightBorderStartNodeId;
	}
	
	@Override
	public void setRightBorderStartNodeId(long startNodeId) {
		rightBorderStartNodeId = startNodeId;
	}
	
	@Override
	public long getRightBorderEndNodeId() {
		return rightBorderEndNodeId;
	}
	
	@Override
	public void setRightBorderEndNodeId(long endNodeId) {
		rightBorderEndNodeId = endNodeId;
	}
	
}
