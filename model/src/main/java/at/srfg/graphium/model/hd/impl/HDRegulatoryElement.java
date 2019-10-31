/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model.hd.impl;

import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.model.hd.HDRegulatoryElementType;
import at.srfg.graphium.model.hd.IHDRegulatoryElement;
import at.srfg.graphium.model.impl.AbstractSegmentXInfo;

public class HDRegulatoryElement extends AbstractSegmentXInfo implements IHDRegulatoryElement {

	private static final String xInfoType = "laneletRegulatoryElement";
	
	private long id;
    private Long graphVersionId;
	// TODO: kann hier der groupKey verwendet werden???
    private HDRegulatoryElementType type;
	private Geometry geometry;
	private Map<String, String> tags;
	
	public HDRegulatoryElement() {
		super(xInfoType);
	}

	public HDRegulatoryElement(long id, Long graphVersionId, HDRegulatoryElementType type,
			Geometry geometry, Map<String, String> tags) {
		super(xInfoType);
		this.id = id;
		this.graphVersionId = graphVersionId;
		this.type = type;
		this.geometry = geometry;
		this.tags = tags;
	}

	@Override
	public HDRegulatoryElementType getType() {
		return type;
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
	public Long getGraphVersionId() {
		return graphVersionId;
	}

	@Override
	public void setGraphVersionId(Long graphVersionId) {
		this.graphVersionId = graphVersionId;
	}

	@Override
	public void setType(HDRegulatoryElementType type) {
		this.type = type;
	}

	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public Map<String, String> getTags() {
		return tags;
	}

	@Override
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (segmentId ^ (segmentId >>> 32));
		result = prime * result + (int) (graphVersionId ^ (graphVersionId >>> 32));
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
		HDRegulatoryElement other = (HDRegulatoryElement) obj;
		if (id != other.id)
			return false;
		if (segmentId != other.segmentId)
			return false;
		if (graphVersionId != other.graphVersionId)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LaneletRegulatoryElement [id=" + id + ", graphVersionId=" + graphVersionId + ", type=" + type
				+ ", geometry=" + geometry + ", tags=" + tags + ", segmentId=" + segmentId + "]";
	}
	
}
