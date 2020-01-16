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
package at.srfg.graphium.routing.api.adapter.impl;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.dto.impl.RouteDtoImpl;

/**
 * @author mwimmer
 *
 */
public class Route2RouteDTOAdapter<T extends IWaySegment> implements IAdapter<RouteDtoImpl<T>, IRoute<T>> {

	@Override
	public RouteDtoImpl<T> adapt(IRoute<T> route) {
		MultiLineString geom = computeRouteGeom(route);
		return new RouteDtoImpl<>(
				route.getLength(), 
				route.getDuration(), 
				route.getPath(), 
				route.getRuntimeInMs(), 
				route.getGraphName(), 
				route.getGraphVersion(), 
				(geom == null ? null : geom.toText()));
	}
	
	private MultiLineString computeRouteGeom(IRoute<T> route) {
		if (route.getSegments() != null && !route.getSegments().isEmpty()) {
			
			LineString[] lineStrings = new LineString[route.getSegments().size()];
			for (int i = 0; i < route.getSegments().size(); i++) {
				lineStrings[i] = route.getSegments().get(i).getGeometry();
			}
			return new MultiLineString(lineStrings, lineStrings[0].getFactory());
		}
		return null;
	}


}
