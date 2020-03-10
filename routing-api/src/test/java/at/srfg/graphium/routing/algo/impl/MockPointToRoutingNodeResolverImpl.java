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
package at.srfg.graphium.routing.algo.impl;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.routing.algo.IPointToRoutingNodeResolver;

/**
 * @author mwimmer
 *
 */
public class MockPointToRoutingNodeResolverImpl implements IPointToRoutingNodeResolver<LineString> {

	@Override
	public LineString resolveSegment(Point point, double searchDistance, String graphName, String graphVersion) {
		Coordinate pointCoord = point.getCoordinate();
		Coordinate[] coords = new Coordinate[2];

		coords[0] = new Coordinate(pointCoord.x - 0.01, pointCoord.y);
		coords[1] = new Coordinate(pointCoord.x + 0.01, pointCoord.y);
		
		return GeometryUtils.createLineString(coords, 4326);
	}

}
