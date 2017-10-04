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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.LineString;

/**
 * factory for production of Graph Model objects (Pojos)
 *
 * @author <a href="mailto:andreas.wagner@salzburgresearch.at">Andreas Wagner</a>
 *
 */
public interface IWayGraphModelFactory<T extends IWaySegment> 
		extends IBaseWayGraphModelFactory<T> {
	
	/**
	 * 
	 * @param id
	 * @param geometry
	 * @param length
	 * @param name
	 * @param maxSpeedTow
	 * @param maxSpeedBkw
	 * @param speedCalcTow
	 * @param speedCalcBkw
	 * @param lanes
	 * @param frc
	 * @param formOfWay
	 * @param streetType
	 * @param wayId
	 * @param startNodeId
	 * @param startNodeIndex
	 * @param endNodeId
	 * @param endNodeIndex
	 * @param accessTow
	 * @param accessBkw
	 * @param tunnel
	 * @param bridge
	 * @param timestamp
	 * @param startNodeCons
	 * @param endNodeCons
	 * @return
	 */
	T newSegment(long id, LineString geometry, float length, String name,
			short maxSpeedTow, short maxSpeedBkw, Short speedCalcTow,
			Short speedCalcBkw, short lanesTow, short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay,
			String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			Set<Access> accessTow, Set<Access> accessBkw, Boolean tunnel, Boolean bridge, Boolean urban,
			Date timestamp, List<IWaySegmentConnection> startNodeCons,
			List<IWaySegmentConnection> endNodeCons);

	/**
	 *
	 * @param id
	 * @param geometry
	 * @param length
	 * @param name
	 * @param maxSpeedTow
	 * @param maxSpeedBkw
	 * @param speedCalcTow
	 * @param speedCalcBkw
	 * @param lanesTow
	 * @param lanesBkw
	 * @param frc
	 * @param formOfWay
	 * @param streetType
	 * @param wayId
	 * @param startNodeId
	 * @param startNodeIndex
	 * @param endNodeId
	 * @param endNodeIndex
	 * @param accessTow
	 * @param accessBkw
	 * @param tunnel
	 * @param bridge
	 * @param urban
	 * @param timestamp
	 * @param startNodeCons
	 * @param endNodeCons
	 * @param tags
	 * @param xInfo
	 * @return
	 */
    T newSegment(long id, LineString geometry,
                           float length, String name, short maxSpeedTow, short maxSpeedBkw,
                           Short speedCalcTow, Short speedCalcBkw, short lanesTow,
                           short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay, String streetType,
                           long wayId, long startNodeId, int startNodeIndex, long endNodeId,
                           int endNodeIndex, Set<Access> accessTow, Set<Access> accessBkw,
                           Boolean tunnel, Boolean bridge, Boolean urban, Date timestamp,
                           List<IWaySegmentConnection> startNodeCons,
                           List<IWaySegmentConnection> endNodeCons,
                           Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo);

}
