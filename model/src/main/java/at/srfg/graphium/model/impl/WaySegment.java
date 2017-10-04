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
package at.srfg.graphium.model.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.OneWay;

public class WaySegment extends BaseWaySegment implements IWaySegment, Serializable {

	private static final long serialVersionUID = -2601898807733166259L;

	protected short maxSpeedTow;
	protected short maxSpeedBkw;
	protected Short speedCalcTow;
	protected Short speedCalcBkw;
	protected short lanesTow;
	protected short lanesBkw;
	protected FuncRoadClass frc;
    protected FormOfWay formOfWay;
	protected Set<Access> accessTow;
	protected Set<Access> accessBkw;
	protected Boolean tunnel;
	protected Boolean bridge;
	protected Boolean urban;
	protected Date timestamp;
	
	public WaySegment() {}
	
	public WaySegment(long id, LineString geometry, float length, String name,
			short maxSpeedTow, short maxSpeedBkw, Short speedCalcTow,
			Short speedCalcBkw, short lanesTow, short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay,
			String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			Set<Access> accessTow, Set<Access> accessBkw, Boolean tunnel, Boolean bridge, Boolean urban,
			Date timestamp, List<IWaySegmentConnection> cons, Map<String, String> tags,
			Map<String, List<ISegmentXInfo>> xInfo) {
		super(id, geometry, length, name, streetType, wayId, startNodeId, startNodeIndex, 
				endNodeId, endNodeIndex, cons, tags, xInfo);
		this.maxSpeedTow = maxSpeedTow;
		this.maxSpeedBkw = maxSpeedBkw;
		this.speedCalcTow = speedCalcTow;
		this.speedCalcBkw = speedCalcBkw;
		this.lanesTow = lanesTow;
		this.lanesBkw = lanesBkw;
		this.frc = frc;
		this.formOfWay = formOfWay;
		this.accessTow = accessTow;
		this.accessBkw = accessBkw;
		this.tunnel = tunnel;
		this.bridge = bridge;
		this.urban = urban;
		this.timestamp = timestamp;
	}

	@Override
	public short getMaxSpeedTow() {
		return maxSpeedTow;
	}

	@Override
	public void setMaxSpeedTow(short maxSpeedTow) {
		this.maxSpeedTow = maxSpeedTow;
	}

	@Override
	public short getMaxSpeedBkw() {
		return maxSpeedBkw;
	}

	@Override
	public void setMaxSpeedBkw(short maxSpeedBkw) {
		this.maxSpeedBkw = maxSpeedBkw;
	}

	@Override
	public Short getSpeedCalcTow() {
		return speedCalcTow;
	}

	@Override
	public void setSpeedCalcTow(Short speedCalcTow) {
		this.speedCalcTow = speedCalcTow;
	}

	@Override
	public Short getSpeedCalcBkw() {
		return speedCalcBkw;
	}

	@Override
	public void setSpeedCalcBkw(Short speedCalcBkw) {
		this.speedCalcBkw = speedCalcBkw;
	}

	@Override
	public FuncRoadClass getFrc() {
		return frc;
	}

	@Override
	public void setFrc(FuncRoadClass frc) {
		this.frc = frc;
	}

	@Override
	public FormOfWay getFormOfWay() {
		return formOfWay;
	}

	@Override
	public void setFormOfWay(FormOfWay formOfWay) {
		this.formOfWay = formOfWay;
	}

	@Override
	public Set<Access> getAccessTow() {
		return accessTow;
	}

	@Override
	public void setAccessTow(Set<Access> accessTow) {
		this.accessTow = accessTow;
	}

	@Override
	public Set<Access> getAccessBkw() {
		return accessBkw;
	}

	@Override
	public void setAccessBkw(Set<Access> accessBkw) {
		this.accessBkw = accessBkw;
	}

	@Override
	public Boolean isTunnel() {
		return tunnel;
	}

	@Override
	public void setTunnel(Boolean tunnel) {
		this.tunnel = tunnel;
	}

	@Override
	public Boolean isBridge() {
		return bridge;
	}

	@Override
	public void setBridge(Boolean bridge) {
		this.bridge = bridge;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public List<IWaySegmentConnection> getStartNodeCons() {
		return this.cons == null ? null : this.cons.stream().filter(iWaySegmentConnection -> iWaySegmentConnection.getNodeId() == this.startNodeId).collect(Collectors.toList());
		// ;-)
	}

	@Override
	public List<IWaySegmentConnection> getEndNodeCons() {
		return this.cons == null ? null : this.cons.stream().filter(iWaySegmentConnection -> iWaySegmentConnection.getNodeId() == this.endNodeId).collect(Collectors.toList());
		// ;-))))
	}

	@Override
	public void setStartNodeCons(List<IWaySegmentConnection> startNodeCons) {
		if (startNodeCons != null) {
			if (this.cons == null) {
				super.setCons(startNodeCons);
			} else if (this.cons.stream().allMatch(iWaySegmentConnection -> iWaySegmentConnection.getNodeId() == this.endNodeId)) {
				this.cons.addAll(startNodeCons);
			} else {
				List<IWaySegmentConnection> endNodeCons = this.getEndNodeCons();
				endNodeCons.addAll(startNodeCons);
				this.cons = endNodeCons;
			}
		}
	}

	@Override
	public void setEndNodeCons(List<IWaySegmentConnection> endNodeCons) {
		if (endNodeCons != null) {
			//Case 1: the backing array is empty, so simply SET it
			if (this.cons == null) {
				super.setCons(endNodeCons);
			//Case 2: all nodes of the backing array are from the type startNodeCons so imply ADD it
			} else if (this.cons.stream().allMatch(iWaySegmentConnection -> iWaySegmentConnection.getNodeId() == this.startNodeId)) {
				this.cons.addAll(endNodeCons);
			//Case 3: only start nodes are considered and the end nodes are added.
			} else {
				List<IWaySegmentConnection> startNodeCons = this.getStartNodeCons();
				startNodeCons.addAll(endNodeCons);
				this.cons = startNodeCons;
			}
		}
	}

	public short getLanesTow() {
		return lanesTow;
	}

	public void setLanesTow(short lanesTow) {
		this.lanesTow = lanesTow;
	}

	public short getLanesBkw() {
		return lanesBkw;
	}

	public void setLanesBkw(short lanesBkw) {
		this.lanesBkw = lanesBkw;
	}

	public Boolean isUrban() {
		return urban;
	}

	public void setUrban(Boolean urban) {
		this.urban = urban;
	}

	@Override
	public int getDuration(boolean directionTow) {
		if (directionTow) {
			return calcDuration((speedCalcTow != null && speedCalcTow > 0 ? speedCalcTow : maxSpeedTow));
		} else {
			return calcDuration((speedCalcBkw != null && speedCalcBkw > 0 ? speedCalcBkw : maxSpeedBkw));
		}
	}

	@Override
	public int getMinDuration(boolean directionTow) {
		if (directionTow) {
			return calcDuration(maxSpeedTow);
		} else {
			return calcDuration(maxSpeedBkw);
		}
	}
	
	private int calcDuration(short speed) {
		return (int) Math.round(length / (speed / 3.6));
	}

	@Override
	public OneWay isOneway() {
		if ((accessTow != null && accessTow.contains(Access.PRIVATE_CAR)) && (accessBkw == null || !accessBkw.contains(Access.PRIVATE_CAR))) {
			return OneWay.ONEWAY_TOW;
		} else if ((accessTow == null || !accessTow.contains(Access.PRIVATE_CAR)) && (accessBkw != null && accessBkw.contains(Access.PRIVATE_CAR))) {
			return OneWay.ONEWAY_BKW;
		} else {
			return OneWay.NO_ONEWAY;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		WaySegment other = (WaySegment) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		try {
			// also clones geometry because JTS geometry implement cloneable!
			WaySegment returnObject = (WaySegment) super.clone();
			returnObject.setCons(this.getCons());
			return returnObject;
		} catch (CloneNotSupportedException e) {
			
		}
		return null;
	}

	@Override
	public String toString() {
		return "WaySegment{" +
				"id=" + id +
				", geometry=" + geometry +
				", length=" + length +
				", name='" + name + '\'' +
				", streetType='" + streetType + '\'' +
				", wayId=" + wayId +
				", startNodeId=" + startNodeId +
				", startNodeIndex=" + startNodeIndex +
				", endNodeId=" + endNodeId +
				", endNodeIndex=" + endNodeIndex +
				", tags=" + tags +
				", maxSpeedTow=" + maxSpeedTow +
				", maxSpeedBkw=" + maxSpeedBkw +
				", speedCalcTow=" + speedCalcTow +
				", speedCalcBkw=" + speedCalcBkw +
				", lanesTow=" + lanesTow +
				", lanesBkw=" + lanesBkw +
				", frc=" + frc +
				", formOfWay=" + formOfWay +
				", accessTow=" + accessTow +
				", accessBkw=" + accessBkw +
				", tunnel=" + tunnel +
				", bridge=" + bridge +
				", urban=" + urban +
				", timestamp=" + timestamp +
				'}';
	}
}