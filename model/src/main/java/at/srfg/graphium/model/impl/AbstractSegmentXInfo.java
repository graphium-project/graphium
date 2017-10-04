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

import at.srfg.graphium.model.ISegmentXInfo;

public abstract class AbstractSegmentXInfo implements ISegmentXInfo {

	protected long segmentId;
	protected Boolean directionTow;
	protected String xInfoType;
	private String groupKey;

	public AbstractSegmentXInfo(String xInfoType) {
		this.xInfoType = xInfoType;
	}

	@Override
	public long getSegmentId() {
		return segmentId;
	}

	@Override
	public void setSegmentId(long segmentId) {
		this.segmentId = segmentId;
	}

	@Override
	public Boolean isDirectionTow() {
		return directionTow;
	}

	@Override
	public void setDirectionTow(Boolean directionTow) {
		this.directionTow = directionTow;
	}

    @Override
    public String getXInfoType() {
        return this.xInfoType;
    }

	@Override
	public String getGroupKey() {
		if (groupKey == null) {
			return Integer.toHexString(hashCode());
		}
		return groupKey;
	}

	@Override
	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public abstract int hashCode();
}
