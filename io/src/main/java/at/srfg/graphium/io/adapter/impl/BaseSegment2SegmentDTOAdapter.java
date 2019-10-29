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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IBidrectionalAdapter;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.dto.ISegmentConnectionDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.dto.impl.BaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.impl.BaseSegmentDTOImpl;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.model.impl.WaySegmentConnection;

/**
 * @author mwimmer
 *
 */
public class BaseSegment2SegmentDTOAdapter<O extends IBaseSegmentDTO,I extends IBaseSegment>
	extends AbstractSegmentDTOAdapter<O, I> {

	private static Logger log = LoggerFactory.getLogger(BaseSegment2SegmentDTOAdapter.class);
	
    public BaseSegment2SegmentDTOAdapter(Class<? extends IBaseSegment> modelClass, Class<? extends IBaseSegmentDTO> dtoClass) {
        super(modelClass, dtoClass);
    }

    private SegmentXInfoMapAdapter segmentXInfoMapAdapter = new SegmentXInfoMapAdapter();

    private ConnectionModelDtoAdapter connectionModelDtoAdapter = new ConnectionModelDtoAdapter();

    @Override
	public O adapt(I segment) {
		O segmentDTO = (O) new BaseSegmentDTOImpl();
		setDtoValues(segmentDTO, segment);
		return segmentDTO;
	}
	
	@Override
	public I adaptReverse(O dto) {
		I segment = (I) new BaseSegment();
		setModelValues(segment,dto);
		return segment;
	}

	protected void setModelValues(I segment, O segmentDTO) {
		segment.setId(segmentDTO.getId());
        segment.setCons(this.connectionModelDtoAdapter.adaptReverse(segmentDTO.getConnection(), segmentDTO.getId()));
        List<ISegmentXInfo> xinfos = this.segmentXInfoMapAdapter.adaptReverse(segmentDTO.getxInfo()); 
        if(xinfos != null) {
        	xinfos.stream().forEach(x -> x.setSegmentId(segment.getId()));
        	segment.setXInfo(xinfos);
        }
	}

	protected void setDtoValues(O segmentDTO, I segment)  {
		segmentDTO.setId(segment.getId());
        segmentDTO.setxInfo(segmentXInfoMapAdapter.adapt(segment.getXInfo()));
		segmentDTO.setConnection(connectionModelDtoAdapter.adapt(segment.getCons()));
	}


    @Override
    protected IWaySegmentConnection adaptConnectionToModel(IBaseSegmentConnectionDTO conn, long fromSegmentId) {
        Map<String,List<IConnectionXInfoDTO>> xInfoDtos = conn.getxInfo();
        Map<String,List<IConnectionXInfo>> connectionXInfos = this.adaptConnXinfoToModel(xInfoDtos);
        Set<Access> accessSet = null;
        if (conn instanceof ISegmentConnectionDTO) {
            accessSet = ((ISegmentConnectionDTO) conn).getAccess();
        }
        return new WaySegmentConnection(conn.getNodeId(),fromSegmentId,conn.getToSegmentId(),accessSet,connectionXInfos);
    }

    /**
	 * @param conn
	 * @return
	 */
    protected IBaseSegmentConnectionDTO adaptConnectionToDTO(IWaySegmentConnection conn) {
        Map<String, List<IConnectionXInfoDTO>> connectionXInfoDTOMap = null;
        try {
            connectionXInfoDTOMap = adaptConnXinfoToDTO(conn.getXInfo());
        } catch (XInfoNotSupportedException e) {
            log.error("adaption of connection's xinfo failed", e);
        }
        return new BaseSegmentConnectionDTO(conn.getNodeId(), conn.getToSegmentId(), connectionXInfoDTOMap, conn.getTags());
    }

    /**
     *
     */
    private class ConnectionModelDtoAdapter implements IBidrectionalAdapter<List<IBaseSegmentConnectionDTO>,List<IWaySegmentConnection>> {

        @Override
        public List<IBaseSegmentConnectionDTO> adapt(List<IWaySegmentConnection> conns) {
            List<IBaseSegmentConnectionDTO> connections = new ArrayList<>();
            if (conns != null && !conns.isEmpty()) {
                connections.addAll(conns.stream().map(BaseSegment2SegmentDTOAdapter.this::adaptConnectionToDTO).collect(Collectors.toList()));
            }
            return connections;
        }

        public List<IWaySegmentConnection> adaptReverse(List<IBaseSegmentConnectionDTO> connDtos, long fromSegmentId) {
            List<IWaySegmentConnection> connections = new ArrayList<>();
            if (connDtos != null && !connDtos.isEmpty()) {
                connections.addAll(connDtos.stream().map(dto -> adaptConnectionToModel(dto, fromSegmentId)).collect(Collectors.toList()));
            }
            return connections;
        }

        @Override
        public List<IWaySegmentConnection> adaptReverse(List<IBaseSegmentConnectionDTO> connDtos) {
            //TODO not needed (implemented) maybe throw an exception
            return null;
        }
    }

    /**
     *
     */
    private class SegmentXInfoMapAdapter implements IBidrectionalAdapter<Map<String,List<ISegmentXInfoDTO>>,List<ISegmentXInfo>> {

        @Override
        public Map<String, List<ISegmentXInfoDTO>> adapt(List<ISegmentXInfo> xInfoList) {
            Map<String, List<ISegmentXInfoDTO>> resultMap = null;
            try {
                if (xInfoList != null && !xInfoList.isEmpty()) {
                    resultMap = new HashMap<>();
                    for (ISegmentXInfo xInfo : xInfoList) {
                        String type = xInfo.getXInfoType();
                        List<ISegmentXInfoDTO> resultList = resultMap.get(type);
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                            resultMap.put(type,resultList);
                        }
                        resultList.add(getSegmentXInfoAdapterRegistry().getObjectForType(type).adaptReverse(xInfo));
                    }
                }
            } catch (XInfoNotSupportedException e) {
                log.error("segment's xinfo could not be found", e);
            }
            return resultMap;
        }

        @Override
        public List<ISegmentXInfo> adaptReverse(Map<String, List<ISegmentXInfoDTO>> xInfoDTOMap) {
            List<ISegmentXInfo> resultList = null;
            try {
                if (xInfoDTOMap != null && !xInfoDTOMap.isEmpty()) {
                    resultList = new ArrayList<>();
                    for (String key : xInfoDTOMap.keySet()) {
                        List<ISegmentXInfoDTO> dtoList = xInfoDTOMap.get(key);
                        if (dtoList != null && !dtoList.isEmpty()) {
                            for (ISegmentXInfoDTO dto : dtoList) {
                                resultList.add(getSegmentXInfoAdapterRegistry().getObjectForType(key).adapt(dto));
                            }
                        }
                    }
                }
            } catch (XInfoNotSupportedException e) {
                log.error("segment's xinfo could not be found", e);
            }
            return resultList;
        }
    }
}