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

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

public class BaseSegment implements IBaseSegment, Serializable {

	private static final long serialVersionUID = -5357759613058650845L;

	protected long id;
	protected Map<String, List<ISegmentXInfo>> xInfo = null;
	protected List<IWaySegmentConnection> cons = new ArrayList<IWaySegmentConnection>();
	
	public BaseSegment() {}
	
	public BaseSegment(long id, Map<String, List<ISegmentXInfo>> xInfo) {
		super();
		this.id = id;
		this.xInfo = xInfo;
	}
		
	public BaseSegment(long id, Map<String, List<ISegmentXInfo>> xInfo,
					   List<IWaySegmentConnection> cons) {
		super();
		this.id = id;
		this.xInfo = xInfo;
		this.cons = cons;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public List<ISegmentXInfo> getXInfo() {
		if (xInfo == null) {
			return null;
		} else {
			List<ISegmentXInfo> xInfoList = new ArrayList<ISegmentXInfo>();
			for (String type : xInfo.keySet()) {
				xInfoList.addAll(xInfo.get(type));
			}
			return xInfoList;
		}
	}

	@Override
	public List<ISegmentXInfo> getXInfo(String type) {
		if (xInfo == null) {
			return null;
		} else {
			return xInfo.get(type);
		}
	}

	@Override
	public void setXInfo(List<ISegmentXInfo> xInfo) {
		this.xInfo = new HashMap<>();
		this.addXInfo(xInfo);
	}

	@Override
	public void addXInfo(ISegmentXInfo xInfo) {
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
	public void addXInfo(List<ISegmentXInfo> xInfo) {
		if (xInfo != null) {
			if (this.xInfo == null) {
				this.xInfo = new HashMap<>();
			}
			for (ISegmentXInfo extInfo : xInfo) {
				String type = extInfo.getXInfoType();
				if (!this.xInfo.containsKey(type)) {
					this.xInfo.put(type, new ArrayList<>());
				}
				// TODO: hinzufügen oder ersetzen?
				this.xInfo.get(type).add(extInfo);
			}
		}
	}

	@Override
	public List<IWaySegmentConnection> getCons() {
		return cons;
	}

	@Override
	public void setCons(List<IWaySegmentConnection> cons) {
		this.cons = cons;
	}

	@Override
	public void addCons(List<IWaySegmentConnection> connections) {
		if (this.cons == null) {
			this.setCons(connections);
		} else {
			this.cons.addAll(connections);
		}
	}

	@Override
	public String toString() {
		return "BaseSegment{" +
				"id=" + id +
				", xInfo=" + xInfo +
				", cons=" + cons +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseSegment that = (BaseSegment) o;

		if (id != that.id) return false;
		if (xInfo != null ? !xInfo.equals(that.xInfo) : that.xInfo != null) return false;
		return cons != null ? cons.equals(that.cons) : that.cons == null;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (xInfo != null ? xInfo.hashCode() : 0);
		result = 31 * result + (cons != null ? cons.hashCode() : 0);
		return result;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// also clones geometry because JTS geometry implements cloneable!
		BaseSegment returnObject = (BaseSegment) super.clone();
		Map<Class<? extends ISegmentXInfo>, List<ISegmentXInfo>> xInfoClones = new HashMap<>();
		if (xInfo != null && !xInfo.isEmpty()) {
			for (Class<? extends ISegmentXInfo> clazz : xInfoClones.keySet()) {
				List<ISegmentXInfo> xiClones = new ArrayList<>(xInfoClones.get(clazz).size());
				for (ISegmentXInfo xi : xInfoClones.get(clazz)) {
					xiClones.add((ISegmentXInfo) xi.clone());
				}
			}
		}
		
		returnObject.setCons(this.getCons());

		return returnObject;
	}

}