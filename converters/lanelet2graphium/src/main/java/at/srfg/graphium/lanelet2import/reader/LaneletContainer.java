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
package at.srfg.graphium.lanelet2import.reader;

import java.util.HashSet;
import java.util.Set;

import at.srfg.graphium.model.IHDWaySegment;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletContainer {

	private TLongObjectMap<Set<IHDWaySegment>> laneletNodeIds = new TLongObjectHashMap<>();
	
	public void addLanelet(IHDWaySegment lanelet) {
		putLanelet(lanelet.getLeftBoarderStartNodeId(), lanelet);
		putLanelet(lanelet.getLeftBoarderEndNodeId(), lanelet);
		putLanelet(lanelet.getRightBoarderStartNodeId(), lanelet);
		putLanelet(lanelet.getRightBoarderEndNodeId(), lanelet);
	}
	
	private void putLanelet(Long nodeId, IHDWaySegment lanelet) {
		if (!laneletNodeIds.containsKey(nodeId)) {
			laneletNodeIds.put(nodeId, new HashSet<>());
		}
		laneletNodeIds.get(nodeId).add(lanelet);
	}

	public Set<IHDWaySegment> getNeighbours(IHDWaySegment lanelet) {
		Set<IHDWaySegment> neighbours = new HashSet<>();
		if (laneletNodeIds.containsKey(lanelet.getLeftBoarderStartNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getLeftBoarderStartNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getLeftBoarderEndNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getLeftBoarderEndNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getRightBoarderStartNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getRightBoarderStartNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getRightBoarderEndNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getRightBoarderEndNodeId()));
		}
		return neighbours;
	}
	
}