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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import javafx.util.Pair;

/**
 * Created by shennebe on 10.02.2017.
 */
public class SegmentXInfoToCSVConverterServiceImpl extends XInfoToCSVConverterServiceImpl<ISegmentXInfoDTO,ISegmentXInfo> {

    @Override
    List<ISegmentXInfo> getXInfosFromSegment(String key, IBaseSegment segment) {
        return segment.getXInfo(key);
    }

    @Override
    Map<Pair[], Map<String, List<ISegmentXInfoDTO>>> getXInfosFromSegmentDTO(IBaseSegmentDTO segmentDTO) {
        Map<Pair[],Map<String,List<ISegmentXInfoDTO>>> resultMap = new HashMap<>();
        resultMap.put(new Pair[]{new Pair<>("segmentId",segmentDTO.getId())},segmentDTO.getxInfo());
        return resultMap;
    }
}
