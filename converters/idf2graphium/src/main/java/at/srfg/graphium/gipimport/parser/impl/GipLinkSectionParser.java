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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import at.srfg.graphium.model.Access;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Created by shennebe on 18.12.2015.
 */
public class GipLinkSectionParser extends AbstractSectionParser<TLongObjectMap<IGipLink>> {

    private TLongObjectMap<IGipLink> links;
    private final IGipSectionParser<TLongObjectMap<IGipNode>> nodeParser;
    private final Set<Integer> functionalRoadClasses;
    private final Set<Access> accessTypes;
    private final ExecutorService executor;
    private final ImportStatistics statistics;

    public GipLinkSectionParser(IGipParser parserReference,
                                IGipSectionParser<TLongObjectMap<IGipNode>> nodeParser,
                                IImportConfigIdf config,
                                ImportStatistics statistics) {
        super(parserReference);
        this.links = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
        this.nodeParser = nodeParser;
        this.functionalRoadClasses = config.getFrcList();
        this.accessTypes = config.getAccessTypes();
        this.executor = Executors.newFixedThreadPool(15);
        this.statistics = statistics;
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {

        if (this.executor.isShutdown()) {
            throw new RuntimeException("Executor is already shutdown");
        }

        String line = null;
        try {

            List<CompletableFuture> futures = new ArrayList<>();

            TObjectIntMap<String> atrPos = new TObjectIntHashMap<>();
            line = file.readLine();

            while (line != null) {

                if (line.startsWith("tbl;")) {
                    break;
                }

                // build map of attribute and position in line (order of attributes is not defined!)
                if (line.startsWith("atr;")) {
                    // atr;LINK_ID;NAME1;NAME2;FROM_NODE;TO_NODE;SPEED_TOW_CAR;SPEED_BKW_CAR;SPEED_TOW_TRUCK;SPEED_BKW_TRUCK;
                    // ACCESS_TOW;ACCESS_BKW;LENGTH;FUNCROADCLASS;CAP_TOW;CAP_BKW;LANES_TOW;LANES_BKW;FORMOFWAY;BRUNNEL;
                    // MAXHEIGHT;MAXWIDTH;MAXPRESSURE;ABUTTER_CAR;ABUTTER_LORRY;U_TURN;SLOPE;URBAN;WIDTH;LEVEL;BAUSTATUS;
                    // PTV_TYPENO;SUBNET_ID;ONEWAY;BLT;BLB;EDGE_ID;AGG_TYP;STATUS
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                AsyncGipLinkParser linkParser = new AsyncGipLinkParser(line,
                        this.getParserReference().getModelFactory(),
                        this.nodeParser.getResult(),
                        this.functionalRoadClasses,
                        this.accessTypes,
                        atrPos);

                futures.add(CompletableFuture.supplyAsync(linkParser, this.executor)
                        .thenAcceptAsync(iGipLink -> {
                            try {
                                if (iGipLink != null) {
                                    links.put(iGipLink.getId(), iGipLink);
                                    if (iGipLink.isValid()) {
                                        //linksToBuild.add(iGipLink.getId());
                                        statistics.increaseNrOfValidLinks();
                                    } else {
                                        log.warn("Gip link not valid: " + iGipLink.toString());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("AcceptAsyncError ", e);
                            }
                        }));

                line = file.readLine();
            }

            for (CompletableFuture future : futures) {
                while (!future.isDone()) {
                    Thread.sleep(500);
                }
            }

        } catch (IOException | InterruptedException e) {
            log.error(e.toString());
        } finally {
            this.executor.shutdown();
        }

        postImport();

        return line;
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.PHASE_LINK;
    }

    @Override
    public TLongObjectMap<IGipLink> getResult() {
        return this.links;
    }
}
