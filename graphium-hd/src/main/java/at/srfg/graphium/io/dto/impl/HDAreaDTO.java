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

import at.srfg.graphium.io.inputformat.impl.jackson.JacksonLineStringDeserializer;
import at.srfg.graphium.io.outputformat.impl.jackson.JacksonGeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.io.dto.IHDAreaDTO;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDAreaDTO extends BaseSegmentDTOImpl implements IHDAreaDTO {

	private Geometry areaGeometry;
	private String type;
	protected Map<String, String> tags;

	public HDAreaDTO() {}
	
	public HDAreaDTO(Geometry areaGeometry) {
		super();
		this.areaGeometry = areaGeometry;
	}

	@Override
	@JsonSerialize(using = JacksonGeometrySerializer.class)
	@JsonDeserialize(using = JacksonLineStringDeserializer.class)
	public Geometry getAreaGeometry() {
		return areaGeometry;
	}

	@Override
	public void setAreaGeometry(Geometry area) {
		areaGeometry = area;
	}

	@Override
	public Map<String, String> getTags() {
		return tags;
	}

	@Override
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}
}
