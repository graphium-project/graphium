/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.routing.model.impl.RoutingAlgorithms;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;
import at.srfg.graphium.routing.model.impl.RoutingMode;

public interface IRoutingOptions {

	void setCriteria(RoutingCriteria criteria);

	RoutingCriteria getCriteria();
	
	void setAlgorithm(RoutingAlgorithms algorithm);
	
	RoutingAlgorithms getAlgorithm();

	void setTimeout(int timeoutMs);
	
	int getTimeout();

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
	void setRoutingTimestamp(Date timestamp);
	
	Date getRoutingTimestamp();
	
	RoutingMode getMode();

	void setMode(RoutingMode mode);
	
	int getTargetSrid();
	
	void setTargetSrid(int targetSrid);

	void setTagValueFilters(Map<String, Set<Object>> tagValueFilters);

	Map<String, Set<Object>> getTagValueFilters();

	/**
	 * @param searchDistance Optional max. distance for searching segments (unit depends on spatial ref system)
	 */
	void setSearchDistance(double searchDistance);
	
	double getSearchDistance();
	
	void setAdditionalOptions(Map<String, Object> additionalOptions);
	
	Map<String, Object> getAdditionalOptions();
	
}
