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
package at.srfg.graphium.osmimport.segmentation;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

/**
 * @author mwimmer
 *
 */
public class WaySegmenter {
	
	public static final String ORIG_WAY_ID = "origWayId";

	public Way segment(Way originalWay, int startNodeIndex, int endNodeIndex) {
		long newWayId = Long.MAX_VALUE - (originalWay.getId() * 1000000 + startNodeIndex * 1000 + endNodeIndex);
		Way way = new Way(
					new CommonEntityData(newWayId, 
										 originalWay.getVersion(), 
										 originalWay.getTimestamp(), 
										 originalWay.getUser(), 
										 originalWay.getChangesetId()));
		way.getTags().addAll(originalWay.getTags());
		way.getTags().add(new Tag(ORIG_WAY_ID, Long.toString(originalWay.getId())));
		List<WayNode> wayNodes = new ArrayList<>();
		for (int i=startNodeIndex; i<= endNodeIndex; i++) {
			wayNodes.add(originalWay.getWayNodes().get(i));
		}
		way.getWayNodes().addAll(wayNodes);
		
		return way;
	}
	
}