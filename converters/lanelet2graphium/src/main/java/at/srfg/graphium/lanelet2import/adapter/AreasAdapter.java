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

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.lanelet2import.helper.AreaHelper;
import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.LaneletHelper;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.impl.HDArea;
import gnu.trove.map.hash.TLongObjectHashMap;

public class AreasAdapter {
	private static Logger log = LoggerFactory.getLogger(AreasAdapter.class);
	
	public List<IHDArea> adaptLanelets(List<Relation> relations, TLongObjectHashMap<Way> ways,
			TLongObjectHashMap<Node> nodes) {
		List<IHDArea> areas = new ArrayList<IHDArea>();
		
		for (Relation rel : relations) {
			String type = LaneletHelper.getType(rel);
			if (type != null && type.equals(Constants.TYPE_AREA)) {
				IHDArea area = adapt(rel, ways, nodes);
				if (area != null) {
					areas.add(area);
				}
			}
		}
		
		return areas;
	}
	
	public IHDArea adapt(Relation relation, TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		IHDArea area = new HDArea();
		area.setId(relation.getId());
		
		area.setAreaGeometry(AreaHelper.createPolygon(relation, ways, nodes, Constants.SRID));
		
		return area;
	}
}
