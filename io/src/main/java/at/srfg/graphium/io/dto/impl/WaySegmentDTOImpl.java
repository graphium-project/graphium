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
package at.srfg.graphium.io.dto.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.model.Access;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vividsolutions.jts.geom.LineString;

/**
 * @author mwimmer
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaySegmentDTOImpl extends BaseWaySegmentDTOImpl implements IWaySegmentDTO {

	protected short maxSpeedTow;	
	protected short maxSpeedBkw;
	protected short calcSpeedTow;
	protected short calcSpeedBkw;
	protected short lanesTow;
	protected short lanesBkw;
	protected short frc;
	protected String formOfWay;
	protected Set<Access> accessTow;
	protected Set<Access> accessBkw;
	protected boolean tunnel;
	protected boolean bridge;
	protected boolean urban;

	public WaySegmentDTOImpl(){ super(); };
	
	public WaySegmentDTOImpl(long id, LineString geometry, String name,
							 short maxSpeedTow, short maxSpeedBkw, short calcSpeedTow,
							 short calcSpeedBkw, short lanesTow, short lanesBkw, short frc, String formOfWay,
							 String streetType, long wayId, long startNodeIndex,
							 long startNodeId, long endNodeIndex, long endNodeId, Set<Access> accessTow,
							 Set<Access> accessBkw, boolean tunnel, boolean bridge, boolean urban,
							 Map<String,String> tags,
							 List<IBaseSegmentConnectionDTO> connection, Map<String,List<ISegmentXInfoDTO>> xInfo) {
		super(id, geometry, name, streetType, wayId, startNodeIndex, startNodeId, endNodeIndex, endNodeId, tags, connection, xInfo);
		this.maxSpeedTow = maxSpeedTow;
		this.maxSpeedBkw = maxSpeedBkw;
		this.calcSpeedTow = calcSpeedTow;
		this.calcSpeedBkw = calcSpeedBkw;
		this.lanesTow = lanesTow;
		this.lanesBkw = lanesBkw;
		this.frc = frc;
		this.formOfWay = formOfWay;
		this.accessTow = accessTow;
		this.accessBkw = accessBkw;
		this.tunnel = tunnel;
		this.bridge = bridge;
		this.urban = urban;
	}

	public short getMaxSpeedTow() {
		return maxSpeedTow;
	}
	public void setMaxSpeedTow(short maxSpeedTow) {
		this.maxSpeedTow = maxSpeedTow;
	}
	public short getMaxSpeedBkw() {
		return maxSpeedBkw;
	}
	public void setMaxSpeedBkw(short maxSpeedBkw) {
		this.maxSpeedBkw = maxSpeedBkw;
	}
	public short getCalcSpeedTow() {
		return calcSpeedTow;
	}
	public void setCalcSpeedTow(short calcSpeedTow) {
		this.calcSpeedTow = calcSpeedTow;
	}
	public short getCalcSpeedBkw() {
		return calcSpeedBkw;
	}
	public void setCalcSpeedBkw(short calcSpeedBkw) {
		this.calcSpeedBkw = calcSpeedBkw;
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
	public short getFrc() {
		return frc;
	}
	public void setFrc(short frc) {
		this.frc = frc;
	}
	public String getFormOfWay() {
		return formOfWay;
	}
	public void setFormOfWay(String formOfWay) {
		this.formOfWay = formOfWay;
	}
	public Set<Access> getAccessTow() {
		return accessTow;
	}
	public void setAccessTow(Set<Access> accessTow) {
		this.accessTow = accessTow;
	}
	public Set<Access> getAccessBkw() {
		return accessBkw;
	}
	public void setAccessBkw(Set<Access> accessBkw) {
		this.accessBkw = accessBkw;
	}
	public boolean isTunnel() {
		return tunnel;
	}
	public void setTunnel(boolean tunnel) {
		this.tunnel = tunnel;
	}
	public boolean isBridge() {
		return bridge;
	}
	public void setBridge(boolean bridge) {
		this.bridge = bridge;
	}
	public boolean isUrban() {
		return urban;
	}
	public void setUrban(boolean urban) {
		this.urban = urban;
	}
	
}