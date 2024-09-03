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
import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.gipimport.model.IGipTurnEdge;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TLongSet;

/**
 * Created by shennebe on 18.12.2015.
 */
public class GipLinkTurnEdgesParser extends AbstractSectionParser<TLongObjectMap<List<IGipTurnEdge>>> {

    private final IImportConfigIdf config;
    private final IGipSectionParser<TLongObjectMap<IGipNode>> nodeParser;
    private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    private final IGipSectionParser<TLongSet> linkCoordinateParser;
    private final TLongObjectMap<List<IGipTurnEdge>> turnEdges;

    public GipLinkTurnEdgesParser(IGipParser parserReference, IImportConfigIdf config,
                                  IGipSectionParser<TLongObjectMap<IGipNode>> nodeParser,
                                  IGipSectionParser<TLongObjectMap<IGipLink>> linkParser,
                                  IGipSectionParser<TLongSet> linkCoordinateParser) {
        super(parserReference);
        this.config = config;
        this.nodeParser = nodeParser;
        this.linkParser = linkParser;
        this.linkCoordinateParser = linkCoordinateParser;
        this.turnEdges = new TLongObjectHashMap<>();
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {

        int countLinksFiltered = 0;
        int countLinksFilteredOnAccessType = 0;
        int countMissingFromLinks = 0;
        int countMissingToLinks = 0;
        int countMissingViaNodes = 0;
        int countInvalidViaNodes = 0;
        int countInvalidViaNodesFoundInFromLinestring = 0;
        int countInvalidViaNodesFoundInToLinestring = 0;

        String line = null;
        try {

            TObjectIntMap<String> atrPos = new TObjectIntHashMap<>();
            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    break;
                }

                // build map of attribute and position in line (order of attributes is not defined!)
                if (line.startsWith("atr")) {
                    //V1
                    // atr;TURN_ID;FROM_LINK;TO_LINK;VIA_NODE;VEHICLE_TYPE;TIME;CAPACITY;LANESFROM;LANESTO;STATUS
                    //V2
                    //atr;OBJECT_ID;SHORT_ID;LINK_FROM_ID;LINK_FROM_SHORT_ID;LINK_TO_ID;LINK_TO_SHORT_ID;NODE_VIA_ID;NODE_VIA_SHORT_ID;ACCESS_TOW;LANES_TOW;TURN_USE_ID;ONEWAY_CAR;ONEWAY_BUS
                            atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    boolean ok = true;

                    String[] values = line.split(";");

                    IGipTurnEdge turnEdge = getParserReference().getModelFactory().newTurnEdge();
                    //V1
                    //turnEdge.setId(Long.parseLong(values[atrPos.get("TURN_ID")]));
                    //V2
                    turnEdge.setId(Long.parseLong(values[atrPos.get("SHORT_ID")]));

                    //V1
                    //turnEdge.setVehicleType(new Integer(values[atrPos.get("VEHICLE_TYPE")]));
                    //V2
                    turnEdge.setVehicleType(new Long(values[atrPos.get("ACCESS_TOW")]));
                    if (ok && config.getAccessTypes() != null && !config.getAccessTypes().isEmpty()){
                        ok = ParserHelper.validateAccess(turnEdge.getVehicleType(), config.getAccessTypes());
                        countLinksFilteredOnAccessType++;
                    }
                    
                    if (ok && turnEdge.getVehicleType() == 0) {
                    	log.debug("TurnEdge with id = " + turnEdge.getId() + " and fromLinkId = " + turnEdge.getFromLinkId() + " has no valid AccessType / VehicleType");
                    	ok = false;
                    }

                    if (ok) {
                        //V1
                    	//if (this.linkParser.getResult().containsKey(Long.parseLong(values[atrPos.get("FROM_LINK")]))) {
                    	//V2
                        if (this.linkParser.getResult().containsKey(Long.parseLong(values[atrPos.get("LINK_FROM_SHORT_ID")]))) {
                    		turnEdge.setFromLinkId(Long.parseLong(values[atrPos.get("LINK_FROM_SHORT_ID")]));
	                    } else {
	                        log.debug("TurnEdge with id = " + turnEdge.getId() + " references missing fromLink with id = " +
	                                values[atrPos.get("LINK_FROM_SHORT_ID")]);
	                        ok = false;
	                        countMissingFromLinks++;
	                    }
                    }

                    if (ok) {
                    	//V1
                        //if (this.linkParser.getResult().containsKey(Long.parseLong(values[atrPos.get("TO_LINK")]))) {
                        //V2
                        if (this.linkParser.getResult().containsKey(Long.parseLong(values[atrPos.get("LINK_TO_SHORT_ID")]))) {
	                        turnEdge.setToLinkId(Long.parseLong(values[atrPos.get("LINK_TO_SHORT_ID")]));
	                    } else {
	                        log.debug("TurnEdge with id = " + turnEdge.getId() + " references missing toLink with id = " +
	                                values[atrPos.get("LINK_TO_SHORT_ID")]);
	                        ok = false;
	                        countMissingToLinks++;
	                    }
                    }

                    // if links have been filtered => skip import of this turnEdge
                    if (ok && (!this.linkCoordinateParser.getResult().contains(turnEdge.getFromLinkId()) ||
                            !this.linkCoordinateParser.getResult().contains(turnEdge.getToLinkId()))) {
                        ok = false;
                        countLinksFiltered++;
                    }

                    if (ok) {
                        //V1
                    	//if (this.nodeParser.getResult().containsKey(Long.parseLong(values[atrPos.get("VIA_NODE")]))) {
                    	//V2
                        if (this.nodeParser.getResult().containsKey(Long.parseLong(values[atrPos.get("NODE_VIA_SHORT_ID")]))) {
	                        turnEdge.setViaNodeId(Long.parseLong(values[atrPos.get("NODE_VIA_SHORT_ID")]));
	                    } else {
	                    	log.debug("TurnEdge with id = " + turnEdge.getId() + " references missing viaNode with id = " +
	                                values[atrPos.get("NODE_VIA_SHORT_ID")]);
	                    	ok = false;
	                        countMissingViaNodes++;
	                    }
                    }

                    // validation: is VIA_NODE a start or end node of each link?
                    if (ok) {
                    	if (turnEdge.getViaNodeId() != 0) {
	                        final IGipNode viaNode = this.nodeParser.getResult().get(turnEdge.getViaNodeId());
	                        final IGipLink fromLink = this.linkParser.getResult().get(turnEdge.getFromLinkId());
	                        final IGipLink toLink = this.linkParser.getResult().get(turnEdge.getToLinkId());
	                        if (turnEdge.getViaNodeId() != fromLink.getFromNodeId() &&
	                                turnEdge.getViaNodeId() != fromLink.getToNodeId()) {
	                            log.error("TurnEdge with id = " + turnEdge.getId() + " has viaNode with id = " +
	                                    turnEdge.getViaNodeId() + ", but it is whether start node nor end node of fromLink " +
	                                    " with id = " + turnEdge.getFromLinkId() + "!");
	                            ok = false;
	
	                            // maybe viaNode is inside the linestring (but no endpoint)?
	                            for (int i = 0; i < fromLink.getCoordinatesX().length; i++) {
	                                int linkCoordx = fromLink.getCoordinatesX()[i];
	                                int linkCoordy = fromLink.getCoordinatesY()[i];
	                                if (linkCoordx == viaNode.getCoordinateX()
	                                        || linkCoordy == viaNode.getCoordinateY()) {
	                                    countInvalidViaNodesFoundInFromLinestring++;
	                                }
	                            }
	
	                        }
	
	                        if (turnEdge.getViaNodeId() != toLink.getFromNodeId() &&
	                                turnEdge.getViaNodeId() != toLink.getToNodeId()) {
	                            log.error("TurnEdge with id = " + turnEdge.getId() + " has viaNode with id = " +
	                                    turnEdge.getViaNodeId() + ", but it is whether start node nor end node of toLink " +
	                                    " with id = " + turnEdge.getToLinkId() + "!");
	                            ok = false;
	
	                            for (int i = 0; i < toLink.getCoordinatesX().length; i++) {
	                                int linkCoordx = toLink.getCoordinatesX()[i];
	                                int linkCoordy = toLink.getCoordinatesY()[i];
	                                if (linkCoordx == viaNode.getCoordinateX()
	                                        || linkCoordy == viaNode.getCoordinateY()) {
	                                    countInvalidViaNodesFoundInToLinestring++;
	                                }
	                            }
	
	                        }
	                        if (!ok) {
	                            countInvalidViaNodes++;
	                        }
                    	}
                    }

                    //V1
                    //turnEdge.setVehicleType(Integer.parseInt(values[atrPos.get("VEHICLE_TYPE")]));
                    //V2
                    turnEdge.setVehicleType(Long.parseLong(values[atrPos.get("ACCESS_TOW")]));

                    if (ok) {
                        if (!turnEdges.containsKey(turnEdge.getFromLinkId())) {
                            turnEdges.put(turnEdge.getFromLinkId(), new ArrayList<>());
                        }
                        turnEdges.get(turnEdge.getFromLinkId()).add(turnEdge);
                    }
                }

                line = file.readLine();
            }

        } catch (IOException e) {
            log.error(e.toString());
        }

        postImport();

        getParserReference().setPhase(IGipParser.PHASE_UNDEFINED);

    	log.info("\n	Found valid turnEdges for " + turnEdges.size() + " links");
    	log.info("\n	Filtered links: " + countLinksFiltered);
        if (countMissingFromLinks > 0 ||
                countMissingToLinks > 0 ||
                countMissingViaNodes > 0 ||
                countInvalidViaNodes > 0) {
        	log.info("\n	Found errors:\n" +
                    "		missing fromLinks: " + countMissingFromLinks + "\n" +
                    "		missing toLinks: " + countMissingToLinks + "\n" +
                    "		missing viaNodes: " + countMissingViaNodes + "\n" +
                    "		invalid viaNodes: " + countInvalidViaNodes + "\n" +
                    "		invalid viaNodes found in linestring of fromLink: " + countInvalidViaNodesFoundInFromLinestring + "\n" +
                    "		invalid viaNodes found in linestring of toLink: " + countInvalidViaNodesFoundInToLinestring);
        } else {
            log.info("	No errors found!");
        }

        return line;
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.PHASE_TURNEDGE;
    }

    @Override
    public TLongObjectMap<List<IGipTurnEdge>> getResult() {
        return this.turnEdges;
    }
}
