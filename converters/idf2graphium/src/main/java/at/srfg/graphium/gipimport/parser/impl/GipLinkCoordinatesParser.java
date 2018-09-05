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
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.srfg.graphium.gipimport.helper.GeoHelper;
import at.srfg.graphium.gipimport.helper.GipComparableLinkCoordinate;
import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * Created by shennebe on 18.12.2015.
 */
public class GipLinkCoordinatesParser extends AbstractSectionParser<TLongSet> {

    private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    private final TLongSet linksToEnqueue;
    private final ExecutorService executor;

    public GipLinkCoordinatesParser(IGipParser parserReference,
                                    IGipSectionParser<TLongObjectMap<IGipLink>> linkParser) {
        super(parserReference);
        this.linkParser = linkParser;
        this.linksToEnqueue = new TSynchronizedLongSet(new TLongHashSet());
        this.executor = Executors.newFixedThreadPool(30);
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {

        if (this.executor.isShutdown()) {
            throw new RuntimeException("Executor is already shutdown");
        }

        String line = null;
        try {

            long currentLinkId = -1;
            Set<GipComparableLinkCoordinate> linkCoords = new TreeSet<>();

            TObjectIntMap<String> atrPos = new TObjectIntHashMap<>();
            GipComparableLinkCoordinate compCoord = null;

            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    if (currentLinkId > -1) {
                        postImport();

                        buildLinkAsync(currentLinkId, linkCoords, this.linkParser.getResult(), linksToEnqueue);

                        //buildLink(currentLinkId, linkCoords, links, linkIdsEnqueued);
                        linkCoords.clear();
                    }
                    break;
                }

                // build map of attribute and position in line (order of attributes is not defined!)
                if (line.startsWith("atr")) {
                    // atr;LINK_ID;COUNT;X;Y;STATUS
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");
                    // atr;LINK_ID;COUNT;X;Y;STATUS
                    long linkId = Long.parseLong(values[atrPos.get("LINK_ID")]);
                    if (currentLinkId != linkId && currentLinkId > -1) {
                        buildLinkAsync(currentLinkId, linkCoords, this.linkParser.getResult(), linksToEnqueue);
                        linkCoords.clear();
                    }

                    currentLinkId = linkId;
                    compCoord = new GipComparableLinkCoordinate();
                    compCoord.setLinkId(linkId);
                    compCoord.setCount(Integer.parseInt(values[atrPos.get("COUNT")]));
                    compCoord.setX((int) Math.round(Double.parseDouble(values[atrPos.get("X")]) * GeoHelper.COORDINATE_MULTIPLIER));
                    compCoord.setY((int) Math.round(Double.parseDouble(values[atrPos.get("Y")]) * GeoHelper.COORDINATE_MULTIPLIER));
                    linkCoords.add(compCoord);

                }

                line = file.readLine();
            }

            postImport();

            // enqueue last imported link
            CompletableFuture future = buildLinkAsync(currentLinkId, linkCoords, this.linkParser.getResult(), this.linksToEnqueue);

            while (!future.isDone()) {
                Thread.sleep(2000);
            }

            // enqueue rest of links... (those without linkCoordinates)
            this.linkParser.getResult().valueCollection().stream().filter(IGipLink::isValid).forEach(link -> {
                if (!this.linksToEnqueue.contains(link.getId())) {
                    this.linksToEnqueue.add(link.getId());
                }
            });


        } catch (IOException | InterruptedException e) {
            log.error(e.toString());
        } finally {
            this.executor.shutdown();
        }

        return line;
    }

    private CompletableFuture buildLinkAsync(final long currentLinkId,
                                             final Set<GipComparableLinkCoordinate> linkCoords,
                                             final TLongObjectMap<IGipLink> links,
                                             final TLongSet linksToEnqueue) {
        int[] linkCoordsX = new int[linkCoords.size() + 2];
        int[] linkCoordsY = new int[linkCoords.size() + 2];
        int i = 1;
        for (GipComparableLinkCoordinate linkCoordinate : linkCoords) {
            linkCoordsX[i] = linkCoordinate.getX();
            linkCoordsY[i] = linkCoordinate.getY();
            i++;
        }
        return CompletableFuture.supplyAsync(new AsyncGipLinkBuilder(
                links.get(currentLinkId),
                linkCoordsX, linkCoordsY), executor).thenAcceptAsync(gipLink -> {
            try {
                if (gipLink != null) {
                    links.put(currentLinkId, gipLink);
                    if (gipLink.isValid()) {
                    	linksToEnqueue.add(currentLinkId);
                    }
                }
            } catch (Exception e) {
                log.error("Async Error ", e);
            }
        });
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.PHASE_LINKCOORDINATES;
    }

    @Override
    public TLongSet getResult() {
        return this.linksToEnqueue;
    }
}
