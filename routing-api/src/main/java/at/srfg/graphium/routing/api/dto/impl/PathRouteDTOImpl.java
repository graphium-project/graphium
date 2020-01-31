/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.routing.api.dto.impl;

import java.util.List;

import at.srfg.graphium.routing.api.dto.IDirectedSegmentDTO;

public class PathRouteDTOImpl extends OverviewRouteDTOImpl {
	
	private List<IDirectedSegmentDTO> segments;

	public PathRouteDTOImpl(Float weight, float length, int duration, int runtimeInMs, String graphName,
			String graphVersion, String geometry, List<IDirectedSegmentDTO> segments) {
		super(weight, length, duration, runtimeInMs, graphName, graphVersion, geometry);
		this.segments = segments;
	}

	public List<IDirectedSegmentDTO> getSegments() {
		return segments;
	}

	public void setSegments(List<IDirectedSegmentDTO> segments) {
		this.segments = segments;
	}
}
