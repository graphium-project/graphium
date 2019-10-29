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
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.dto.ISegmentConnectionDTO;
import at.srfg.graphium.model.Access;

/**
 * @author mwimmer
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SegmentConnectionDTOImpl extends BaseSegmentConnectionDTO implements ISegmentConnectionDTO {

	private Set<Access> access;
	
	public SegmentConnectionDTOImpl() {}
	
	public SegmentConnectionDTOImpl(long nodeId,
									long toSegmentId,
									Map<String, List<IConnectionXInfoDTO>> xInfo,
									Set<Access> access,
									Map<String, String> tags) {
		super(nodeId,toSegmentId,xInfo, tags);
		this.access = access;
	}

	public Set<Access> getAccess() {
		return access;
	}
	public void setAccess(Set<Access> access) {
		this.access = access;
	}
	
}