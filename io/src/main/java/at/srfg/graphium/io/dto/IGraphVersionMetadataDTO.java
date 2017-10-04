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
package at.srfg.graphium.io.dto;

import java.util.Map;
import java.util.Set;

/**
 * @author mwimmer
 *
 */
// TODO: add all properties required to be identical with old version in genericJacksonWayGraphOutputFormat
public interface IGraphVersionMetadataDTO {

	public abstract long getId();

	public abstract void setId(long id);

	public abstract String getGraphName();

	public abstract void setGraphName(String graphName);

	public abstract String getVersion();

	public abstract void setVersion(String version);

	public abstract String getOriginGraphName();

	public abstract void setOriginGraphName(String originGraphName);

	public abstract String getOriginVersion();

	public abstract void setOriginVersion(String originVersion);

	public abstract String getState();

	public abstract void setState(String state);

	public abstract Long getValidFrom();

	public abstract void setValidFrom(Long validFrom);

	public abstract Long getValidTo();

	public abstract void setValidTo(Long validTo);

	public abstract String getCoveredArea();

	public abstract void setCoveredArea(String coveredArea);

	public abstract int getSegmentsCount();

	public abstract void setSegmentsCount(int segmentsCount);

	public abstract int getConnectionsCount();

	public abstract void setConnectionsCount(int connectionsCount);

	public abstract Set<String> getAccessTypes();

	public abstract void setAccessTypes(Set<String> accessTypes);

	public abstract Map<String, String> getTags();

	public abstract void setTags(Map<String, String> tags);

	public abstract String getSource();

	public abstract void setSource(String source);

	public abstract String getType();

	public abstract void setType(String type);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract Long getCreationTimestamp();

	public abstract void setCreationTimestamp(Long creationTimestamp);

	public abstract Long getStorageTimestamp();

	public abstract void setStorageTimestamp(Long storageTimestamp);

	public abstract String getCreator();

	public abstract void setCreator(String creator);

	public abstract String getOriginUrl();

	public abstract void setOriginUrl(String originUrl);

}