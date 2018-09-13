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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;

/**
 * Created by shennebe on 27.09.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseSegmentDTOImpl implements IBaseSegmentDTO {

	protected long id;
	protected Map<String,List<ISegmentXInfoDTO>> xInfo;
	protected List<IBaseSegmentConnectionDTO> connection;
    
    public BaseSegmentDTOImpl() {}

    public BaseSegmentDTOImpl(long id, List<IBaseSegmentConnectionDTO> connection,  Map<String,List<ISegmentXInfoDTO>> xInfo) {
        this.id = id;
        this.xInfo = xInfo;
        this.connection = connection;
    }
   

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Map<String, List<ISegmentXInfoDTO>> getxInfo() {
        return xInfo;
    }

    @Override
    public void setxInfo(Map<String, List<ISegmentXInfoDTO>> xInfo) {
        this.xInfo = xInfo;
    }

    @Override
	@JsonDeserialize(contentAs = BaseSegmentConnectionDTO.class)
    public List<IBaseSegmentConnectionDTO> getConnection() {
        return connection;
    }

    @Override
    public void setConnection(List<IBaseSegmentConnectionDTO> connection) {
        this.connection = connection;
    }
    @JsonIgnore
	@Override
	public String getSegmentType() {
		String className = this.getClass().getSimpleName();
		className = className.replaceAll("DTO","");
		className = className.replaceAll("Impl", "");
		return className.toLowerCase();
	}
}
