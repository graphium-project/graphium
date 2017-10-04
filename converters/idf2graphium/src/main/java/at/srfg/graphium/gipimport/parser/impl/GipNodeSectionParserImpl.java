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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.gipimport.helper.GeoHelper;
import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * Created by shennebe on 18.12.2015.
 */
public class GipNodeSectionParserImpl extends AbstractSectionParser<TLongObjectMap<IGipNode>> {

    private TLongObjectMap<IGipNode> nodes;
    private double minX = Double.MAX_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = 0;
    private double maxY = 0;

    private static Logger log = LoggerFactory.getLogger(GipNodeSectionParserImpl.class);

    public GipNodeSectionParserImpl(IGipParser gipParser) {
        super(gipParser);
        this.nodes = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
    }


    public String parseSectionInternally(BufferedReader file) {

        String line = null;
        TObjectIntMap<String> atrPos = null;

        try {

            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    break;
                }

                // build map of attribute and position in line (order of attributes is not defined!)
                // atr;NODE_ID;LEVEL;VIRTUAL_TYPE;X;Y;VIRT_LINKID;VIRT_PERCENT;BIKE_DELAY;STATUS
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");
                    IGipNode node = super.getParserReference().getModelFactory().newNode();
                    node.setId(Long.parseLong(values[atrPos.get("NODE_ID")]));
                    node.setVirtual(values[atrPos.get("VIRTUAL_TYPE")].equals("1"));
                    double xValue = Double.parseDouble(values[atrPos.get("X")]);
                    node.setCoordinateX((int) Math.round(xValue * GeoHelper.COORDINATE_MULTIPLIER));
                    double yValue = Double.parseDouble(values[atrPos.get("Y")]);
                    node.setCoordinateY((int) Math.round(yValue * GeoHelper.COORDINATE_MULTIPLIER));
                    node.setVirtualLinkId(Long.parseLong(values[atrPos.get("VIRT_LINKID")]));

                    nodes.put(node.getId(), node);

                    if (Double.parseDouble(values[atrPos.get("X")]) < minX) {
                        minX = Double.parseDouble(values[atrPos.get("X")]);
                    }
                    if (Double.parseDouble(values[atrPos.get("Y")]) < minY) {
                        minY = Double.parseDouble(values[atrPos.get("Y")]);
                    }
                    if (Double.parseDouble(values[atrPos.get("X")]) > maxX) {
                        maxX = Double.parseDouble(values[atrPos.get("X")]);
                    }
                    if (Double.parseDouble(values[atrPos.get("Y")]) > maxY) {
                        maxY = Double.parseDouble(values[atrPos.get("Y")]);
                    }

                }

                line = file.readLine();
            }

        } catch (IOException e) {
            log.error(e.toString());
        }

        postImport();

        return line;
    }


    @Override
    public String getPhase() {
        return IGipSectionParser.PHASE_NODE;
    }

    @Override
    public TLongObjectMap<IGipNode> getResult() {
        return this.nodes;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }
}
