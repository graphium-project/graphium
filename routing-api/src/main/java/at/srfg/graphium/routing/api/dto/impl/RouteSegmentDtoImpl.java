/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.api.dto.impl;

/**
 * @author mwimmer
 *
 */
public class RouteSegmentDtoImpl {
	
	private long segmentId;
	private String geometry;
	private float length;
	private int duration;
	private short maxSpeed;
	
	public RouteSegmentDtoImpl(long segmentId, String geometry, float length, int duration, short maxSpeed) {
		super();
		this.segmentId = segmentId;
		this.geometry = geometry;
		this.length = length;
		this.duration = duration;
		this.maxSpeed = maxSpeed;
	}
	
	public long getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(long segmentId) {
		this.segmentId = segmentId;
	}
	public String getGeometry() {
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public float getLength() {
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public short getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(short maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

}
