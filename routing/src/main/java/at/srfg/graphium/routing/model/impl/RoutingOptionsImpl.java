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
package at.srfg.graphium.routing.model.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import at.srfg.graphium.routing.model.IRoutingOptions;

public class RoutingOptionsImpl implements IRoutingOptions, Serializable {

	private static final long serialVersionUID = 6972227240039471500L;
	private String graphName;
	private String graphVersion;
	private Date routingTimestamp;
	private RoutingAlgorithms algorithm;
	private RoutingCriteria criteria;
	private RoutingMode mode;
	private int targetSrid = 4326;
	private Map<String, Set<Object>> tagValueFilters;
	private double searchDistance;

	public RoutingOptionsImpl() {}
	
	public RoutingOptionsImpl(String graphName, String graphVersion,
			RoutingCriteria criteria, RoutingMode mode) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.algorithm = RoutingAlgorithms.ASTAR;
		this.criteria = criteria;
		this.mode = mode;
	}
	
	public RoutingOptionsImpl(String graphName, String graphVersion,
			RoutingAlgorithms algorithm, RoutingCriteria criteria, RoutingMode mode) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.algorithm = algorithm;
		this.criteria = criteria;
		this.mode = mode;
	}
	
	public RoutingOptionsImpl(String graphName, String graphVersion, 
			RoutingAlgorithms algorithm, RoutingCriteria criteria,
			RoutingMode mode, int targetSrid) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.algorithm = algorithm;
		this.criteria = criteria;
		this.mode = mode;
		this.targetSrid = targetSrid;
	}
	
	public RoutingOptionsImpl(String graphName, String graphVersion, Date routingTimestamp,
			RoutingAlgorithms algorithm, RoutingCriteria criteria, RoutingMode mode, 
			int targetSrid, Map<String, Set<Object>> tagValueFilters, double searchDistance) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.routingTimestamp = routingTimestamp;
		this.algorithm = algorithm;
		this.criteria = criteria;
		this.mode = mode;
		this.targetSrid = targetSrid;
		this.tagValueFilters = tagValueFilters;
		this.searchDistance = searchDistance;
	}
	
	@Override
	public void setCriteria(RoutingCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public RoutingCriteria getCriteria() {
		return criteria;
	}

	@Override
	public RoutingAlgorithms getAlgorithm() {
		return algorithm;
	}

	@Override
	public void setAlgorithm(RoutingAlgorithms algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public RoutingMode getMode() {
		return mode;
	}

	@Override
	public void setMode(RoutingMode mode) {
		this.mode = mode;
	}
	
	@Override
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	@Override
	public String getGraphName() {
		return graphName;
	}

	@Override
	public void setGraphVersion(String version) {
		this.graphVersion = version;
	}

	@Override
	public String getGraphVersion() {
		return graphVersion;
	}

	@Override
	public Date getRoutingTimestamp() {
		return routingTimestamp;
	}

	@Override
	public void setRoutingTimestamp(Date routingTimestamp) {
		this.routingTimestamp = routingTimestamp;
	}

	@Override
	public int getTargetSrid() {	
		return targetSrid;
	}

	@Override
	public void setTargetSrid(int targetSrid) {
		this.targetSrid = targetSrid;
	}
	
	@Override
	public void setTagValueFilters(Map<String, Set<Object>> tagValueFilters) {
		this.tagValueFilters = tagValueFilters;
	}

	@Override
	public Map<String, Set<Object>> getTagValueFilters() {
		return tagValueFilters;
	}
	
	@Override
	public double getSearchDistance() {
		return searchDistance;
	}

	@Override
	public void setSearchDistance(double searchDistance) {
		this.searchDistance = searchDistance;
	}

	/**
	 * for use in spring caching with spring el for key generation 
	 * @param options
	 * @param startUri
	 * @param endUri
	 * @return
	 */
	public static int generateCacheKey(IRoutingOptions options, String startUri, String endUri) {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(options.getGraphName());
		builder.append(options.getMode());
		builder.append(options.getCriteria());
		builder.append(options.getTargetSrid());
		builder.append(startUri);
		builder.append(endUri);
		return builder.hashCode();
	}

	@Override
	public String toString() {
		return "RoutingOptionsImpl [graphName=" + graphName + ", graphVersion=" + graphVersion + ", routingTimestamp="
				+ routingTimestamp + ", algorithm=" + algorithm + ", criteria=" + criteria + ", mode=" + mode
				+ ", targetSrid=" + targetSrid + ", tagValueFilters=" + tagValueFilters + ", searchDistance="
				+ searchDistance + "]";
	}
	
}