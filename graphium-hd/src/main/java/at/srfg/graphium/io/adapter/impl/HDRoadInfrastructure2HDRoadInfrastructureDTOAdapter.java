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

import at.srfg.graphium.io.dto.IHDAreaDTO;
import at.srfg.graphium.io.dto.IHDRoadInfrastructureDTO;
import at.srfg.graphium.io.dto.impl.HDAreaDTO;
import at.srfg.graphium.io.dto.impl.HDRoadInfrastructureDTO;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDRoadInfrastructure;
import at.srfg.graphium.model.hd.impl.HDArea;
import at.srfg.graphium.model.hd.impl.HDRoadInfrastructure;

public class HDRoadInfrastructure2HDRoadInfrastructureDTOAdapter
		<O extends IHDRoadInfrastructureDTO, I extends IHDRoadInfrastructure> extends BaseSegment2SegmentDTOAdapter<O, I> {

	public HDRoadInfrastructure2HDRoadInfrastructureDTOAdapter() {
		super(HDRoadInfrastructure.class, HDRoadInfrastructureDTO.class);
	}
	
	@Override
	public O adapt(I segment) {
		O segmentDTO = (O) new HDRoadInfrastructureDTO();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}

	@Override
	public I adaptReverse(O dto) {
		I segment = (I) new HDRoadInfrastructure();
		setModelValues(segment,dto);
		return segment;
	}

	@Override
	protected void setModelValues(I segment, O segmentDTO) {
		super.setModelValues(segment, segmentDTO);
		segment.setGeometry(segmentDTO.getGeometry());
		segment.setType(segmentDTO.getType());
		segment.setTags(segmentDTO.getTags());
	}

	protected void setDtoValues(O segmentDTO, I segment) {
		super.setDtoValues(segmentDTO, segment);
		segmentDTO.setGeometry(segment.getGeometry());
		segmentDTO.setType(segment.getType());
		segmentDTO.setTags(segment.getTags());
	}

}
