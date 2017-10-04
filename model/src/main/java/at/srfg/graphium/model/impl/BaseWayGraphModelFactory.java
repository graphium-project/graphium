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
package at.srfg.graphium.model.impl;

import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

public class BaseWayGraphModelFactory extends AbstractWayGraphModelFactory<IBaseWaySegment> {

	@Override
	public IBaseWaySegment newSegment() {
		return new BaseWaySegment();
	}

	@Override
	public IBaseWaySegment newSegment(long id,
			Map<String, List<ISegmentXInfo>> xInfo) {
		IBaseWaySegment segment = new BaseWaySegment();
		segment.setId(id);
		segment.setXInfo(mergeXInfoList(xInfo));
		return segment;		
	}
	
	@Override
	public IBaseWaySegment newSegment(long id, Map<String, List<ISegmentXInfo>> xInfo,
			List<IWaySegmentConnection> connections) {
		IBaseWaySegment segment = new BaseWaySegment();
		segment.setId(id);
		segment.setXInfo(mergeXInfoList(xInfo));
		segment.setCons(connections);
		return segment;		
	}

	@Override
	public IBaseWaySegment newSegment(long id, LineString geometry, float length,
			String name, String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections) {
		return new BaseWaySegment(id, geometry, length, name, streetType, wayId, startNodeId, startNodeIndex, 
				endNodeId, endNodeIndex, connections, null, null);
	}
	
	@Override
	public IBaseWaySegment newSegment(long id, LineString geometry, float length,
			String name, String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections, Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo) {
		return new BaseWaySegment(id, geometry, length, name, streetType, wayId, startNodeId, startNodeIndex, 
				endNodeId, endNodeIndex, connections, tags, xInfo);
	}
}
