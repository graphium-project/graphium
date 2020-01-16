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

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRouteModelFactory;

public class RouteModelFactoryImpl extends GenericRouteModelFactory<IWaySegment> implements IRouteModelFactory<IWaySegment> {

	public RouteModelFactoryImpl() {
		super();
	}

	@Override
	public IRoute<IWaySegment> newRoute() {	
		return new RouteImpl<IWaySegment>();
	}

}
