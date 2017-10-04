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
package at.srfg.graphium.pixelcuts.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import at.srfg.graphium.pixelcuts.model.IAzimuth;
import at.srfg.graphium.pixelcuts.model.IPixelCut;
import at.srfg.graphium.pixelcuts.model.IRenderingSegment;
import at.srfg.graphium.pixelcuts.model.ISegment;
import at.srfg.graphium.pixelcuts.model.impl.AzimuthImpl;
import at.srfg.graphium.pixelcuts.model.impl.PixelCut;
import at.srfg.graphium.pixelcuts.model.impl.RenderingSegmentImpl;
import at.srfg.graphium.pixelcuts.service.IRenderingCalculator;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

/**
 * Created by shennebe on 27.04.2015.
 */
public class RenderingCalculatorImpl implements IRenderingCalculator {


    private static Logger log = Logger.getLogger(RenderingCalculatorImpl.class);

    @Override
    public void calculateAllReduceFactors(final TLongObjectMap<TLongArrayList> nodeSegmentTable,
                                          final TLongObjectMap<ISegment> segmentsTable,
                                          final TLongSet relevantLinks,
                                          TLongObjectMap<IPixelCut> pixelCuts) {
    	TLongObjectMap<IRenderingSegment> renderingResults = new TLongObjectHashMap<>();
    	nodeSegmentTable.forEachEntry((nodeId, segmentIds) -> {
            if (segmentIds.size() > 1) {
                ISegment[] segments = new ISegment[segmentIds.size()];
                for (int i = 0; i < segmentIds.size(); i++) {
                    segments[i] = segmentsTable.get(segmentIds.get(i));
                }
                investigateNode(nodeId, renderingResults, segments);
            }
            return true;
        });
        adaptPixelCuts(pixelCuts, renderingResults);
    }

    private TLongObjectMap<IPixelCut> adaptPixelCuts(TLongObjectMap<IPixelCut> pixelCuts, TLongObjectMap<IRenderingSegment> renderingResults) {
    	for (Long key : renderingResults.keys()) {
    		IRenderingSegment seg = renderingResults.get(key);
    		pixelCuts.put(key, new PixelCut(seg.getId(), null, seg.getStartCutRight(), seg.getStartCutLeft(), seg.getEndCutRight(), seg.getEndCutLeft()));
    	}
    	return pixelCuts;
	}

	private void investigateNode(long nodeId, TLongObjectMap<IRenderingSegment> renderingResult,
    		ISegment... connectedSegments) {
        List<IAzimuth> azimuthList = new ArrayList<>();
        for (ISegment segmentFrom : connectedSegments) {
            for (ISegment segmentTo : connectedSegments) {
                if (segmentFrom != null && segmentTo != null &&
                        (segmentFrom.getId() != segmentTo.getId())) {
                    azimuthList.add(calculateAzimuth(nodeId, segmentFrom,segmentTo));
                }
            }
        }
        calculateRendering(azimuthList, renderingResult);
    }

    private void calculateRendering(List<IAzimuth> azimuthsPerNode,
                                    TLongObjectMap<IRenderingSegment> rendering) {
        for (IAzimuth azimuth : azimuthsPerNode) {
            this.calculateRendering(azimuth, azimuthsPerNode, rendering);
        }
    }

    private void calculateRendering(IAzimuth currentAzimuth,
                                    List<IAzimuth> azimuthsToInvestigate,
                                    TLongObjectMap<IRenderingSegment> renderingResult) {
        IRenderingSegment rendering = renderingResult.get(currentAzimuth.getSegmentFrom().getId());
        if (rendering == null) {
            rendering = new RenderingSegmentImpl();
        }
        rendering.setId(currentAzimuth.getSegmentFrom().getId());
        boolean connectedOnStartNode = currentAzimuth.getSegmentFrom().getFromNodeId() == currentAzimuth.getNodeId();
        //Only consider azimuths from same origin
        for (IAzimuth azimuthToInvestigate : azimuthsToInvestigate) {
            if (currentAzimuth.getSegmentFrom().getId() == azimuthToInvestigate.getSegmentFrom().getId()
                    && currentAzimuth.getSegmentFrom().getFuncRoadClassValue() >= azimuthToInvestigate.getSegmentTo().getFuncRoadClassValue()
                    && azimuthToInvestigate.getSegmentTo().getFuncRoadClassValue() != 0) {
                if (connectedOnStartNode) {
                    rendering.addStartCut(azimuthToInvestigate.getReduceFactor() * -1);
                } else {
                    rendering.addEndCut(azimuthToInvestigate.getReduceFactor());
                }
            }
        }
        renderingResult.put(currentAzimuth.getSegmentFrom().getId(), rendering);
    }

    private IAzimuth calculateAzimuth(long nodeId,ISegment segmentFrom, ISegment segmentTo) {
        double azimuthFrom = 0;
        double azimuthTo = 0;
        if (segmentFrom.getFromNodeId() == nodeId) {
            azimuthFrom = getAzimuth(this.getSecondXCoord(segmentFrom), this.getSecondYCoord(segmentFrom),
                    this.getFirstXCoord(segmentFrom),this.getFirstYCoord(segmentFrom));
        } else if (segmentFrom.getToNodeId() == nodeId) {
            azimuthFrom = getAzimuth(this.getBeforeLastXCoord(segmentFrom),this.getBeforeLastYCoord(segmentFrom),
                    this.getLastXCoord(segmentFrom),this.getLastYCoord(segmentFrom));
        } else {
            log.error("Gip Link has no valid connected node Id");
        }
        if (segmentTo.getFromNodeId() == nodeId) {
            azimuthTo = getAzimuth(this.getFirstXCoord(segmentTo),this.getFirstYCoord(segmentTo),
                    this.getSecondXCoord(segmentTo),this.getSecondYCoord(segmentTo));
        } else if (segmentTo.getToNodeId() == nodeId) {
            azimuthTo = getAzimuth(this.getLastXCoord(segmentTo),this.getLastYCoord(segmentTo),
                    this.getBeforeLastXCoord(segmentTo),this.getBeforeLastYCoord(segmentTo));
        } else {
            log.error("Gip link has no valid connected nodeId");
        }
        IAzimuth azimuth = new AzimuthImpl();
        azimuth.setNodeId(nodeId);
        azimuth.setSegmentFrom(segmentFrom);
        azimuth.setSegmentTo(segmentTo);
        azimuth.setAzimuthFrom(azimuthFrom);
        azimuth.setAzimuthTo(azimuthTo);
        azimuth.setReduceFactor(calculateReduceFactor(azimuth.getAzimuthFrom(), azimuth.getAzimuthTo()));
        return azimuth;
    }

    private int getFirstXCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesX() != null && segment.getCoordinatesX().length > 0) {
            return segment.getCoordinatesX()[0];
        }
        return 0;
    }

    private int getFirstYCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesY() != null && segment.getCoordinatesY().length > 0) {
            return segment.getCoordinatesY()[0];
        }
        return 0;
    }

    private int getSecondXCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesX() != null && segment.getCoordinatesX().length > 1) {
            return segment.getCoordinatesX()[1];
        }
        return 0;
    }

    private int getSecondYCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesY() != null && segment.getCoordinatesY().length > 1) {
            return segment.getCoordinatesY()[1];
        }
        return 0;
    }

    private int getBeforeLastXCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesX() != null && segment.getCoordinatesX().length > 1) {
            return segment.getCoordinatesX()[segment.getCoordinatesX().length - 2];
        }
        return 0;
    }

    private int getBeforeLastYCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesY() != null && segment.getCoordinatesY().length > 1) {
            return segment.getCoordinatesY()[segment.getCoordinatesY().length - 2];
        }
        return 0;
    }

    private int getLastXCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesX() != null && segment.getCoordinatesX().length > 0) {
            return segment.getCoordinatesX()[segment.getCoordinatesX().length - 1];
        }
        return 0;
    }

    private int getLastYCoord(ISegment segment) {
        if (segment != null && segment.getCoordinatesY() != null && segment.getCoordinatesY().length > 0) {
            return segment.getCoordinatesY()[segment.getCoordinatesY().length - 1];
        }
        return 0;
    }

    public static double calculateReduceFactor(double azimuthFrom,double azimuthTo) {
        return Math.tan(((azimuthTo - (azimuthFrom + 2 * Math.PI)) % (2*Math.PI))/2);
    }

    public static double getAzimuth(int startX, int startY, int endX, int endY) {
        //return this.getAzimuth(new Point(startCoordinate),new Point(endCoordinate));
        double dx = endX - startX;
        double dy = endY - startY;
        double alpha = 0;
        if (dx > 0 && dy > 0) {
            alpha = Math.atan(dx/dy);
        } else if (dx > 0 && dy < 0) {
            alpha = Math.PI/2 + Math.atan(Math.abs(dy/dx));
        } else if (dx < 0 && dy < 0) {
            alpha = Math.PI + Math.atan(dx/dy);
        } else if (dx < 0 && dy > 0) {
            alpha = 2 * Math.PI - Math.abs(Math.atan(dx / dy));
        } else if (dx == 0) {
            alpha = dy >= 0 ? 0 : Math.PI;
        } else if (dy == 0) {
            alpha = dx >= 0 ? (Math.PI / 2) : (3 * Math.PI / 2);
        }
        return alpha;
    }

}
