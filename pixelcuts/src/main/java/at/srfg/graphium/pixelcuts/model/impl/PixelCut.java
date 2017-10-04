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

import at.srfg.graphium.model.impl.AbstractSegmentXInfo;
import at.srfg.graphium.pixelcuts.model.IPixelCut;

/**
 * @author mwimmer
 *
 */
public class PixelCut extends AbstractSegmentXInfo implements IPixelCut {

    private Long graphVersionId;
	private double startCutRight;
	private double startCutLeft;
	private double endCutRight;
	private double endCutLeft;

	public PixelCut() {
		super("pixelcut");
	}

    public PixelCut(Long segmentId, Long graphVersionId, double startCutRight, double startCutLeft,
			double endCutRight, double endCutLeft) {
    	super("pixelcut");
    	this.segmentId = segmentId;
		this.graphVersionId = graphVersionId;
		this.startCutRight = startCutRight;
		this.startCutLeft = startCutLeft;
		this.endCutRight = endCutRight;
		this.endCutLeft = endCutLeft;
	}

	@Override
    public Long getGraphVersionId() {
		return graphVersionId;
	}

    @Override
	public void setGraphVersionId(Long graphVersionId) {
		this.graphVersionId = graphVersionId;
	}

	@Override
    public double getStartCutRight() {
        return startCutRight;
    }

    @Override
    public double getStartCutLeft() {
        return startCutLeft;
    }

    @Override
    public double getEndCutRight() {
        return endCutRight;
    }

    @Override
    public double getEndCutLeft() {
        return endCutLeft;
    }

    @Override
    public void setStartCutRight(double startCutRight) {
		this.startCutRight = startCutRight;
	}

    @Override
	public void setStartCutLeft(double startCutLeft) {
		this.startCutLeft = startCutLeft;
	}

    @Override
	public void setEndCutRight(double endCutRight) {
		this.endCutRight = endCutRight;
	}

    @Override
	public void setEndCutLeft(double endCutLeft) {
		this.endCutLeft = endCutLeft;
	}

	@Override
    public String toString() {
        return "PixelCut{" +
                "segmentId=" + super.getSegmentId() +
                "graphVersionId=" + (getGraphVersionId() == null ? "null" : getGraphVersionId()) +
                ", startCutRight=" + getStartCutRight() +
                ", startCutLeft=" + getStartCutLeft() +
                ", endCutRight=" + getEndCutRight() +
                ", endCutLeft=" + getEndCutLeft() +
                '}';
    }

	@Override
	public int hashCode() {
		int result = 0;
		long temp;
		if (graphVersionId != null) {
			result = graphVersionId.hashCode();
		}
		temp = Double.doubleToLongBits(startCutRight);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(startCutLeft);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(endCutRight);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(endCutLeft);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}