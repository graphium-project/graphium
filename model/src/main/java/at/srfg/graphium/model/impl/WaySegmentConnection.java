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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

public class WaySegmentConnection implements IWaySegmentConnection, Serializable {

	private static final long serialVersionUID = -9129902769554345468L;

	protected long nodeId;
	protected long fromSegmentId;
	protected long toSegmentId;
	protected Set<Access> access;
	protected Map<String, List<IConnectionXInfo>> xInfo;
	
	public WaySegmentConnection() {}
	
	public WaySegmentConnection(long nodeId, long fromSegmentId,
			long toSegmentId, Set<Access> access) {
		super();
		this.nodeId = nodeId;
		this.fromSegmentId = fromSegmentId;
		this.toSegmentId = toSegmentId;
		this.access = access;
	}

	public WaySegmentConnection(long nodeId, long fromSegmentId, long toSegmentId, Set<Access> access,
			Map<String, List<IConnectionXInfo>> xInfo) {
		super();
		this.nodeId = nodeId;
		this.fromSegmentId = fromSegmentId;
		this.toSegmentId = toSegmentId;
		this.access = access;
		this.xInfo = xInfo;
	}

	@Override
	public long getNodeId() {
		return nodeId;
	}
	@Override
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	@Override
	public long getFromSegmentId() {
		return fromSegmentId;
	}
	@Override
	public void setFromSegmentId(long fromSegmentId) {
		this.fromSegmentId = fromSegmentId;
	}
	@Override
	public long getToSegmentId() {
		return toSegmentId;
	}
	@Override
	public void setToSegmentId(long toSegmentId) {
		this.toSegmentId = toSegmentId;
	}
	@Override
	public Set<Access> getAccess() {
		return access;
	}
	@Override
	public void setAccess(Set<Access> access) {
		this.access = access;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (fromSegmentId ^ (fromSegmentId >>> 32));
		result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
		result = prime * result + (int) (toSegmentId ^ (toSegmentId >>> 32));
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
		WaySegmentConnection other = (WaySegmentConnection) obj;
		if (fromSegmentId != other.fromSegmentId)
			return false;
		if (nodeId != other.nodeId)
			return false;
		if (toSegmentId != other.toSegmentId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WaySegmentConnection [nodeId=" + nodeId + ", fromSegmentId="
				+ fromSegmentId + ", toSegmentId=" + toSegmentId + ", access="
				+ access + "]";
	}

	@Override
	public List<IConnectionXInfo> getXInfo() {
		if (xInfo == null) {
			return null;
		} else {
			final List<IConnectionXInfo> xInfoList = new ArrayList<>();
			xInfo.forEach((key,valueList) -> {
				xInfoList.addAll(valueList);
			});
			return xInfoList;
		}
	}

	@Override
	public List<IConnectionXInfo> getXInfo(String type) {
		if (xInfo == null) {
			return null;
		} else {
			return xInfo.get(type);
		}
	}

	@Override
	public void setXInfo(List<IConnectionXInfo> xInfo) {
		this.xInfo = new HashMap<>();
		this.addXInfo(xInfo);
	}

	@Override
	public void addXInfo(IConnectionXInfo xInfo) {
		if (xInfo != null) {
			if (this.xInfo == null) {
				this.xInfo = new HashMap<>();
			}
			String type = xInfo.getXInfoType();
			if (!this.xInfo.containsKey(type)) {
				this.xInfo.put(type, new ArrayList<>());
			}
			// TODO: hinzufügen oder ersetzen?
			this.xInfo.get(type).add(xInfo);
		}
	}

	@Override
	public void addXInfo(List<IConnectionXInfo> xInfo) {
		if (xInfo != null) {
			if (this.xInfo == null) {
				this.xInfo = new HashMap<>();
			}
			for (IConnectionXInfo extInfo : xInfo) {
				String type = extInfo.getXInfoType();
				if (!this.xInfo.containsKey(type)) {
					this.xInfo.put(type, new ArrayList<>());
				}
				// TODO: hinzufügen oder ersetzen?
				this.xInfo.get(type).add(extInfo);
			}
		}
	}

}
