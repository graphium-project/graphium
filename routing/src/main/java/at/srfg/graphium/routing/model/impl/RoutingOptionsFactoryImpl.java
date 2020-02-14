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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.util.MultiValueMap;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.IRoutingOptionsFactory;

public class RoutingOptionsFactoryImpl implements IRoutingOptionsFactory<IRoutingOptions> {

	private final static String COORD_PAIR_SEPERATOR = ";";
	private final static String COORD_SEPERATOR = ",";
	
	public final static String PARAM_ROUTING_MODE = "mode";
	public final static String PARAM_ROUTING_ALGO = "algo";
	public final static String PARAM_ROUTING_CRITERIA = "criteria";
	public final static String PARAM_TIMEOUT = "timeout";
	public final static String PARAM_TIMESTAMP = "time";
	// (required?)
	public final static String PARAM_TAG_VALUE_FILTERS = "tagFilters";
	
	protected double defaultSearchDistance = RoutingOptionsImpl.DEFAULT_SEARCH_DISTANCE;

	@Override
	public IRoutingOptions newRoutingOptions(String graphName, String graphVersion,
			String coordsString, MultiValueMap<String, String> allParams, MutableBoolean cancellationObject) 
					throws RoutingParameterException {
		IRoutingOptions options = new RoutingOptionsImpl(graphName, graphVersion, parseCoordsString(coordsString), cancellationObject);
		
		if(allParams.containsKey(PARAM_ROUTING_MODE)) {
			options.setMode(RoutingMode.fromValue(allParams.getFirst(PARAM_ROUTING_MODE)));
		}
		if(allParams.containsKey(PARAM_ROUTING_ALGO)) {
			options.setAlgorithm(RoutingAlgorithms.fromValue(allParams.getFirst(PARAM_ROUTING_ALGO)));
		}
		if(allParams.containsKey(PARAM_ROUTING_CRITERIA)) {
			options.setCriteria(RoutingCriteria.fromValue(allParams.getFirst(PARAM_ROUTING_CRITERIA)));
		}
		if(allParams.containsKey(PARAM_TIMESTAMP)) {
			options.setRoutingTimestamp(toRoutingTimestamp(allParams.getFirst(PARAM_TIMESTAMP)));
		}
		
		options.setSearchDistance(defaultSearchDistance);
		return options;
	}

	private LocalDate toRoutingTimestamp(String timestampString) throws RoutingParameterException  {
		LocalDate date;
		// opt. 1 - Unix Timestamp
		if(NumberUtils.isNumber(timestampString)) {
			date = Instant.ofEpochMilli(Long.parseLong(timestampString)).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		// opt. 2 - try parse ISO-Date-Time Format
		else {
			try {
				date = LocalDate.parse(timestampString, DateTimeFormatter.ISO_DATE_TIME);
			} catch(DateTimeParseException e) {
				throw new RoutingParameterException("routing timestamp " + timestampString + " not parsable", e);
			}
		}
		return date;
	}

	private int toTimeout(String timeoutString) throws RoutingParameterException {
		try {
			return Integer.parseInt(timeoutString);
		} catch(NumberFormatException e) {
			throw new RoutingParameterException("timout paramater not parsabel to number", e);
		}
	}

	private List<Coordinate> parseCoordsString(String coordsString) throws RoutingParameterException {
		try { 
			String[] pairs = coordsString.split(COORD_PAIR_SEPERATOR);
			String[] coordString;
			List<Coordinate> coords = new ArrayList<Coordinate>();
			for(String pair : pairs) {
				coordString = pair.split(COORD_SEPERATOR);			
				coords.add(new Coordinate(coordToNumber(coordString[0]), coordToNumber(coordString[1])));
			}
			return coords;
		} catch (Exception e) {
			throw new RoutingParameterException("error parsing coordinates", e);
		}
		
	}

	private double coordToNumber(String coordString) {
		return Double.parseDouble(coordString);
	}

	public double getDefaultSearchDistance() {
		return defaultSearchDistance;
	}

	public void setDefaultSearchDistance(double defaultSearchDistance) {
		this.defaultSearchDistance = defaultSearchDistance;
	}
	
}
