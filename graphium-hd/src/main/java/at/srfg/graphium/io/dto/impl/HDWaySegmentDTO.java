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
	
	private LineString leftBoarderGeometry;
	private long leftBoarderStartNodeId;
	private long leftBoarderEndNodeId;
	private LineString rightBoarderGeometry;
	private long rightBoarderStartNodeId;
	private long rightBoarderEndNodeId;
	
	public HDWaySegmentDTO() {}
	
	public HDWaySegmentDTO(LineString leftBoarderGeometry, long leftBoarderStartNodeId, long leftBoarderEndNodeId,
			LineString rightBoarderGeometry, long rightBoarderStartNodeId, long rightBoarderEndNodeId) {
		super();
		this.leftBoarderGeometry = leftBoarderGeometry;
		this.leftBoarderStartNodeId = leftBoarderStartNodeId;
		this.leftBoarderEndNodeId = leftBoarderEndNodeId;
		this.rightBoarderGeometry = rightBoarderGeometry;
		this.rightBoarderStartNodeId = rightBoarderStartNodeId;
		this.rightBoarderEndNodeId = rightBoarderEndNodeId;
	}

	@Override
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public LineString getLeftBoarderGeometry() {
		return leftBoarderGeometry;
	}
	
	@Override
	public void setLeftBoarderGeometry(LineString geometry) {
		leftBoarderGeometry = geometry;
	}
	
	@Override
	public long getLeftBoarderStartNodeId() {
		return leftBoarderStartNodeId;
	}
	
	@Override
	public void setLeftBoarderStartNodeId(long startNodeId) {
		leftBoarderStartNodeId = startNodeId;
	}
	
	@Override
	public long getLeftBoarderEndNodeId() {
		return leftBoarderEndNodeId;
	}
	
	@Override
	public void setLeftBoarderEndNodeId(long endNodeId) {
		leftBoarderEndNodeId = endNodeId;
	}
	
	@Override
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public LineString getRightBoarderGeometry() {
		return rightBoarderGeometry;
	}
	
	@Override
	public void setRightBoarderGeometry(LineString geometry) {
		rightBoarderGeometry = geometry;
	}
	
	@Override
	public long getRightBoarderStartNodeId() {
		return rightBoarderStartNodeId;
	}
	
	@Override
	public void setRightBoarderStartNodeId(long startNodeId) {
		rightBoarderStartNodeId = startNodeId;
	}
	
	@Override
	public long getRightBoarderEndNodeId() {
		return rightBoarderEndNodeId;
	}
	
	@Override
	public void setRightBoarderEndNodeId(long endNodeId) {
		rightBoarderEndNodeId = endNodeId;
	}
	
}
