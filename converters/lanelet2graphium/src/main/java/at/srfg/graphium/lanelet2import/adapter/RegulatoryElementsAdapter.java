/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.lanelet2import.adapter;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.helper.LaneletHelper;
import at.srfg.graphium.model.hd.HDRegulatoryElementType;
import at.srfg.graphium.model.hd.IHDRegulatoryElement;
import at.srfg.graphium.model.hd.impl.HDRegulatoryElement;
import com.vividsolutions.jts.geom.Geometry;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author mwimmer
 *
 */
public class RegulatoryElementsAdapter {

    private static Logger log = LoggerFactory.getLogger(RegulatoryElementsAdapter.class);

    public List<IHDRegulatoryElement> adaptRegulatorElement(
            Relation regulatoryElement, TLongObjectHashMap<Relation> relations,
            TLongObjectHashMap<Way> ways, TLongObjectHashMap<Node> nodes) {
		List<IHDRegulatoryElement> regulatoryElements = new ArrayList<>();

		Map<String, String> tags = new HashMap<>();
		regulatoryElement.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));

		String type = tags.get("subtype");
		IHDRegulatoryElement adapted = new HDRegulatoryElement();
		adapted.setId(regulatoryElement.getId());

		HDRegulatoryElementType regulatoryElementType = HDRegulatoryElementType.fromValue(type);
		if(regulatoryElementType != null) {
			adapted.setType(HDRegulatoryElementType.TRAFFIC_LIGHT);
			Set<Long> refLines = new HashSet<>();
			Set<Long> refers = new HashSet<>();
			for (RelationMember member : regulatoryElement.getMembers()) {
				if (member.getMemberType().equals(EntityType.Way)) {
					String role = member.getMemberRole();
					if(role.equals("ref_line")) {
						//Geometry refLineGeom = LaneletHelper.createLinestring(ways.get(member.getMemberId()), nodes, Constants.SRID);
						//adapted.setGeometry(refLineGeom);
						//log.info("creating ref_line geom");
						refLines.add(member.getMemberId());
					}
					else if(role.equals("refers")) {
						refers.add(member.getMemberId());
					}
					else {
						log.info("role {} not handled", role);
					}
				}
			}
			if(!refLines.isEmpty()) {
				adapted.setRefLineIds(refLines);
			}
			if(!refers.isEmpty()) {
				adapted.setRefersIds(refers);
			}
			regulatoryElements.add(adapted);
		}
		else {
			log.warn("unknown regulatory element type: {}, skipping", type);
		}
		return regulatoryElements;
    }
}