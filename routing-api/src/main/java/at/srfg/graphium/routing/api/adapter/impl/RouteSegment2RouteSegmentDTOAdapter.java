/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.api.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.dto.impl.RouteSegmentDtoImpl;

/**
 * @author mwimmer
 *
 */
public class RouteSegment2RouteSegmentDTOAdapter<T extends IWaySegment> implements IAdapter<List<RouteSegmentDtoImpl>, List<T>> {

	@Override
	public List<RouteSegmentDtoImpl> adapt(List<T> segments) {
		List<RouteSegmentDtoImpl> segmentDtos = null;
		if (segments != null) {
			segmentDtos = new ArrayList<>();
			T prevSegment = null;
			for (T segment : segments) {
				int duration;
				short maxSpeed;
				if (prevSegment == null) {
					if (segments.get(1) == null || 
						segment.getEndNodeId() == segments.get(1).getStartNodeId() ||
						segment.getEndNodeId() == segments.get(1).getEndNodeId()) {
						duration = segment.getDuration(true);
						maxSpeed = segment.getMaxSpeedTow();
					} else {
						duration = segment.getDuration(false);
						maxSpeed = segment.getMaxSpeedBkw();
					}
				} else {
					if (segment.getStartNodeId() == prevSegment.getStartNodeId() ||
						segment.getStartNodeId() == prevSegment.getEndNodeId()) {
						duration = segment.getDuration(true);
						maxSpeed = segment.getMaxSpeedTow();
					} else {
						duration = segment.getDuration(false);
						maxSpeed = segment.getMaxSpeedBkw();
					}
				}
				segmentDtos.add(new RouteSegmentDtoImpl(
					segment.getId(), 
					segment.getGeometry().toText(), 
					segment.getLength(), 
					duration, 
					maxSpeed));
			}
		}
		return segmentDtos;
	}

}
