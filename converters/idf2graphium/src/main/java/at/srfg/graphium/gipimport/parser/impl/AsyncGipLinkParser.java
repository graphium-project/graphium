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

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipModelFactory;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.model.Access;
import au.com.bytecode.opencsv.CSVReader;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;

/**
 * Created by shennebe on 14.12.2015.
 */
public class AsyncGipLinkParser implements Supplier<IGipLink> {

    private final String line;
    private final IGipModelFactory modelFactory;
    private final TLongObjectMap<IGipNode> nodes;
    private final Set<Integer> functionalRoadClasses;
    private final Set<Access> accessTypes;
    private final TObjectIntMap<String> atrPos;
    private static int nrCorrectedLines = 0;
    private static final Logger log = LoggerFactory.getLogger(AsyncGipLinkParser.class);


    public AsyncGipLinkParser(String line,
                              IGipModelFactory gipModelFactory,
                              TLongObjectMap<IGipNode> nodes,
                              Set<Integer> functionalRoadClasses,
                              Set<Access> accessTypes,
                              TObjectIntMap<String> atrPos) {
        this.line = line;
        this.modelFactory = gipModelFactory;
        this.nodes = nodes;
        this.functionalRoadClasses = functionalRoadClasses;
        this.accessTypes = accessTypes;
        this.atrPos = atrPos;
    }

    public static int getNrCorrectedLines() {
        return nrCorrectedLines;
    }

    private IGipLink parseLink() throws IOException {

        if (this.line.startsWith("rec") && this.atrPos != null && this.atrPos.size() > 0) {

            String[] result = ParserHelper.correctLine(this.line);
            String line = result[0];
            if (result.length > 1) {
                nrCorrectedLines++;
            }

            CSVReader reader = new CSVReader(new StringReader(line), ';');

            String[] values = reader.readNext();

            reader.close();

            try {
                IGipLink link = modelFactory.newLink();
                link.setId(Long.parseLong(values[atrPos.get("LINK_ID")]));
                link.setName1(values[atrPos.get("NAME1")]);
                link.setName2(values[atrPos.get("NAME2")]);
                IGipNode fromNode = this.nodes.get(Long.parseLong(values[atrPos.get("FROM_NODE")]));
                if (fromNode != null) {
                    link.setFromNodeId(fromNode.getId());
                }
                IGipNode toNode = this.nodes.get(Long.parseLong(values[atrPos.get("TO_NODE")]));
                if (toNode != null) {
                    link.setToNodeId(toNode.getId());
                }
                short speedTow = Short.parseShort(values[atrPos.get("MAXSPEED_TOW_CAR")]);
                if (speedTow <= 0) {
                	speedTow = Short.parseShort(values[atrPos.get("SPEED_TOW_CAR")]);
                }
                short speedBkw = Short.parseShort(values[atrPos.get("MAXSPEED_BKW_CAR")]);
                if (speedBkw <= 0) {
                	speedBkw = Short.parseShort(values[atrPos.get("SPEED_BKW_CAR")]);
                }
                link.setSpeedTow(speedTow);
                link.setSpeedBkw(speedBkw);
                link.setAccessTow(Integer.parseInt(values[atrPos.get("ACCESS_TOW")]));
                link.setAccessBkw(Integer.parseInt(values[atrPos.get("ACCESS_BKW")]));
                link.setLength(Float.parseFloat(values[atrPos.get("LENGTH")]));
                link.setFuncRoadClassValue(Short.parseShort(values[atrPos.get("FUNCROADCLASS")]));
                link.setFormOfWay(Short.parseShort(values[atrPos.get("FORMOFWAY")]));
                link.setUrban(Short.parseShort(values[atrPos.get("URBAN")]) == 1);

                float lanesTow = Short.parseShort(values[atrPos.get("LANES_TOW")]);	// input is decimal(2,1)!
                float lanesBkw = Short.parseShort(values[atrPos.get("LANES_BKW")]);	// input is decimal(2,1)!
                link.setLanesTow((short) lanesTow);
                link.setLanesBkw((short) lanesBkw);
                link.setEdgeId(Long.parseLong(values[atrPos.get("EDGE_ID")]));

                // TODO: Brauchen wir das?
                link.setUTurn(Byte.parseByte(values[atrPos.get("U_TURN")]));


                link.setOneway(Byte.parseByte(values[atrPos.get("ONEWAY")]));
                if (link.getOneway() == (byte)-1) {
                    link.setOneway((byte)2);
                }

                if (fromNode == null || fromNode.getCoordinateX() == 0 || fromNode.getCoordinateY() == 0 ||
                        toNode == null || toNode.getCoordinateX() == 0 || toNode.getCoordinateY() == 0) {

                    StringBuilder err = new StringBuilder();
                    if (fromNode == null) {
                        err.append(" FromNode is null;");
                    } else if (fromNode.getCoordinateY() == 0 || fromNode.getCoordinateX() == 0) {
                        err.append(" FromNode-Coordinate is null;");
                    }
                    if (toNode == null) {
                        err.append(" ToNode is null;");
                    } else if (toNode.getCoordinateY() == 0 || toNode.getCoordinateX() == 0) {
                        err.append(" ToNode-Coordinate is null;");
                    }

                    log.error("Link with ID " + link.getId() + " has invalid geometry!!! (" + err.toString() + ")");
                    return null;
                }

                link.setCoordinatesX(new int[]{fromNode.getCoordinateX(),toNode.getCoordinateX()});
                link.setCoordinatesY(new int[]{fromNode.getCoordinateY(),toNode.getCoordinateY()});
                link.setLevel(Float.parseFloat(values[atrPos.get("LEVEL")]));
                try {
                    link.setBridge(Integer.parseInt(values[atrPos.get("BRUNNEL")]) == 1);
                    link.setTunnel(Integer.parseInt(values[atrPos.get("BRUNNEL")]) == 2);
                } catch (NumberFormatException e) {
                    link.setBridge(Float.parseFloat(values[atrPos.get("BRUNNEL")]) == 1.0);
                    link.setTunnel(Float.parseFloat(values[atrPos.get("BRUNNEL")]) == 2.0);
                }

                int baustatus = Integer.parseInt(values[atrPos.get("BAUSTATUS")]);
                
                //this.links.put(link.getId(), link);

                boolean valid = true;
                // is link valid to enqueue?

                if (functionalRoadClasses != null && !functionalRoadClasses.contains(new Integer(link.getFuncRoadClassValue()))) {
                    valid = false;
                }

                if (baustatus != 5) {
                	// accept only completed segments
                	valid = false;
                }
                
                if (valid && accessTypes != null && !accessTypes.isEmpty()) {
                    boolean accessFound = ParserHelper.validateAccess(link.getAccessTow(), accessTypes);
                    if (!accessFound) {
                        accessFound = ParserHelper.validateAccess(link.getAccessBkw(), accessTypes);
                    }
                    valid = accessFound;
                }

                if (valid) {
                    link.setValid(true);
                }

                return link;

            } catch(NumberFormatException e) {
                log.warn("expected number but was not in " + values[atrPos.get("LINK_ID")],e);
                log.warn("row data line: " + ParserHelper.rebuildHeaderLine(values));
            }

        }
        return null;
    }

    @Override
    public IGipLink get() {
        try {
            return this.parseLink();
        } catch (Exception e) {
            log.error("An Exception Occured while parsing ",e);
        }
        return null;
    }
}
