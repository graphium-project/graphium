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
package at.srfg.graphium.osmimport.reader.pbf;

/**
 * @author mwimmer
 *
 */
public class WayRef {

	private long wayId;
	/**
	 * 0 = end node
	 * 1 = normal node
	 * 2 = potentially segmentation node
	 */
	private byte type;
	/**
	 * 0 = no oneway
	 * 1 = oneway from start to end node
	 * 2 = reverse oneway from end to start node
	 */
	private byte oneway;
	private long startNodeId;
	private long endNodeId;
	
	public WayRef(long wayId, byte type, byte oneway, long startNodeId, long endNodeId) {
		super();
		this.wayId = wayId;
		this.type = type;
		this.oneway = oneway;
		this.startNodeId = startNodeId;
		this.endNodeId = endNodeId;
	}

	public long getWayId() {
		return wayId;
	}

	public void setWayId(long wayId) {
		this.wayId = wayId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getOneway() {
		return oneway;
	}

	public void setOneway(byte oneway) {
		this.oneway = oneway;
	}

	public long getStartNodeId() {
		return startNodeId;
	}

	public void setStartNodeId(long startNodeId) {
		this.startNodeId = startNodeId;
	}

	public long getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(long endNodeId) {
		this.endNodeId = endNodeId;
	}
	
}