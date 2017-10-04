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
package at.srfg.graphium.io.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by shennebe on 27.09.2016.
 */
public interface IBaseSegmentDTO {
	
    long getId();

    void setId(long id);

    Map<String, List<ISegmentXInfoDTO>> getxInfo();

    void setxInfo(Map<String, List<ISegmentXInfoDTO>> xInfo);

    List<IBaseSegmentConnectionDTO> getConnection();

    void setConnection(List<IBaseSegmentConnectionDTO> connection);
    
    String getSegmentType();
}
