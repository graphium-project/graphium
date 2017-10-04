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
/**
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.tutorial.xinfo.model.impl;

import at.srfg.graphium.model.impl.AbstractSegmentXInfo;
import at.srfg.graphium.tutorial.xinfo.model.IRoadDamage;

/**
 * @author mwimmer
 */
public class RoadDamageImpl extends AbstractSegmentXInfo implements IRoadDamage {

	private static final String xInfoType = "roaddamage";
	
	/**
	 * id of referenced graph version
	 */
	private long graphVersionId = 0;
	/**
	 * start offset on segment geometry in road damage object direction
	 */
	private float startOffset = 0;
	/**
	 * end offset on segment geometry in road damage object direction
	 */
	private float endOffset = 1;
	/**
	 * type of road damage
	 */
	private String type;
	
	public RoadDamageImpl() {
		super(xInfoType);
	}
	
	public RoadDamageImpl(long segmentId, long graphVersionId, Boolean directionTow, float startOffset, float endOffset, String type) {
		super(xInfoType);
		this.segmentId = segmentId;
		this.graphVersionId = graphVersionId;
		this.directionTow = directionTow;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.type = type;
	}
	
	public long getGraphVersionId() {
		return graphVersionId;
	}
	public void setGraphVersionId(long graphVersionId) {
		this.graphVersionId = graphVersionId;
	}
	public float getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(float startOffset) {
		this.startOffset = startOffset;
	}
	public float getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(float endOffset) {
		this.endOffset = endOffset;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directionTow == null) ? 0 : directionTow.hashCode());
		result = prime * result + (int) (segmentId ^ (segmentId >>> 32));
		result = prime * result + (int) (graphVersionId ^ (graphVersionId >>> 32));
		result = prime * result + ((xInfoType == null) ? 0 : xInfoType.hashCode());
		result = prime * result + Float.floatToIntBits(endOffset);
		result = prime * result + Float.floatToIntBits(startOffset);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RoadDamageImpl other = (RoadDamageImpl) obj;
		if (directionTow == null) {
			if (other.directionTow != null)
				return false;
		} else if (!directionTow.equals(other.directionTow))
			return false;
		if (graphVersionId != other.graphVersionId)
			return false;
		if (segmentId != other.segmentId)
			return false;
		if (xInfoType == null) {
			if (other.xInfoType != null)
				return false;
		} else if (!xInfoType.equals(other.xInfoType))
			return false;
		if (Float.floatToIntBits(endOffset) != Float.floatToIntBits(other.endOffset))
			return false;
		if (Float.floatToIntBits(startOffset) != Float.floatToIntBits(other.startOffset))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RoadDamageImpl [graphVersionId=" + graphVersionId + ", startOffset=" + startOffset + ", endOffset=" + endOffset + ", type=" + type
				+ ", directionTow=" + directionTow + "]";
	}
	
}
