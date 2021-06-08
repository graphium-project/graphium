/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.io.dto.IHDRegulatoryElementDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDRegulatoryElementDTO extends AbstractSegmentXInfoDTO implements IHDRegulatoryElementDTO {
	
	private long id;
    private Long graphVersionId;
	// TODO: kann hier der groupKey verwendet werden???
    private String type;
	private Geometry geometry;
	private Map<String, String> tags;
	
	public HDRegulatoryElementDTO() {
	}

	public HDRegulatoryElementDTO(long id, Long graphVersionId, String type,
			Geometry geometry, Map<String, String> tags) {
		this.id = id;
		this.graphVersionId = graphVersionId;
		this.type = type;
		this.geometry = geometry;
		this.tags = tags;
	}

	@Override
	public String getType() {
		return type;
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
	public Long getGraphVersionId() {
		return graphVersionId;
	}

	@Override
	public void setGraphVersionId(Long graphVersionId) {
		this.graphVersionId = graphVersionId;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
//    @JsonAnyGetter
	public Map<String, String> getTags() {
		return tags;
	}

	@Override
//    @JsonAnySetter
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
}
