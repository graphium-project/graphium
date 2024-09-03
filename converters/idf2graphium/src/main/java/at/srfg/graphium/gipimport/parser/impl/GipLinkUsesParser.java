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
package at.srfg.graphium.gipimport.parser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import at.srfg.graphium.model.Access;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * @author mwimmer
 *
 */
public class GipLinkUsesParser extends AbstractSectionParser<TLongObjectMap<Map<String, Object>>> {

	private TLongObjectMap<Map<String, Object>> map;

	public GipLinkUsesParser(IGipParser parserReference) {
		super(parserReference);
		map = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
	}

	@Override
	public String getPhase() {
		return IGipSectionParser.PHASE_LINKUSE;
	}

	@Override
	public TLongObjectMap<Map<String, Object>> getResult() {
		return map;
	}

	@Override
	public String parseSectionInternally(BufferedReader file) {
		String line = null;
		try {
			
			Set<Access> accessTypes = new HashSet<>();
			accessTypes.add(Access.PUBLIC_BUS);

			TObjectIntMap<String> atrPos = new TObjectIntHashMap<>();
			line = file.readLine();
			while (line != null) {
				if (line.startsWith("tbl;")) {
					break;
				}
				
				// build map of attribute and position in line (order of attributes is not defined!)
				if (line.startsWith("atr")) {
					//V1
					// atr;USE_ID;LINK_ID;COUNT;OFFSET;WIDTH;MINWIDTH;FROM_PERCENT;TO_PERCENT;BASETYPE;USE_ACCESS_TOW;USE_ACCESS_BKW;STATUS;USE_OBJECTID;LINK_OBJECTID
					//V2
					//atr;OBJECT_ID;SHORT_ID;LINK_ID;LINK_SHORT_ID;LINEAR_USE_ID;OFFSET_AVERAGE;WIDTH_AVERAGE;WIDTH_MINIMAL;LINK_PERCENTAGE_FROM;LINK_PERCENTAGE_TO;BASE_TYPE;ACCESS_TOW;ACCESS_BKW;SURFACE;ELECTRIC;ONEWAY_CAR;ONEWAY_BIKE;ONEWAY_BUS;ONEWAY_PEDESTRIAN
					atrPos = ParserHelper.splitAtrLine(line);
				}
				
				if (line.startsWith("rec")) {
					boolean ok = true;

					String[] values = line.split(";");

					//V1
					//if (values[atrPos.get("BASETYPE")].equals("34")) {
					//V2
					if (values[atrPos.get("BASE_TYPE")].equals("34")) {
						//V1
						//boolean tow = ParserHelper.validateAccess(Integer.parseInt(values[atrPos.get("USE_ACCESS_TOW")]), accessTypes);
						//V2
						boolean tow = ParserHelper.validateAccess(Integer.parseInt(values[atrPos.get("ACCESS_TOW")]), accessTypes);
						//V1
						//boolean bkw = ParserHelper.validateAccess(Integer.parseInt(values[atrPos.get("USE_ACCESS_BKW")]), accessTypes);
						//V2
						boolean bkw = ParserHelper.validateAccess(Integer.parseInt(values[atrPos.get("ACCESS_BKW")]), accessTypes);
						String direction = "";
						if (!tow && bkw) {
							direction = "bkw";
						} else if (tow && !bkw) {
							direction = "tow";
						} else {
							direction = "both";
						}
						Map<String, Object> buslaneMap = new HashMap<>();
						buslaneMap.put("buslane", direction);

						//V1
						//map.put(Long.parseLong(values[atrPos.get("LINK_ID")]), buslaneMap);
						//V2
						map.put(Long.parseLong(values[atrPos.get("LINK_SHORT_ID")]), buslaneMap);
					}
					
				}

				line = file.readLine();

			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return line;
	}

}
