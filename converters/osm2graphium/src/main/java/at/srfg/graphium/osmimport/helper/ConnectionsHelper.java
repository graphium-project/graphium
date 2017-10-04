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
package at.srfg.graphium.osmimport.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.WaySegmentConnection;

/**
 * @author mwimmer
 *
 */
public class ConnectionsHelper {
	
	private static Logger log = LoggerFactory.getLogger(ConnectionsHelper.class);

	public static void addConnectionToSegment(IWaySegment segment, IWaySegmentConnection connection, Long nodeId) {
		if (nodeId == segment.getStartNodeId()) {
			if (segment.getStartNodeCons() == null) {
				segment.setStartNodeCons(new ArrayList<>());
			}
			segment.getCons().add(connection);
		} else {
			if (segment.getEndNodeCons() == null) {
				segment.setEndNodeCons(new ArrayList<>());
			}
			segment.getCons().add(connection);
		}
	}

	public static IWaySegmentConnection createConnection(Long segmentFromId, Long segmentToId, Long nodeId, Set<Access> accesses) {
		return new WaySegmentConnection(nodeId, segmentFromId, segmentToId, accesses);
	}
	
	public static void ensureUniqueConnections(IWaySegment segment) {
		if (segment != null) {
			if (segment.getStartNodeCons() != null) {
				Set<IWaySegmentConnection> connSet = new HashSet<>();
				for (IWaySegmentConnection conn : segment.getStartNodeCons()) {
					connSet.add(conn);
				}
				if (segment.getStartNodeCons().size() != connSet.size()) {
					log.warn("Found duplicate start node connections for way " + segment.getId());
					segment.setStartNodeCons(new ArrayList<>(connSet));
				}
			}
			if (segment.getEndNodeCons() != null) {
				Set<IWaySegmentConnection> connSet = new HashSet<>();
				for (IWaySegmentConnection conn : segment.getEndNodeCons()) {
					connSet.add(conn);
				}
				if (segment.getEndNodeCons().size() != connSet.size()) {
					log.warn("Found duplicate end node connections for way " + segment.getId());
					segment.setEndNodeCons(new ArrayList<>(connSet));
				}
			}
		}
	}

}