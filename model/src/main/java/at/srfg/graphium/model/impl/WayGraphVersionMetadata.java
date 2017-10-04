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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

public class WayGraphVersionMetadata implements IWayGraphVersionMetadata {

	protected long id;
	protected long graphId;
	protected String graphName;
	protected String version;
	protected String originGraphName;
	protected String originVersion;
	protected State state;
	protected Date validFrom;
	protected Date validTo;
	protected Polygon coveredArea;
	protected int segmentsCount;
	protected int connectionsCount;
	protected Set<Access> accessTypes;
	protected Map<String,String> tags = new HashMap<String, String>();
	protected ISource source;
	protected String type;
	protected String description;
	protected Date creationTimestamp;
	protected Date storageTimestamp;
	protected String creator;
	protected String originUrl;
	
	public WayGraphVersionMetadata() {}
	
	public WayGraphVersionMetadata(long id, long graphId, String graphName, String version,
			String originGraphName, String originVersion, State state, 
			Date validFrom, Date validTo, Polygon coveredArea,
			int segmentsCount, int connectionsCount, Set<Access> accessTypes,
			Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl) {
		super();
		this.id = id;
		this.graphId = graphId;
		this.graphName = graphName;
		this.version = version;
		this.originGraphName = originGraphName;
		this.originVersion = originVersion;
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.coveredArea = coveredArea;
		this.segmentsCount = segmentsCount;
		this.connectionsCount = connectionsCount;
		this.accessTypes = accessTypes;
		this.tags = tags;
		this.source = source;
		this.type = type;
		this.description = description;
		this.creationTimestamp = creationTimestamp;
		this.storageTimestamp = storageTimestamp;
		this.creator = creator;
		this.originUrl = originUrl;
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
	public long getGraphId() {
		return graphId;
	}

	@Override
	public void setGraphId(long graphId) {
		this.graphId = graphId;
	}

	@Override
	public String getGraphName() {
		return graphName;
	}

	@Override
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getOriginGraphName() {
		return originGraphName;
	}

	@Override
	public void setOriginGraphName(String originGraphName) {
		this.originGraphName = originGraphName;
	}

	@Override
	public String getOriginVersion() {
		return originVersion;
	}

	@Override
	public void setOriginVersion(String originVersion) {
		this.originVersion = originVersion;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	@Override
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidTo() {
		return validTo;
	}

	@Override
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@Override
	public Polygon getCoveredArea() {
		return coveredArea;
	}

	@Override
	public void setCoveredArea(Polygon coveredArea) {
		this.coveredArea = coveredArea;
	}

	@Override
	public int getSegmentsCount() {
		return segmentsCount;
	}

	@Override
	public void setSegmentsCount(int segmentsCount) {
		this.segmentsCount = segmentsCount;
	}

	@Override
	public int getConnectionsCount() {
		return connectionsCount;
	}

	@Override
	public void setConnectionsCount(int connectionsCount) {
		this.connectionsCount = connectionsCount;
	}

	@Override
	public Set<Access> getAccessTypes() {
		return accessTypes;
	}

	@Override
	public void setAccessTypes(Set<Access> accessTypes) {
		this.accessTypes = accessTypes;
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
	public ISource getSource() {
		return source;
	}

	@Override
	public void setSource(ISource source) {
		this.source = source;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	@Override
	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Override
	public Date getStorageTimestamp() {
		return storageTimestamp;
	}

	@Override
	public void setStorageTimestamp(Date storageTimestamp) {
		this.storageTimestamp = storageTimestamp;
	}

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getOriginUrl() {
		return originUrl;
	}

	@Override
	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		WayGraphVersionMetadata other = (WayGraphVersionMetadata) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WayGraphVersionMetainfo [id=" + id + ", graphId=" + graphId + ", graphName=" + graphName
				+ ", version=" + version + ", originGraphName="
				+ originGraphName + ", originVersion=" + originVersion
				+ ", state=" + state
				+ ", validFrom=" + validFrom + ", validTo=" + validTo
				+ ", coveredArea=" + coveredArea + ", segmentsCount="
				+ segmentsCount + ", connectionsCount=" + connectionsCount
				+ ", accessTypes=" + accessTypes + ", tags=" + tags
				+ ", source=" + source + ", type=" + type + ", description="
				+ description + ", creationTimestamp=" + creationTimestamp
				+ ", storageTimestamp=" + storageTimestamp + ", creator="
				+ creator + ", originUrl=" + originUrl + "]";
	}

}