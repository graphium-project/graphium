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
package at.srfg.graphium.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

public interface IWayGraphVersionMetadata {

	public abstract long getId();

	public abstract void setId(long id);

	public abstract long getGraphId();

	public abstract void setGraphId(long graphId);

	public abstract String getGraphName();

	public abstract void setGraphName(String graphName);

	public abstract String getVersion();

	public abstract void setVersion(String version);

	public abstract String getOriginGraphName();

	public abstract void setOriginGraphName(String originGraphName);

	public abstract String getOriginVersion();

	public abstract void setOriginVersion(String originVersion);

	public abstract State getState();

	public abstract void setState(State state);

	public abstract Date getValidFrom();

	public abstract void setValidFrom(Date validFrom);

	public abstract Date getValidTo();

	public abstract void setValidTo(Date validTo);

	public abstract Polygon getCoveredArea();

	public abstract void setCoveredArea(Polygon coveredArea);

	public abstract int getSegmentsCount();

	public abstract void setSegmentsCount(int segmentsCount);

	public abstract int getConnectionsCount();

	public abstract void setConnectionsCount(int connectionsCount);

	public abstract Set<Access> getAccessTypes();

	public abstract void setAccessTypes(Set<Access> accessTypes);

	public abstract Map<String, String> getTags();

	public abstract void setTags(Map<String, String> tags);

	public abstract ISource getSource();

	public abstract void setSource(ISource source);

	public abstract String getType();

	public abstract void setType(String type);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract Date getCreationTimestamp();

	public abstract void setCreationTimestamp(Date creationTimestamp);

	public abstract Date getStorageTimestamp();

	public abstract void setStorageTimestamp(Date storageTimestamp);

	public abstract String getCreator();

	public abstract void setCreator(String creator);

	public abstract String getOriginUrl();

	public abstract void setOriginUrl(String originUrl);

}