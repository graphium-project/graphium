/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.gipimport.helper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class GeoHelper {
	
	private static GeometryFactory gm = new GeometryFactory();

	public static final int COORDINATE_MULTIPLIER = 10000000;
	
	public static Point createPoint2D(double x, double y, int SRID) {
		Coordinate c = new Coordinate(x, y);
		Point p = gm.createPoint(c);
		p.setSRID(SRID);
		return p;
	}
	
	public static LineString createLineString(Coordinate[] coords, int SRID) {
		LineString ls = gm.createLineString(coords);
		ls.setSRID(SRID);
		return ls;
	}

    public static boolean isConnected(LineString lineFrom, LineString lineTo) {
        return lineFrom != null && lineTo != null &&
                (equalsPoint(lineFrom.getEndPoint(),lineTo.getEndPoint())
                || equalsPoint(lineFrom.getStartPoint(),lineTo.getEndPoint())
                || equalsPoint(lineFrom.getEndPoint(),lineTo.getStartPoint())
                || equalsPoint(lineFrom.getStartPoint(),lineTo.getStartPoint())
                );
    }

    public static boolean equalsPoint(Point first, Point second) {
        return first.getX() == second.getX() && first.getY() == second.getY();
    }
}
