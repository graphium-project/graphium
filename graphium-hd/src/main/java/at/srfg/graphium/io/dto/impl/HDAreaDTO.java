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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.io.dto.IHDAreaDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDAreaDTO extends WaySegmentDTOImpl implements IHDAreaDTO {

	private Geometry areaGeometry;
	
	public HDAreaDTO() {}
	
	public HDAreaDTO(Geometry areaGeometry) {
		super();
		this.areaGeometry = areaGeometry;
	}

	@Override
	public Geometry getAreaGeometry() {
		return areaGeometry;
	}

	@Override
	public void setAreaGeometry(Geometry area) {
		areaGeometry = area;
	}
	
}
