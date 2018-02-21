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
package at.srfg.graphium.osmimport.connections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.osmimport.helper.ConnectionsHelper;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class ConnectionsBuilder {
	
	public void createConnections(IWaySegment segmentFrom, List<IWaySegment> segmentsTo, Long viaNodeId, Set<Access> defaultAccesses, 
			TLongObjectHashMap<List<Relation>> wayRelations) {

		if ((segmentFrom.getStartNodeId() == viaNodeId && segmentFrom.getAccessBkw() == null) ||
			(segmentFrom.getEndNodeId()   == viaNodeId && segmentFrom.getAccessTow() == null)) {
			// oneway
			return;
		}
		
		// select correct IDs (original wayIDs)
		long fromId = ((segmentFrom.getWayId() > 0 && segmentFrom.getId() != segmentFrom.getWayId()) ? segmentFrom.getWayId() : segmentFrom.getId());
		
		// check if restrictions for segmentFromId exist
		if (wayRelations.containsKey(fromId)) {
			List<Relation> relations = wayRelations.get(fromId);
			
			Long onlyToSegmentId = null;
			Set<Long> notToSegmentIds = new HashSet<>();
			
			for (Relation rel : relations) {
				for (Tag tag : rel.getTags()) {
					if (tag.getKey().equals("restriction")) {
						
						long toWayId = 0;
						long nodeId = 0;
						for (RelationMember member : rel.getMembers()) {
							if (member.getMemberRole().equals("to")) {
								toWayId = member.getMemberId();
							}
							if (member.getMemberRole().equals("via")) {
								nodeId = member.getMemberId();
							}
						}
						
						if (nodeId == 0 || nodeId != viaNodeId) {
							// restriction relates to other direction
							continue;
						}

						if (tag.getValue().startsWith("only_")) {
							onlyToSegmentId = toWayId;
						}
						if (tag.getValue().startsWith("no_")) {
							notToSegmentIds.add(toWayId);
						}
					}
				}
			}
			
			for (IWaySegment segmentTo : segmentsTo) {
				if (onlyToSegmentId != null) {
					if (segmentTo.getId() == onlyToSegmentId || segmentTo.getWayId() == onlyToSegmentId) {
						createConnection(segmentFrom, segmentTo, viaNodeId, defaultAccesses);
					}
				} else if (notToSegmentIds.contains(segmentTo.getId())) {
					// no connection has to be built
				} else {
					createConnection(segmentFrom, segmentTo, viaNodeId, defaultAccesses);
				}
			}

		} else {
			
			for (IWaySegment segmentTo : segmentsTo) {
				createConnection(segmentFrom, segmentTo, viaNodeId, defaultAccesses);
			}
			
		}
		
		// remove possibly duplicate connections
		ConnectionsHelper.ensureUniqueConnections(segmentFrom);
		
	}

	/**
	 * @param segmentFrom
	 * @param segmentTo
	 * @param viaNodeId
	 * @param defaultAccesses
	 */
	private void createConnection(IWaySegment segmentFrom, IWaySegment segmentTo, Long viaNodeId,
			Set<Access> defaultAccesses) {
		if ((segmentTo.getStartNodeId() == viaNodeId && segmentTo.getAccessTow() != null) ||	// check if segmentTo is oneway
			(segmentTo.getEndNodeId()   == viaNodeId && segmentTo.getAccessBkw() != null)) {
			ConnectionsHelper.addConnectionToSegment(segmentFrom, 
					ConnectionsHelper.createConnection(segmentFrom.getId(), segmentTo.getId(), viaNodeId, defaultAccesses), 
					viaNodeId);
		}
	}
	
}