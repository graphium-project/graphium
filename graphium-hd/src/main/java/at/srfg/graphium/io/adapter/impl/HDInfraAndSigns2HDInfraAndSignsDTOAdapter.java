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

import at.srfg.graphium.io.dto.IHDInfraAndSignsDTO;
import at.srfg.graphium.io.dto.impl.HDInfraAndSignsDTO;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.model.hd.impl.HDInfraAndSigns;

public class HDInfraAndSigns2HDInfraAndSignsDTOAdapter
		<O extends IHDInfraAndSignsDTO, I extends IHDInfraAndSigns> extends BaseSegment2SegmentDTOAdapter<O, I> {

	public HDInfraAndSigns2HDInfraAndSignsDTOAdapter() {
		super(HDInfraAndSigns.class, HDInfraAndSignsDTO.class);
	}
	
	@Override
	public O adapt(I segment) {
		O segmentDTO = (O) new HDInfraAndSignsDTO();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}

	@Override
	public I adaptReverse(O dto) {
		I segment = (I) new HDInfraAndSigns();
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
