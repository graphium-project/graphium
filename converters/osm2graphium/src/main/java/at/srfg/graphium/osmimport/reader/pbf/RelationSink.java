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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.osmimport.reader.pbf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 */
public class RelationSink implements Sink {

	private TLongObjectHashMap<List<Relation>> wayRelations = new TLongObjectHashMap<>();
	private Set<Long> wayIds = new HashSet<>();
	
	public RelationSink(TLongObjectHashMap<List<WayRef>> wayRefs) {
		for (List<WayRef> wayRefList : wayRefs.valueCollection()) {
			for (WayRef wayRef : wayRefList) {
				wayIds.add(wayRef.getWayId());
			}
		}
	}
	
	@Override
	public void initialize(Map<String, Object> metaData) {
	}

	@Override
	public void complete() {
	}

	@Override
	public void release() {
	}

	@Override
	public void process(EntityContainer entityContainer) {
		Entity entity = entityContainer.getEntity();
		if (entity instanceof Relation) {
			Relation relation = (Relation) entity;
			Long wayFromId = getWayIdIfValid(relation);
			if (wayFromId != null) {
				if (!wayRelations.containsKey(wayFromId)) {
					wayRelations.put(wayFromId, new ArrayList<>());
				}
				wayRelations.get(wayFromId).add(relation);
			}
		}
	}

	private Long getWayIdIfValid(Relation relation) {
		boolean viaNodeFound = false;
		Long fromWayId = null;
		boolean toWayFound = false;
		for (RelationMember member : relation.getMembers()) {
			if (member.getMemberType().equals(EntityType.Way)) {
				if (wayIds.contains(member.getMemberId())) {
					if (member.getMemberRole().equals("from")) {
						fromWayId = member.getMemberId();
					} else if (member.getMemberRole().equals("to")) {
						toWayFound = true;
					}
				}
			} else if (member.getMemberType().equals(EntityType.Node)) {
				if (member.getMemberRole().equals("via")) {
					viaNodeFound = true;
				}
			}
		}

		if (fromWayId != null &&
			toWayFound &&
			viaNodeFound) {
			return fromWayId;
		} else {
			return null;
		}
	}

	public TLongObjectHashMap<List<Relation>> getWayRelations() {
		return wayRelations;
	}

}