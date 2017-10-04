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

import at.srfg.graphium.io.dto.impl.DefaultSegmentXInfoDTO;
import at.srfg.graphium.model.impl.DefaultSegmentXInfo;

import java.util.HashMap;

/**
 * Example implementation for the default adapter converting a default XInfo to a DTO and vice versa.
 *
 * Created by shennebe on 23.09.2016.
 */
public class DefaultSegmentXInfoAdapter extends AbstractXInfoDTOAdapter<DefaultSegmentXInfo,DefaultSegmentXInfoDTO> {

    public DefaultSegmentXInfoAdapter() {
        super(new DefaultSegmentXInfo(), new DefaultSegmentXInfoDTO());
    }

    @Override
    public DefaultSegmentXInfo adapt(DefaultSegmentXInfoDTO objectToAdapt) {
        DefaultSegmentXInfo segmentXInfo = new DefaultSegmentXInfo();
        segmentXInfo.setDirectionTow(objectToAdapt.getDirectionTow());
        if (objectToAdapt.getValues() != null && !objectToAdapt.getValues().isEmpty()) {
            segmentXInfo.setValues(new HashMap<>());
            for (String key : objectToAdapt.getValues().keySet()) {
                segmentXInfo.getValues().put(key,objectToAdapt.getValues().get(key));
            }
        }
        return segmentXInfo;
    }

    @Override
    public DefaultSegmentXInfoDTO adaptReverse(DefaultSegmentXInfo xInfo) {
        DefaultSegmentXInfoDTO dto = new DefaultSegmentXInfoDTO();
        dto.setDirectionTow(xInfo.isDirectionTow());
        if (xInfo.getValues() != null && !xInfo.getValues().isEmpty()) {
            xInfo.getValues().forEach(dto::setValue);
        }
        return dto;
    }
}
