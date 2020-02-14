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
package at.srfg.graphium.routing.model.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;

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
	private MutableBoolean cancellationObject;
	private Map<String, Object> additionalOptions;

	public RoutingOptionsImpl(String graphName, String graphVersion) {
		this.graphName = graphName;
		this.graphVersion = graphVersion;
		this.routingTimestamp = LocalDate.now();
		this.algorithm = RoutingAlgorithms.BIDIRECTIONAL_DIJKSTRA;
		this.criteria = RoutingCriteria.LENGTH;
		this.mode = RoutingMode.CAR;
		this.outputSrid = DEFAULT_SRID;
		this.tagValueFilters = new HashMap<>();
		this.additionalOptions = new HashMap<>();		
		this.searchDistance = DEFAULT_SEARCH_DISTANCE;
	}

	public RoutingOptionsImpl(String graphName, String graphVersion, MutableBoolean cancellationObject) {
		this(graphName, graphVersion);
		this.cancellationObject = cancellationObject;
	}
	
	public RoutingOptionsImpl(String graphName, String graphVersion, List<Coordinate> coordinates, MutableBoolean cancellationObject) {
		this(graphName, graphVersion, cancellationObject);
		this.coordinates = coordinates;
	}
	
	public RoutingOptionsImpl(String graphName, String graphVersion, List<Coordinate> coordinates) {
		this(graphName, graphVersion);
		this.coordinates = coordinates;
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
	public void setAdditionalOptions(Map<String, Object> additionalOptions) {
		this.additionalOptions = additionalOptions;
	}

	@Override
	public Map<String, Object> getAdditionalOptions() {
		return additionalOptions;
	}
	
	@Override
	public int getOutputSrid() {
		return outputSrid;
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

	@Override
	public boolean isCancelled() {
		if (cancellationObject != null) {
			return cancellationObject.booleanValue();
		} else {
			return false;
		}
	}
	
	@Override
	public MutableBoolean getCancellationObject() {
		return cancellationObject;
	}

	@Override
	public void setCancellationObject(MutableBoolean cancellationObject) {
		this.cancellationObject = cancellationObject;
	}
	
}