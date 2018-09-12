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
package at.srfg.graphium.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.LineString;

public interface IBaseWayGraphModelFactory<T extends IBaseWaySegment> 
	extends IBaseGraphModelFactory<T>, IWayGraphMetadataFactory {
	
	public T newSegment(long id, LineString geometry, float length, String name, String streetType, long wayId,
			long startNodeId, int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections);
	
	public T newSegment(long id, LineString geometry, float length, String name, String streetType, long wayId,
			long startNodeId, int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections,
			Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo);
	
	/**
	 * create new empty way segment connection
	 * @return empty way segment connection object
	 */
	public IWaySegmentConnection newWaySegmentConnection();
	
	public IWaySegmentConnection newWaySegmentConnection(long nodeId, long fromSegmentId,
			long toSegmentId, Set<Access> access);
	
	
}
