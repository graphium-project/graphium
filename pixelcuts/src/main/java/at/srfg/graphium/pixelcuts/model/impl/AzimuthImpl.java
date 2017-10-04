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
package at.srfg.graphium.pixelcuts.model.impl;


import at.srfg.graphium.pixelcuts.model.IAzimuth;
import at.srfg.graphium.pixelcuts.model.ISegment;

/**
 * Created by shennebe on 27.04.2015.
 */
public class AzimuthImpl implements IAzimuth {

    private long nodeId;
    private ISegment segmentFrom;
    private ISegment segmentTo;
    private double azimuthFrom;
    private double azimuthTo;
    private double reduceFactor;

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public ISegment getSegmentFrom() {
        return segmentFrom;
    }

    @Override
    public void setSegmentFrom(ISegment segmentFrom) {
        this.segmentFrom = segmentFrom;
    }

    @Override
    public ISegment getSegmentTo() {
        return segmentTo;
    }

    @Override
    public void setSegmentTo(ISegment segmentTo) {
        this.segmentTo = segmentTo;
    }

    @Override
    public double getAzimuthFrom() {
        return azimuthFrom;
    }

    @Override
    public void setAzimuthFrom(double azimuthFrom) {
        this.azimuthFrom = azimuthFrom;
    }

    @Override
    public double getAzimuthTo() {
        return azimuthTo;
    }

    @Override
    public void setAzimuthTo(double azimuthTo) {
        this.azimuthTo = azimuthTo;
    }

    @Override
    public double getReduceFactor() {
        return reduceFactor;
    }

    @Override
    public void setReduceFactor(double reduceFactor) {
        this.reduceFactor = reduceFactor;
    }

    @Override
    public String toString() {
        return "GipAzimuthImpl{" +
                "gipLinkFrom=" + segmentFrom +
                ", gipLinkTo=" + segmentTo +
                ", azimuthFrom=" + azimuthFrom +
                ", azimuthTo=" + azimuthTo +
                ", reduceFactor=" + reduceFactor +
                '}';
    }
}
