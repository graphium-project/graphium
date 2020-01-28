/**
 * Graphium - Module of Graphium for routing services 
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.model.IRoutingAlgorithm;
import at.srfg.graphium.routing.model.IRoutingCriteria;
import at.srfg.graphium.routing.model.IRoutingMode;
import at.srfg.graphium.routing.model.IRoutingOptions;

public class RoutingOptionsImpl implements IRoutingOptions, Serializable {

	private static final long serialVersionUID = 6972227240039471500L;
	
	public static final int DEFAULT_SRID = 4326; 
	// default search distance (15 meter)
	// TODO: define in meters instead of projected value
	public static final double DEFAULT_SEARCH_DISTANCE = 0.0000904776810466969;
	
	// mandatory params
	private String graphName;
	private String graphVersion;
	List<Coordinate> coordinates;

	// optional params (with defaults)
	private LocalDate routingTimestamp;
	private IRoutingAlgorithm algorithm;
	private IRoutingCriteria criteria;
	private IRoutingMode mode;
	private int outputSrid = 4326;
	// TODO: Was war das nochmal
	private Map<String, Set<Object>> tagValueFilters;
	private double searchDistance;
	private int timeout;
	private Map<String, Object> additionalOptions;

	public RoutingOptionsImpl(String graphName, String graphVersion, List<Coordinate> coordinates) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.coordinates = coordinates;
		this.routingTimestamp = LocalDate.now();
		this.algorithm = RoutingAlgorithms.BIDIRECTIONAL_DIJKSTRA;
		this.criteria = RoutingCriteria.LENGTH;
		this.mode = RoutingMode.CAR;
		this.outputSrid = DEFAULT_SRID;
		this.timeout = -1;
		this.tagValueFilters = new HashMap<>();
		this.additionalOptions = new HashMap<>();		
		this.searchDistance = DEFAULT_SEARCH_DISTANCE;
	}
	
	@Override
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
	
	@Override
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
	
	@Override
	public void setCriteria(IRoutingCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public IRoutingCriteria getCriteria() {
		return criteria;
	}

	@Override
	public IRoutingAlgorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public void setAlgorithm(IRoutingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public IRoutingMode getMode() {
		return mode;
	}

	@Override
	public void setMode(IRoutingMode mode) {
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
	public LocalDate getRoutingTimestamp() {
		return routingTimestamp;
	}

	@Override
	public void setRoutingTimestamp(LocalDate routingTimestamp) {
		this.routingTimestamp = routingTimestamp;
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

	@Override
	public void setTimeout(int timeoutMs) {
		this.timeout = timeoutMs;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public void setAdditionalOptions(Map<String, Object> additionalOptions) {
		this.additionalOptions = additionalOptions;
	}

	@Override
	public Map<String, Object> getAdditionalOptions() {
		return additionalOptions;
	}
	
	/**
	 * for use in spring caching with spring el for key generation 
	 * @param options
	 * @param startUri
	 * @param endUri
	 * @return
	 */
	// TODO: overwork
	/*public static int generateCacheKey(IRoutingOptions options, String startUri, String endUri) {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(options.getGraphName());
		builder.append(options.getMode());
		builder.append(options.getCriteria());
		builder.append(startUri);
		builder.append(endUri);
		return builder.hashCode();
	}*/

	@Override
	public String toString() {
		return "RoutingOptionsImpl [graphName=" + graphName + ", graphVersion=" + graphVersion + ", routingTimestamp="
				+ routingTimestamp + ", algorithm=" + algorithm + ", criteria=" + criteria + ", mode=" + mode
				+ ", outputSrid=" + outputSrid + ", tagValueFilters=" + tagValueFilters + ", searchDistance="
				+ searchDistance + "]";
	}

	
}