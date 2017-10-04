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

import com.vividsolutions.jts.geom.LineString;

/**
 * @author mwimmer
 *
 */
public interface IBaseWaySegmentDTO extends IBaseSegmentDTO {

	LineString getGeometry();

	void setGeometry(LineString geometry);

	String getName();

	void setName(String name);

	String getStreetType();

	void setStreetType(String streetType);

	long getWayId();

	void setWayId(long wayId);

	long getStartNodeIndex();

	void setStartNodeIndex(long startNodeIndex);

	long getStartNodeId();

	void setStartNodeId(long startNodeId);

	long getEndNodeIndex();

	void setEndNodeIndex(long endNodeIndex);

	long getEndNodeId();

	void setEndNodeId(long endNodeId);
	
	Map<String, String> getTags();
	
	void setTags(Map<String, String> tags);
		
}