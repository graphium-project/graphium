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
package at.srfg.graphium.lanelet2import.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.geomutils.GeometryUtils;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class EntityParsingHelper {

	public static String getType(Entity entity) {
		String type = null;
		
		for (Tag tag : entity.getTags()) {
			if ("type".equals(tag.getKey().toLowerCase())) {
				type = tag.getValue();
			}
		}
		
		return type;
	}
	
	public static SimpleEntry<String, String> parseLaneletBoarderType(String side, Way way) {
		String type = null;
		String subType = null;
		SimpleEntry<String, String> pair = null;
		
		for (Tag tag : way.getTags()) {
			if ("type".equals(tag.getKey().toLowerCase())) {
				type = tag.getValue();
			}
			if ("subtype".equals(tag.getKey().toLowerCase())) {
				subType = tag.getValue();
			}
		}
		
		if (type != null) {
			pair = new SimpleEntry<>(type, subType);
		}

		return pair;
	}
	
	public static LineString createLinestring(Way way, TLongObjectHashMap<Node> nodes, int srid) {
		if (way.getWayNodes() == null) {
			return null;
		}
		
		List<Coordinate> coords = new ArrayList<>();
		int i = 0;
		for (WayNode wayNode : way.getWayNodes()) {
			Node node = nodes.get(wayNode.getNodeId());
			if (node != null) {
				coords.add(new Coordinate(node.getLongitude(), node.getLatitude()));
				i++;
			} else {
				return null;
			}
		}
		
		if (i < 2) {
			return null;
		}
		
		return GeometryUtils.createLineString(coords.toArray(new Coordinate[0]), srid);
	}
	
}