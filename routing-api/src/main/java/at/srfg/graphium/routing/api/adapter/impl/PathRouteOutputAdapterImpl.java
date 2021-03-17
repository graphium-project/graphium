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
package at.srfg.graphium.routing.api.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapter;
import at.srfg.graphium.routing.api.dto.IDirectedSegmentDTO;
import at.srfg.graphium.routing.api.dto.impl.DirectedSegmentDTOImpl;
import at.srfg.graphium.routing.api.dto.impl.PathRouteDTOImpl;
import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IRoute;

public class PathRouteOutputAdapterImpl<T extends IBaseWaySegment>
	implements IRouteOutputAdapter<PathRouteDTOImpl, Float, T> {

	private IRouteOutput<PathRouteDTOImpl, Float> output;
	
	public PathRouteOutputAdapterImpl() {
		this.output = new PathRouteOutputImpl();
	}
	
	@Override
	public IRouteOutput<PathRouteDTOImpl, Float> adaptsTo() {
		return output;
	}

	@Override
	public PathRouteDTOImpl adapt(IRoute<T, Float> route) {
		String geometryWkt = null;
		if (route.getGeometry() != null) {
			geometryWkt = route.getGeometry().toText();
		}
		List<DirectedSegmentDTOImpl> segments = new ArrayList<DirectedSegmentDTOImpl>();
		if (route.getPath() != null) {
			for (IDirectedSegment segment : route.getPath()) {
				segments.add(new DirectedSegmentDTOImpl(segment.getId(), segment.isTowards()));
			}
		}
		
		return new PathRouteDTOImpl(route.getWeight(), route.getLength(), route.getDuration(), 
				route.getRuntimeInMs(), route.getGraphName(), route.getGraphVersion(), geometryWkt, segments);
	}
}
