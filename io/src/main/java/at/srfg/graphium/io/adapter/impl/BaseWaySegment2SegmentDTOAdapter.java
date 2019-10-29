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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.dto.IBaseWaySegmentDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.dto.ISegmentConnectionDTO;
import at.srfg.graphium.io.dto.impl.BaseWaySegmentDTOImpl;
import at.srfg.graphium.io.dto.impl.SegmentConnectionDTOImpl;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.BaseWaySegment;

/**
 * @author mwimmer
 *
 */
public class BaseWaySegment2SegmentDTOAdapter<O extends IBaseWaySegmentDTO, I extends IBaseWaySegment> extends BaseSegment2SegmentDTOAdapter<O,I>  {

	private static Logger log = LoggerFactory.getLogger(BaseWaySegment2SegmentDTOAdapter.class);

	public BaseWaySegment2SegmentDTOAdapter() {
		super(BaseWaySegment.class, BaseWaySegmentDTOImpl.class);
	}
	
	public BaseWaySegment2SegmentDTOAdapter(Class<? extends IBaseWaySegment> modelClass, Class<? extends IBaseWaySegmentDTO> dtoClass) {
		super(modelClass, dtoClass);
	}

	@Override
	public O adapt(I segment) {
		O segmentDTO = (O) new BaseWaySegmentDTOImpl();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}

    @Override
    public I adaptReverse(O dto) {
        I segment = (I) new BaseWaySegment();
        setModelValues(segment,dto);
        return segment;
    }

    @Override
    protected void setModelValues(I segment, O segmentDTO) {
        super.setModelValues(segment, segmentDTO);
        segment.setGeometry(segmentDTO.getGeometry());
		// calculate length from geometry
		segment.setLength((float) GeometryUtils.calculateLengthMeterFromWGS84LineStringAndoyer(segment.getGeometry()));
				
        segment.setEndNodeId(segmentDTO.getEndNodeId());
        segment.setStartNodeId(segmentDTO.getStartNodeId());
        segment.setStartNodeIndex((int)segmentDTO.getStartNodeIndex());
        segment.setEndNodeIndex((int)segmentDTO.getEndNodeIndex());
        segment.setName(segmentDTO.getName());
        segment.setStreetType(segmentDTO.getStreetType());
        segment.setTags(segmentDTO.getTags());
        segment.setWayId(segmentDTO.getWayId());
    }

    protected void setDtoValues(O segmentDTO, I segment) {
		super.setDtoValues(segmentDTO, segment);
		segmentDTO.setGeometry(segment.getGeometry());
		segmentDTO.setEndNodeId(segment.getEndNodeId());
		segmentDTO.setStartNodeId(segment.getStartNodeId());
		segmentDTO.setEndNodeIndex(segment.getEndNodeIndex());
		segmentDTO.setStartNodeIndex(segment.getStartNodeIndex());
		segmentDTO.setName(segment.getName());
		segmentDTO.setStreetType(segment.getStreetType());
		segmentDTO.setTags(segment.getTags());
		segmentDTO.setWayId(segment.getWayId());
	}



	protected ISegmentConnectionDTO adaptConnectionToDTO(IWaySegmentConnection conn) {
		Map<String, List<IConnectionXInfoDTO>> connectionXInfoDTOMap = null;
		try {
			connectionXInfoDTOMap = adaptConnXinfoToDTO(conn.getXInfo());
		} catch (XInfoNotSupportedException e) {
            log.error("adaption of connection's xinfo failed", e);
		}
		return new SegmentConnectionDTOImpl(conn.getNodeId(), conn.getToSegmentId(), connectionXInfoDTOMap, conn.getAccess(), conn.getTags());
	}

}