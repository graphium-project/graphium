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
package at.srfg.graphium.io.converter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

/**
 * Created by shennebe on 10.02.2017.
 */
public class ConnectionXInfoToCSVConverterServiceImpl extends XInfoToCSVConverterServiceImpl<IConnectionXInfoDTO,IConnectionXInfo> {

    @Override
    List<IConnectionXInfo> getXInfosFromSegment(String key, IBaseSegment segment) {
        List<IConnectionXInfo> xInfos = new ArrayList<>();
        List<IWaySegmentConnection> connections = segment.getCons();
        if (connections != null && !connections.isEmpty()) {
            for (IWaySegmentConnection connection : connections) {
                xInfos.addAll(connection.getXInfo(key));
            }
        }
        return xInfos;
    }



    @Override
    Map<Pair<String, Object>[], Map<String, List<IConnectionXInfoDTO>>> getXInfosFromSegmentDTO(IBaseSegmentDTO segmentDTO) {
        List<IBaseSegmentConnectionDTO> connectionDTOS = segmentDTO.getConnection();
        Map<Pair<String, Object> [], Map<String, List<IConnectionXInfoDTO>>> resultMap = new HashMap<>();
        if (connectionDTOS != null && !connectionDTOS.isEmpty()) {
            for (IBaseSegmentConnectionDTO connectionDTO : connectionDTOS) {
                Map<String,List<IConnectionXInfoDTO>> connectionXInfoMap = connectionDTO.getxInfo();
                Pair<String, Object>[] additionalFields = new Pair[3];
                additionalFields[0] =  Pair.of("node_id",connectionDTO.getNodeId());
                additionalFields[1] = Pair.of("from_segment",segmentDTO.getId());
                additionalFields[2] = Pair.of("to_segment",connectionDTO.getToSegmentId());
                resultMap.put(additionalFields,connectionXInfoMap);
            }
        }
        return resultMap;
    }
}
