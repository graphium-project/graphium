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
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

public class BaseWaySegment extends BaseSegment implements IBaseWaySegment, Serializable {

	private static final long serialVersionUID = 2289711257656355322L;

	protected LineString geometry;
	protected float length = -1;
	protected String name;
	protected String streetType;
	protected long wayId;
	protected long startNodeId;
	protected int startNodeIndex;
	protected long endNodeId;
	protected int endNodeIndex;	
	protected Map<String, String> tags;
	
	public BaseWaySegment() {}
	
	public BaseWaySegment(long id, LineString geometry, float length, String name, String streetType, long wayId,
			long startNodeId, int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> cons,
			Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo) {
		super(id, xInfo,cons);
		this.geometry = geometry;
		this.length = length;
		this.name = name;
		this.streetType = streetType;
		this.wayId = wayId;
		this.startNodeId = startNodeId;
		this.startNodeIndex = startNodeIndex;
		this.endNodeId = endNodeId;
		this.endNodeIndex = endNodeIndex;
		this.tags = tags;
		this.xInfo = xInfo;
	}
	
	public LineString getGeometry() {
		return geometry;
	}
	public void setGeometry(LineString geometry) {
		this.geometry = geometry;
	}
	public float getLength() {
		if (length == 0) {
			length = (float) geometry.getLength();
		}
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreetType() {
		return streetType;
	}
	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}
	public long getWayId() {
		return wayId;
	}
	public void setWayId(long wayId) {
		this.wayId = wayId;
	}
	public long getStartNodeId() {
		return startNodeId;
	}
	public void setStartNodeId(long startNodeId) {
		this.startNodeId = startNodeId;
	}
	public int getStartNodeIndex() {
		return startNodeIndex;
	}
	public void setStartNodeIndex(int startNodeIndex) {
		this.startNodeIndex = startNodeIndex;
	}
	public long getEndNodeId() {
		return endNodeId;
	}
	public void setEndNodeId(long endNodeId) {
		this.endNodeId = endNodeId;
	}
	public int getEndNodeIndex() {
		return endNodeIndex;
	}
	public void setEndNodeIndex(int endNodeIndex) {
		this.endNodeIndex = endNodeIndex;
	}
	public Map<String, String> getTags() {
		return tags;
	}
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}


	@Override
	public String toString() {
		return "BaseWaySegment{" +
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
				'}';
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return (WaySegment) super.clone();
	}

}