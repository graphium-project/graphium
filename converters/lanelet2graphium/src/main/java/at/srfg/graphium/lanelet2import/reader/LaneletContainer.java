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

import at.srfg.graphium.model.hd.IHDWaySegment;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class LaneletContainer {

	private TLongObjectMap<Set<IHDWaySegment>> laneletNodeIds = new TLongObjectHashMap<>();
	
	public void addLanelet(IHDWaySegment lanelet) {
		putLanelet(lanelet.getLeftBorderStartNodeId(), lanelet);
		putLanelet(lanelet.getLeftBorderEndNodeId(), lanelet);
		putLanelet(lanelet.getRightBorderStartNodeId(), lanelet);
		putLanelet(lanelet.getRightBorderEndNodeId(), lanelet);
	}
	
	private void putLanelet(Long nodeId, IHDWaySegment lanelet) {
		if (!laneletNodeIds.containsKey(nodeId)) {
			laneletNodeIds.put(nodeId, new HashSet<>());
		}
		laneletNodeIds.get(nodeId).add(lanelet);
	}

	public Set<IHDWaySegment> getNeighbours(IHDWaySegment lanelet) {
		Set<IHDWaySegment> neighbours = new HashSet<>();
		if (laneletNodeIds.containsKey(lanelet.getLeftBorderStartNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getLeftBorderStartNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getLeftBorderEndNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getLeftBorderEndNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getRightBorderStartNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getRightBorderStartNodeId()));
		}
		if (laneletNodeIds.containsKey(lanelet.getRightBorderEndNodeId())) { 
			neighbours.addAll(laneletNodeIds.get(lanelet.getRightBorderEndNodeId()));
		}
		return neighbours;
	}
	
}