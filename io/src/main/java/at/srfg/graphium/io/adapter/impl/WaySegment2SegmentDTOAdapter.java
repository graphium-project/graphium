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

import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.dto.impl.WaySegmentDTOImpl;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WaySegment;

/**
 * @author mwimmer
 *
 */
public class WaySegment2SegmentDTOAdapter<O extends IWaySegmentDTO, I extends IWaySegment>
	extends BaseWaySegment2SegmentDTOAdapter<O, I> {

	public WaySegment2SegmentDTOAdapter() {
		super(WaySegment.class, WaySegmentDTOImpl.class);
	}

	public WaySegment2SegmentDTOAdapter(Class<? extends IWaySegment> modelClass, Class<? extends IWaySegmentDTO> dtoClass) {
		super(modelClass, dtoClass);
	}

	@Override
	public O adapt(I segment) {
		O segmentDTO = (O) new WaySegmentDTOImpl();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}

	@Override
	public I adaptReverse(O dto) {
		I segment = (I) new WaySegment();
		setModelValues(segment,dto);
		return segment;
	}

	@Override
	protected void setModelValues(I segment, O segmentDTO) {
		super.setModelValues(segment, segmentDTO);
		segment.setAccessBkw(segmentDTO.getAccessBkw());
		segment.setAccessTow(segmentDTO.getAccessTow());
		segment.setBridge(segmentDTO.isBridge());
		segment.setTunnel(segmentDTO.isTunnel());
		segment.setUrban(segmentDTO.isUrban());
		segment.setSpeedCalcBkw(segmentDTO.getCalcSpeedBkw());
		segment.setSpeedCalcTow(segmentDTO.getCalcSpeedTow());
		segment.setMaxSpeedBkw(segmentDTO.getMaxSpeedBkw());
		segment.setMaxSpeedTow(segmentDTO.getMaxSpeedTow());
		segment.setLanesBkw(segmentDTO.getLanesBkw());
		segment.setLanesTow(segmentDTO.getLanesTow());
		segment.setFrc(FuncRoadClass.getFuncRoadClassForValue(segmentDTO.getFrc()));
		segment.setFormOfWay(FormOfWay.valueOf(segmentDTO.getFormOfWay()));
	}

	protected void setDtoValues(O segmentDTO, I segment) {
		super.setDtoValues(segmentDTO, segment);
		segmentDTO.setAccessBkw(segment.getAccessBkw());
		segmentDTO.setAccessTow(segment.getAccessTow());
		if (segment.isBridge() != null) {
			segmentDTO.setBridge(segment.isBridge());
		}
		if (segment.isTunnel() != null) {
			segmentDTO.setTunnel(segment.isTunnel());
		}
		if (segment.isUrban() != null) {
			segmentDTO.setUrban(segment.isUrban());
		}
		if (segment.getSpeedCalcBkw() != null) {
			segmentDTO.setCalcSpeedBkw(segment.getSpeedCalcBkw());
		}
		if (segment.getSpeedCalcTow() != null) {
			segmentDTO.setCalcSpeedTow(segment.getSpeedCalcTow());
		}
		segmentDTO.setMaxSpeedBkw(segment.getMaxSpeedBkw());
		segmentDTO.setMaxSpeedTow(segment.getMaxSpeedTow());
		segmentDTO.setLanesBkw(segment.getLanesBkw());
		segmentDTO.setLanesTow(segment.getLanesTow());
		if (segment.getFrc() != null) {
			segmentDTO.setFrc(segment.getFrc().getValue());
		}
		if (segment.getFormOfWay() != null) {
			segmentDTO.setFormOfWay(segment.getFormOfWay().name());
		}
	}

}