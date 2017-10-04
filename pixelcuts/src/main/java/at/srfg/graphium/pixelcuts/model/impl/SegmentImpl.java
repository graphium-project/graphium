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

import java.util.Arrays;

import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.pixelcuts.model.ISegment;

/**
 * @author mwimmer
 *
 */
public class SegmentImpl implements ISegment {

	private long id;
	private long fromNodeId;
	private long toNodeId;
	private int[] coordinatesX;
	private int[] coordinatesY;
	private short funcRoadClass;
	
	public SegmentImpl() {}
	
	public SegmentImpl(long id, long fromNodeId, long toNodeId, int[] coordinatesX, int[] coordinatesY,
			short funcRoadClass) {
		super();
		this.id = id;
		this.fromNodeId = fromNodeId;
		this.toNodeId = toNodeId;
		this.coordinatesX = coordinatesX;
		this.coordinatesY = coordinatesY;
		this.funcRoadClass = funcRoadClass;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getFromNodeId() {
		return fromNodeId;
	}
	
	public void setFromNodeId(long fromNodeId) {
		this.fromNodeId = fromNodeId;
	}
	
	public long getToNodeId() {
		return toNodeId;
	}
	
	public void setToNodeId(long toNodeId) {
		this.toNodeId = toNodeId;
	}
	
	public int[] getCoordinatesX() {
		return coordinatesX;
	}
	
	public void setCoordinatesX(int[] coordinatesX) {
		this.coordinatesX = coordinatesX;
	}
	
	public int[] getCoordinatesY() {
		return coordinatesY;
	}
	
	public void setCoordinatesY(int[] coordinatesY) {
		this.coordinatesY = coordinatesY;
	}
	
	public FuncRoadClass getFuncRoadClass() {
		return FuncRoadClass.getFuncRoadClassForValue(funcRoadClass);
	}

	public short getFuncRoadClassValue() {
		return funcRoadClass;
	}

	public void setFuncRoadClassValue(short funcRoadClass) {
		this.funcRoadClass = funcRoadClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fromNodeId ^ (fromNodeId >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (toNodeId ^ (toNodeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SegmentImpl other = (SegmentImpl) obj;
		if (fromNodeId != other.fromNodeId)
			return false;
		if (id != other.id)
			return false;
		if (toNodeId != other.toNodeId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SegmentImpl [id=" + id + ", fromNodeId=" + fromNodeId + ", toNodeId=" + toNodeId + ", coordinatesX="
				+ Arrays.toString(coordinatesX) + ", coordinatesY=" + Arrays.toString(coordinatesY) + ", funcRoadClass="
				+ funcRoadClass + "]";
	}

}
