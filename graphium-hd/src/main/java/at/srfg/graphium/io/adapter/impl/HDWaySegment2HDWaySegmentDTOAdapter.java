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
package at.srfg.graphium.io.adapter.impl;

import at.srfg.graphium.io.dto.IHDWaySegmentDTO;
import at.srfg.graphium.io.dto.impl.HDWaySegmentDTO;
import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.model.impl.HDWaySegment;

public class HDWaySegment2HDWaySegmentDTOAdapter<O extends IHDWaySegmentDTO, I extends IHDWaySegment> extends WaySegment2SegmentDTOAdapter<O, I> {

	public HDWaySegment2HDWaySegmentDTOAdapter() {
		super(HDWaySegment.class, HDWaySegmentDTO.class);
	}
	
	@Override
	public O adapt(I segment) {
		O segmentDTO = (O) new HDWaySegmentDTO();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}

	@Override
	public I adaptReverse(O dto) {
		I segment = (I) new HDWaySegment();
		setModelValues(segment,dto);
		return segment;
	}

	@Override
	protected void setModelValues(I segment, O segmentDTO) {
		super.setModelValues(segment, segmentDTO);
		segment.setLeftBoarderGeometry(segmentDTO.getLeftBoarderGeometry());
		segment.setLeftBoarderStartNodeId(segmentDTO.getLeftBoarderStartNodeId());
		segment.setLeftBoarderEndNodeId(segmentDTO.getLeftBoarderEndNodeId());
		segment.setRightBoarderGeometry(segmentDTO.getRightBoarderGeometry());
		segment.setRightBoarderStartNodeId(segmentDTO.getRightBoarderStartNodeId());
		segment.setRightBoarderEndNodeId(segmentDTO.getRightBoarderEndNodeId());
	}

	protected void setDtoValues(O segmentDTO, I segment) {
		super.setDtoValues(segmentDTO, segment);
		segmentDTO.setLeftBoarderGeometry(segment.getLeftBoarderGeometry());
		segmentDTO.setLeftBoarderStartNodeId(segment.getLeftBoarderStartNodeId());
		segmentDTO.setLeftBoarderEndNodeId(segment.getLeftBoarderEndNodeId());
		segmentDTO.setRightBoarderGeometry(segment.getRightBoarderGeometry());
		segmentDTO.setRightBoarderStartNodeId(segment.getRightBoarderStartNodeId());
		segmentDTO.setRightBoarderEndNodeId(segment.getRightBoarderEndNodeId());
	}
	
}