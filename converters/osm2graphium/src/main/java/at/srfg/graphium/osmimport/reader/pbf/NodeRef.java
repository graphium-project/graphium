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

import gnu.trove.list.TLongList;

/**
 * @author mwimmer
 *
 */
public class NodeRef {
	
	private TLongList wayIds;
	/**
	 * 0 = normal node
	 * 1 = end node
	 * 2 = potentially segmentation node
	 */
	private byte type;
	
	public NodeRef(TLongList wayIds, byte type) {
		super();
		this.wayIds = wayIds;
		this.type = type;
	}
	
	public TLongList getWayIds() {
		return wayIds;
	}
	public void setWayIds(TLongList wayIds) {
		this.wayIds = wayIds;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	
}