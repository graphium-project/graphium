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

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.exception.RoutingException;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRouteModelFactory;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.IRoutingOptionsFactory;

public abstract class GenericRouteModelFactory<T extends IWaySegment> implements IRouteModelFactory<T>, IRoutingOptionsFactory {

	private static Logger log = LoggerFactory.getLogger(GenericRouteModelFactory.class);

	@Override
	public abstract IRoute<T> newRoute();
	
	@Override
	public IRoutingOptions newRoutingOptions() {
		return new RoutingOptionsImpl();
	}

	@Override
	public IRoutingOptions createRoutingOptions(String graphName, String graphVersion, Date routingTimestamp, String routingMode,
			String routingCriteria) throws RoutingException {
		return createRoutingOptions(graphName, graphVersion, routingTimestamp, routingMode, routingCriteria, RoutingAlgorithms.ASTAR.name());
	}
	
	@Override
	public IRoutingOptions createRoutingOptions(String graphName, String graphVersion, Date routingTimestamp, String routingMode,
			String routingCriteria, String routingAlgorithm) throws RoutingException {
		RoutingCriteria criteria = RoutingCriteria.fromValue(routingCriteria);
		RoutingMode mode = RoutingMode.fromValue(routingMode);
		RoutingAlgorithms algo = RoutingAlgorithms.valueOf(routingAlgorithm);
		if (criteria == null) {
			String msg = routingCriteria + " is not a valid routing criteria";
			log.warn(msg);
			throw new RoutingException(msg);
		}
		if (mode == null) {
			String msg = routingMode + " is not a valid routing mode";
			log.warn(msg);
			throw new RoutingException(msg);
		}
		IRoutingOptions options = new RoutingOptionsImpl(graphName, graphVersion, routingTimestamp, algo, criteria, mode, 0, null, 0);
		return options;
	}	
	
	@Override
	public IRoutingOptions createRoutingOptions(String graphName, String graphVersion, Date routingTimestamp, String routingMode,
			String routingCriteria, Map<String, Set<Object>> segmentTagBlacklist) throws RoutingException {
		IRoutingOptions options = createRoutingOptions(graphName, graphVersion, routingTimestamp, routingMode, routingCriteria);
		options.setTagValueFilters(segmentTagBlacklist);
		return options;
	}	
	
}