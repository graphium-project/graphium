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
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDWaySegment;

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
		segment.setLeftBorderGeometry(segmentDTO.getLeftBorderGeometry());
		segment.setLeftBorderStartNodeId(segmentDTO.getLeftBorderStartNodeId());
		segment.setLeftBorderEndNodeId(segmentDTO.getLeftBorderEndNodeId());
		segment.setRightBorderGeometry(segmentDTO.getRightBorderGeometry());
		segment.setRightBorderStartNodeId(segmentDTO.getRightBorderStartNodeId());
		segment.setRightBorderEndNodeId(segmentDTO.getRightBorderEndNodeId());
	}

	protected void setDtoValues(O segmentDTO, I segment) {
		super.setDtoValues(segmentDTO, segment);
		segmentDTO.setLeftBorderGeometry(segment.getLeftBorderGeometry());
		segmentDTO.setLeftBorderStartNodeId(segment.getLeftBorderStartNodeId());
		segmentDTO.setLeftBorderEndNodeId(segment.getLeftBorderEndNodeId());
		segmentDTO.setRightBorderGeometry(segment.getRightBorderGeometry());
		segmentDTO.setRightBorderStartNodeId(segment.getRightBorderStartNodeId());
		segmentDTO.setRightBorderEndNodeId(segment.getRightBorderEndNodeId());
	}
	
}