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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author mwimmer
 *
 */
@JsonRootName(value = "graphVersionMetadata")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphVersionMetadataDTOImpl implements IGraphVersionMetadataDTO {

	protected long id;
	protected String graphName;
	protected String version;
	protected String originGraphName;
	protected String originVersion;
	protected String state;
	protected Long validFrom;
	protected Long validTo;
	protected String coveredArea;
	protected int segmentsCount;
	protected int connectionsCount;
	protected Set<String> accessTypes;
	
	protected Map<String,String> tags = new HashMap<String, String>();
	
	protected String source;
	protected String type;
	protected String description;
	protected Long creationTimestamp;
	protected Long storageTimestamp;
	protected String creator;
	protected String originUrl;

	/**
	 * Default constructor needed for deserialization
	 */
	public GraphVersionMetadataDTOImpl() {
	}

	public GraphVersionMetadataDTOImpl(long id, String graphName,
									   String version, String originGraphName, String originVersion,
									   String state, Long validFrom, Long validTo, String coveredArea,
									   int segmentsCount, int connectionsCount, Set<String> accessTypes,
									   Map<String, String> tags, String source, String type,
									   String description, long creationTimestamp, long storageTimestamp,
									   String creator, String originUrl) {
		super();
		this.id = id;
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getGraphName() {
		return graphName;
	}
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getOriginGraphName() {
		return originGraphName;
	}
	public void setOriginGraphName(String originGraphName) {
		this.originGraphName = originGraphName;
	}
	public String getOriginVersion() {
		return originVersion;
	}
	public void setOriginVersion(String originVersion) {
		this.originVersion = originVersion;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Long validFrom) {
		this.validFrom = validFrom;
	}
	public Long getValidTo() {
		return validTo;
	}
	public void setValidTo(Long validTo) {
		this.validTo = validTo;
	}
	public String getCoveredArea() {
		return coveredArea;
	}
	public void setCoveredArea(String coveredArea) {
		this.coveredArea = coveredArea;
	}
	public int getSegmentsCount() {
		return segmentsCount;
	}
	public void setSegmentsCount(int segmentsCount) {
		this.segmentsCount = segmentsCount;
	}
	public int getConnectionsCount() {
		return connectionsCount;
	}
	public void setConnectionsCount(int connectionsCount) {
		this.connectionsCount = connectionsCount;
	}
	public Set<String> getAccessTypes() {
		return accessTypes;
	}
	public void setAccessTypes(Set<String> accessTypes) {
		this.accessTypes = accessTypes;
	}
	public Map<String, String> getTags() {
		return tags;
	}
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public Long getStorageTimestamp() {
		return storageTimestamp;
	}
	public void setStorageTimestamp(Long storageTimestamp) {
		this.storageTimestamp = storageTimestamp;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getOriginUrl() {
		return originUrl;
	}
	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GraphVersionMetadataDTOImpl that = (GraphVersionMetadataDTOImpl) o;

		if (id != that.id) return false;
		if (segmentsCount != that.segmentsCount) return false;
		if (connectionsCount != that.connectionsCount) return false;
		if (graphName != null ? !graphName.equals(that.graphName) : that.graphName != null) return false;
		if (version != null ? !version.equals(that.version) : that.version != null) return false;
		if (originGraphName != null ? !originGraphName.equals(that.originGraphName) : that.originGraphName != null)
			return false;
		if (originVersion != null ? !originVersion.equals(that.originVersion) : that.originVersion != null)
			return false;
		if (state != null ? !state.equals(that.state) : that.state != null) return false;
		if (validFrom != null ? !validFrom.equals(that.validFrom) : that.validFrom != null) return false;
		if (validTo != null ? !validTo.equals(that.validTo) : that.validTo != null) return false;
		if (coveredArea != null ? !coveredArea.equals(that.coveredArea) : that.coveredArea != null) return false;
		if (accessTypes != null ? !accessTypes.equals(that.accessTypes) : that.accessTypes != null) return false;
		if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
		if (source != null ? !source.equals(that.source) : that.source != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (creationTimestamp != null ? !creationTimestamp.equals(that.creationTimestamp) : that.creationTimestamp != null)
			return false;
		if (storageTimestamp != null ? !storageTimestamp.equals(that.storageTimestamp) : that.storageTimestamp != null)
			return false;
		if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
		return originUrl != null ? originUrl.equals(that.originUrl) : that.originUrl == null;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (graphName != null ? graphName.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (originGraphName != null ? originGraphName.hashCode() : 0);
		result = 31 * result + (originVersion != null ? originVersion.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
		result = 31 * result + (validTo != null ? validTo.hashCode() : 0);
		result = 31 * result + (coveredArea != null ? coveredArea.hashCode() : 0);
		result = 31 * result + segmentsCount;
		result = 31 * result + connectionsCount;
		result = 31 * result + (accessTypes != null ? accessTypes.hashCode() : 0);
		result = 31 * result + (tags != null ? tags.hashCode() : 0);
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (creationTimestamp != null ? creationTimestamp.hashCode() : 0);
		result = 31 * result + (storageTimestamp != null ? storageTimestamp.hashCode() : 0);
		result = 31 * result + (creator != null ? creator.hashCode() : 0);
		result = 31 * result + (originUrl != null ? originUrl.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GraphVersionMetadataDTOImpl{" +
				"id=" + id +
				", graphName='" + graphName + '\'' +
				", version='" + version + '\'' +
				", originGraphName='" + originGraphName + '\'' +
				", originVersion='" + originVersion + '\'' +
				", state='" + state + '\'' +
				", validFrom=" + validFrom +
				", validTo=" + validTo +
				", coveredArea='" + coveredArea + '\'' +
				", segmentsCount=" + segmentsCount +
				", connectionsCount=" + connectionsCount +
				", accessTypes=" + accessTypes +
				", tags=" + tags +
				", source='" + source + '\'' +
				", type='" + type + '\'' +
				", description='" + description + '\'' +
				", creationTimestamp=" + creationTimestamp +
				", storageTimestamp=" + storageTimestamp +
				", creator='" + creator + '\'' +
				", originUrl='" + originUrl + '\'' +
				'}';
	}
}
