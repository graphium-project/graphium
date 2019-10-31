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
package at.srfg.graphium.model.impl;

import java.util.HashMap;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.model.OneWay;

public class HDWaySegment extends WaySegment implements IHDWaySegment {

	private static final long serialVersionUID = 70164823153042629L;
	
	private LineString leftBoarderGeometry;
	private long leftBoarderStartNodeId;
	private long leftBoarderEndNodeId;
	private LineString rightBoarderGeometry;
	private long rightBoarderStartNodeId;
	private long rightBoarderEndNodeId;
	
	public HDWaySegment() {
		tags = new HashMap<>();
	}
	
	public HDWaySegment(LineString leftBoarderGeometry, long leftBoarderStartNodeId, long leftBoarderEndNodeId,
			LineString rightBoarderGeometry, long rightBoarderStartNodeId, long rightBoarderEndNodeId) {
		super();
		this.leftBoarderGeometry = leftBoarderGeometry;
		this.leftBoarderStartNodeId = leftBoarderStartNodeId;
		this.leftBoarderEndNodeId = leftBoarderEndNodeId;
		this.rightBoarderGeometry = rightBoarderGeometry;
		this.rightBoarderStartNodeId = rightBoarderStartNodeId;
		this.rightBoarderEndNodeId = rightBoarderEndNodeId;
	}

	@Override
	public LineString getLeftBoarderGeometry() {
		return leftBoarderGeometry;
	}
	
	@Override
	public void setLeftBoarderGeometry(LineString geometry) {
		leftBoarderGeometry = geometry;
	}
	
	@Override
	public long getLeftBoarderStartNodeId() {
		return leftBoarderStartNodeId;
	}
	
	@Override
	public void setLeftBoarderStartNodeId(long startNodeId) {
		leftBoarderStartNodeId = startNodeId;
	}
	
	@Override
	public long getLeftBoarderEndNodeId() {
		return leftBoarderEndNodeId;
	}
	
	@Override
	public void setLeftBoarderEndNodeId(long endNodeId) {
		leftBoarderEndNodeId = endNodeId;
	}
	
	@Override
	public LineString getRightBoarderGeometry() {
		return rightBoarderGeometry;
	}
	
	@Override
	public void setRightBoarderGeometry(LineString geometry) {
		rightBoarderGeometry = geometry;
	}
	
	@Override
	public long getRightBoarderStartNodeId() {
		return rightBoarderStartNodeId;
	}
	
	@Override
	public void setRightBoarderStartNodeId(long startNodeId) {
		rightBoarderStartNodeId = startNodeId;
	}
	
	@Override
	public long getRightBoarderEndNodeId() {
		return rightBoarderEndNodeId;
	}
	
	@Override
	public void setRightBoarderEndNodeId(long endNodeId) {
		rightBoarderEndNodeId = endNodeId;
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
		result = prime * result + (int) (leftBoarderEndNodeId ^ (leftBoarderEndNodeId >>> 32));
		result = prime * result + (int) (leftBoarderStartNodeId ^ (leftBoarderStartNodeId >>> 32));
		result = prime * result + (int) (rightBoarderEndNodeId ^ (rightBoarderEndNodeId >>> 32));
		result = prime * result + (int) (rightBoarderStartNodeId ^ (rightBoarderStartNodeId >>> 32));
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
		if (leftBoarderEndNodeId != other.leftBoarderEndNodeId)
			return false;
		if (leftBoarderStartNodeId != other.leftBoarderStartNodeId)
			return false;
		if (rightBoarderEndNodeId != other.rightBoarderEndNodeId)
			return false;
		if (rightBoarderStartNodeId != other.rightBoarderStartNodeId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Lanelet [leftBoarderGeometry=" + leftBoarderGeometry + ", leftBoarderStartNodeId="
				+ leftBoarderStartNodeId + ", leftBoarderEndNodeId=" + leftBoarderEndNodeId + ", rightBoarderGeometry="
				+ rightBoarderGeometry + ", rightBoarderStartNodeId=" + rightBoarderStartNodeId
				+ ", rightBoarderEndNodeId=" + rightBoarderEndNodeId + ", maxSpeedTow="
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