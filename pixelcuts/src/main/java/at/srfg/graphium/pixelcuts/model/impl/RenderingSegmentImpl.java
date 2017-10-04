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

import at.srfg.graphium.pixelcuts.model.IRenderingSegment;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Created by shennebe on 27.04.2015.
 */
public class RenderingSegmentImpl implements IRenderingSegment {

    public RenderingSegmentImpl() {
	}

    private long id;
	private TDoubleArrayList startCuts = new TDoubleArrayList();
    private TDoubleArrayList endCuts = new TDoubleArrayList();

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public double getStartCutRight() {
        return this.startCuts.isEmpty() ? 0 : this.startCuts.max();
    }

    @Override
    public double getStartCutLeft() {
        return this.startCuts.isEmpty() ? 0 :this.startCuts.min();
    }

    @Override
    public double getEndCutRight() {
        return this.endCuts.isEmpty() ? 0 :this.endCuts.max();
    }

    @Override
    public double getEndCutLeft() {
        return this.endCuts.isEmpty() ? 0 : this.endCuts.min();
    }

    @Override
    public void addStartCut(double startCut) {
        this.startCuts.add(startCut);
    }

    @Override
    public void addEndCut(double endCut) {
        this.endCuts.add(endCut);
    }

    @Override
    public String toString() {
        return "GipRenderingImpl{" +
                "gipLinkId=" + id +
                ", startCutRight=" + getStartCutRight() +
                ", startCutLeft=" + getStartCutLeft() +
                ", endCutRight=" + getEndCutRight() +
                ", endCutLeft=" + getEndCutLeft() +
                '}';
    }
}
