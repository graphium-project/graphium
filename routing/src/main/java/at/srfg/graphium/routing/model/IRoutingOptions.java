/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.routing.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.vividsolutions.jts.geom.Coordinate;

public interface IRoutingOptions {
	
	List<Coordinate> getCoordinates();
	
	void setCoordinates(List<Coordinate> coordinate);

	void setCriteria(IRoutingCriteria criteria);

	IRoutingCriteria getCriteria();
	
	void setAlgorithm(IRoutingAlgorithm algorithm);
	
	IRoutingAlgorithm getAlgorithm();

	boolean isCancelled();
	
	MutableBoolean getCancellationObject();
	
	void setCancellationObject(MutableBoolean cancellationObject);
	
	void setGraphName(String graphName);

	String getGraphName();

	/**
	 * @param version Graph's version. If set routingTimestamp will be ignored. If not set and routingTimestamp not set the current
	 * Graph's version will be selected for routing.
	 */
	void setGraphVersion(String version);
	
	String getGraphVersion();
	
	/**
	 * @param timestamp Timestamp for selecting the correct graph's version (e.g. timestamp will be in past for historical analysis).
	 * Will be ignored if graphVersion is set.
	 */
	void setRoutingTimestamp(LocalDateTime timestamp);
	
	LocalDateTime getRoutingTimestamp();
	
	IRoutingMode getMode();

	void setMode(IRoutingMode mode);

	void setTagValueFilters(Map<String, Set<Object>> tagValueFilters);

	Map<String, Set<Object>> getTagValueFilters();

	/**
	 * @param searchDistance Optional max. distance for searching segments (unit depends on spatial ref system)
	 */
	void setSearchDistance(double searchDistance);
	
	double getSearchDistance();
	
	void setAdditionalOptions(Map<String, Object> additionalOptions);
	
	Map<String, Object> getAdditionalOptions();

	int getOutputSrid();
	
}
