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
package at.srfg.graphium.io.dto.impl;

import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Created by shennebe on 27.09.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseSegmentConnectionDTO implements IBaseSegmentConnectionDTO {


	public BaseSegmentConnectionDTO() {}

    public BaseSegmentConnectionDTO(long nodeId, long toSegmentId, Map<String, List<IConnectionXInfoDTO>> xInfo) {
        this.nodeId = nodeId;
        this.toSegmentId = toSegmentId;
        this.xInfo = xInfo;
    }

    private long nodeId;
    private long toSegmentId;
    private Map<String,List<IConnectionXInfoDTO>> xInfo;

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public long getToSegmentId() {
        return toSegmentId;
    }

    @Override
    public void setToSegmentId(long toSegmentId) {
        this.toSegmentId = toSegmentId;
    }

    @Override
    public Map<String, List<IConnectionXInfoDTO>> getxInfo() {
        return xInfo;
    }

    @Override
    public void setxInfo(Map<String, List<IConnectionXInfoDTO>> xInfo) {
        this.xInfo = xInfo;
    }
}
