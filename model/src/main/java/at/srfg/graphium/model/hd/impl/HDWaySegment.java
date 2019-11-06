/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model.hd.impl;

import java.util.HashMap;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.OneWay;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.impl.WaySegment;

public class HDWaySegment extends WaySegment implements IHDWaySegment {

	private static final long serialVersionUID = 70164823153042629L;
	
	private LineString leftBorderGeometry;
	private long leftBorderStartNodeId;
	private long leftBorderEndNodeId;
	private LineString rightBorderGeometry;
	private long rightBorderStartNodeId;
	private long rightBorderEndNodeId;
	
	public HDWaySegment() {
		tags = new HashMap<>();
	}
	
	public HDWaySegment(LineString leftBorderGeometry, long leftBorderStartNodeId, long leftBorderEndNodeId,
			LineString rightBorderGeometry, long rightBorderStartNodeId, long rightBorderEndNodeId) {
		super();
		this.leftBorderGeometry = leftBorderGeometry;
		this.leftBorderStartNodeId = leftBorderStartNodeId;
		this.leftBorderEndNodeId = leftBorderEndNodeId;
		this.rightBorderGeometry = rightBorderGeometry;
		this.rightBorderStartNodeId = rightBorderStartNodeId;
		this.rightBorderEndNodeId = rightBorderEndNodeId;
	}

	@Override
	public LineString getLeftBorderGeometry() {
		return leftBorderGeometry;
	}
	
	@Override
	public void setLeftBorderGeometry(LineString geometry) {
		leftBorderGeometry = geometry;
	}
	
	@Override
	public long getLeftBorderStartNodeId() {
		return leftBorderStartNodeId;
	}
	
	@Override
	public void setLeftBorderStartNodeId(long startNodeId) {
		leftBorderStartNodeId = startNodeId;
	}
	
	@Override
	public long getLeftBorderEndNodeId() {
		return leftBorderEndNodeId;
	}
	
	@Override
	public void setLeftBorderEndNodeId(long endNodeId) {
		leftBorderEndNodeId = endNodeId;
	}
	
	@Override
	public LineString getRightBorderGeometry() {
		return rightBorderGeometry;
	}
	
	@Override
	public void setRightBorderGeometry(LineString geometry) {
		rightBorderGeometry = geometry;
	}
	
	@Override
	public long getRightBorderStartNodeId() {
		return rightBorderStartNodeId;
	}
	
	@Override
	public void setRightBorderStartNodeId(long startNodeId) {
		rightBorderStartNodeId = startNodeId;
	}
	
	@Override
	public long getRightBorderEndNodeId() {
		return rightBorderEndNodeId;
	}
	
	@Override
	public void setRightBorderEndNodeId(long endNodeId) {
		rightBorderEndNodeId = endNodeId;
	}
	
	@Override
	public OneWay isOneway() {
		if (accessBkw != null && !accessBkw.isEmpty()) {
			return OneWay.NO_ONEWAY;
		} else {
			return OneWay.ONEWAY_TOW; // default
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (leftBorderEndNodeId ^ (leftBorderEndNodeId >>> 32));
		result = prime * result + (int) (leftBorderStartNodeId ^ (leftBorderStartNodeId >>> 32));
		result = prime * result + (int) (rightBorderEndNodeId ^ (rightBorderEndNodeId >>> 32));
		result = prime * result + (int) (rightBorderStartNodeId ^ (rightBorderStartNodeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HDWaySegment other = (HDWaySegment) obj;
		if (leftBorderEndNodeId != other.leftBorderEndNodeId)
			return false;
		if (leftBorderStartNodeId != other.leftBorderStartNodeId)
			return false;
		if (rightBorderEndNodeId != other.rightBorderEndNodeId)
			return false;
		if (rightBorderStartNodeId != other.rightBorderStartNodeId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Lanelet [leftBorderGeometry=" + leftBorderGeometry + ", leftBorderStartNodeId="
				+ leftBorderStartNodeId + ", leftBorderEndNodeId=" + leftBorderEndNodeId + ", rightBorderGeometry="
				+ rightBorderGeometry + ", rightBorderStartNodeId=" + rightBorderStartNodeId
				+ ", rightBorderEndNodeId=" + rightBorderEndNodeId + ", maxSpeedTow="
				+ maxSpeedTow + ", maxSpeedBkw=" + maxSpeedBkw + ", speedCalcTow=" + speedCalcTow + ", speedCalcBkw="
				+ speedCalcBkw + ", lanesTow=" + lanesTow + ", lanesBkw=" + lanesBkw + ", frc=" + frc + ", formOfWay="
				+ formOfWay + ", accessTow=" + accessTow + ", accessBkw=" + accessBkw + ", tunnel=" + tunnel
				+ ", bridge=" + bridge + ", urban=" + urban + ", timestamp=" + timestamp + ", geometry=" + geometry
				+ ", length=" + length + ", name=" + name + ", streetType=" + streetType + ", wayId=" + wayId
				+ ", startNodeId=" + startNodeId + ", startNodeIndex=" + startNodeIndex + ", endNodeId=" + endNodeId
				+ ", endNodeIndex=" + endNodeIndex + ", tags=" + tags + ", id=" + id + ", xInfo=" + xInfo + ", cons="
				+ cons + "]";
	}
	
}