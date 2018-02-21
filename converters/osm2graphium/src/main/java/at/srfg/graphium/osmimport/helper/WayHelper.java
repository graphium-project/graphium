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
package at.srfg.graphium.osmimport.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.osmimport.reader.pbf.WayRef;

/**
 * @author mwimmer
 */
public class WayHelper {
	
	private static Logger log = LoggerFactory.getLogger(WayHelper.class);

	public static Way preprocessWay(Way way) {
		//way.getWayNodes().removeIf(arg0)
		Iterator<WayNode> it = way.getWayNodes().iterator();
		WayNode predNode = null;
		WayNode succNode = null;
		while (it.hasNext()) {
			succNode = it.next();
			if (predNode != null && predNode.getNodeId() == succNode.getNodeId()) {
				it.remove();
			} else {
				predNode = succNode;
			}
		}
		if (way.getWayNodes().size() < 2) {
			if (log.isDebugEnabled()) {
				log.debug("Way " + way.getId() + " has " + way.getWayNodes().size() + " nodes (after removing duplicate nodes) and will be ignored");
			}
			way = null;
		}
		return way;
	}
	
	public static byte checkOneway(Map<String, String> tags) {
		byte oneway = 0;
		if (!(tags.containsKey("oneway") && tags.get("oneway").equals("no"))) {
			if (tags.containsKey("oneway")) {
				if (tags.get("oneway").equals("yes")) {
					oneway = 1;
				} else if (tags.get("oneway").equals("-1") || tags.get("oneway").equals("reverse")) {
					oneway = 2;
				}
			}
			if (tags.containsKey("junction") && tags.get("junction").equals("roundabout")) {
				oneway = 1;
			}
			if (tags.containsKey("highway") && tags.get("highway").equals("motorway")) {
				oneway = 1;
			}
		}
		return oneway;
	}
	
	public static Map<String, String> createTagMap(Way way) {
		Map<String, String> tagMap = new HashMap<>();
		Collection<Tag> tags = way.getTags();
		for (Tag tag : tags) {
			tagMap.put(tag.getKey(), tag.getValue());
		}
		return tagMap;
	}

	public static IWaySegment createDummyWaySegment(WayRef ref, Set<Access> defaultAccesses) {
		IWaySegment dummySegment = new WaySegment();
		dummySegment.setId(ref.getWayId());
		dummySegment.setStartNodeId(ref.getStartNodeId());
		dummySegment.setEndNodeId(ref.getEndNodeId());
		if (ref.getOneway() == 0 || ref.getOneway() == 1) {
			dummySegment.setAccessTow(defaultAccesses);
		}
		if (ref.getOneway() == 0 || ref.getOneway() == 2) {
			dummySegment.setAccessBkw(defaultAccesses);
		}
		return dummySegment;
	}
	
}
