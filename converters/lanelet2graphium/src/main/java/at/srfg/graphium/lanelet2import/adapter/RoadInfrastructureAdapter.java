/**
 * Copyright © 2021 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.lanelet2import.adapter;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.LaneletHelper;
import at.srfg.graphium.model.hd.IHDRoadInfrastructure;
import at.srfg.graphium.model.hd.impl.HDRoadInfrastructure;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadInfrastructureAdapter {
	private static Logger log = LoggerFactory.getLogger(RoadInfrastructureAdapter.class);
	
	public List<IHDRoadInfrastructure> adapt(TLongObjectHashMap<Relation> relations, TLongObjectHashMap<Way> ways,
											 TLongObjectHashMap<Node> nodes) {
		List<IHDRoadInfrastructure> roadInfras = new ArrayList<>();
		
		for (Way way : ways.valueCollection()) {
			String type = LaneletHelper.getType(way);
			if (type != null && (type.equals(Constants.STOP_LINE) || type.equals(Constants.ARROW) ||
					type.equals(Constants.TRAFFIC_LIGHT) || type.equals(Constants.TRAFFIC_SIGN))) {
				IHDRoadInfrastructure roadInfra = adapt(way, ways, nodes);
				if (roadInfra != null) {
					roadInfras.add(roadInfra);
				}
			}
		}
		
		return roadInfras;
	}
	
	public IHDRoadInfrastructure adapt(Way way, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		IHDRoadInfrastructure roadInfra = new HDRoadInfrastructure();
		roadInfra.setId(way.getId());
		roadInfra.setGeometry(LaneletHelper.createLinestring(way, nodes, Constants.SRID));
		roadInfra.setType(LaneletHelper.getType(way));

		Map<String, String> tags = new HashMap<>();
		way.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));

		// TODO: handle more tags
		if(tags.containsKey("subtype")) {
			roadInfra.setTags(new HashMap<>());
			roadInfra.getTags().put("subtype", tags.get("subtype"));
		}

		return roadInfra;
	}
}
