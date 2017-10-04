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
/*
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.geomutils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * A helper class mainly relating on JTS implementation by Vividsolutions.com
 * 
 */
public class GeometryUtils {

	public static final int WGS84 = 4326;
	
	public static GeometryFactory gm = new GeometryFactory();

	// source http://de.wikipedia.org/wiki/World_Geodetic_System_1984
	private static double wgs84_a = 6378137.000;

	private static double wgs84_b = 6356752.314;
	
	private static double wgs84_f = (wgs84_a - wgs84_b) / wgs84_a;

	/**
	 * ACHTUNG 
	 * 
	 * Diese Methode liefert bei Anwendung auf WGS84 Koordinaten falsche Ergebnisse, der Fehler wird 
	 * umso größer, je weiter der zu projizierende Punkt von der Linie entfernt ist und je weiter der Punkt vom Aquator entfernt ist.
	 *  Wenn der zu projizierende Punkt auf der Linie liegt sollte der Fehler 0 sein.
	 *  
	 *  Für Salzburg (ca 13E 48N) beträgt der Fehler auf einer 45° schrägen Linie 38.6% des Abstandes von der Linie. D.h. beträgt 
	 *  der Abstand des Punktes von der Linie 5m beträgt der Fehler 1,93m.
	 *  
	 * @param p
	 * @param lineString
	 * @return
	 */
	public static double distanceOnLineStringInMeter(Point p, LineString lineString) {
		
		if (p.getSRID() != lineString.getSRID()) {
			throw new IllegalArgumentException("SRID of parameter p is not the same as from parameter lineString");
		}
		LocationIndexedLine indexedStartSeg = new LocationIndexedLine(lineString);
		LinearLocation startLoc = indexedStartSeg.project(p.getCoordinate());
		Geometry cutStartGeom = indexedStartSeg.extractLine(indexedStartSeg.getStartIndex(), startLoc);
		cutStartGeom.setSRID(lineString.getSRID());
		double lengthInMeter = calculateLengthMeterFromWGS84LineStringAndoyer((LineString) cutStartGeom);
		return lengthInMeter;
	}
	
	/**
	 * ACHTUNG 
	 * 
	 * Diese Methode liefert bei Anwendung auf WGS84 Koordinaten eventuell falsche Ergebnisse. 
	 * Siehe distanceOnLineStringInMeter()
	 * @param p
	 * @param lineString
	 * @return
	 */
	public static Point projectPointOnLineString(Point p, LineString lineString) {
		LocationIndexedLine line = new LocationIndexedLine(lineString);
		LinearLocation here = line.project(p.getCoordinate());
		Coordinate coord = line.extractPoint( here );
		Point point = createPoint(coord, lineString.getSRID());		
		return point;
	}

	/**
	 * Creates a com.vividsolutions.jts.geom.Point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param SRID
	 * @return
	 */
	public static Point createPoint(double x, double y, double z, int SRID) {
		Coordinate c = new Coordinate(x, y, z);
		return createPoint(c, SRID);
	}

	/**
	 * 
	 * @param c
	 * @param SRID
	 * @return
	 */
	public static Point createPoint(Coordinate c, int SRID) {
		Point p = gm.createPoint(c);
		p.setSRID(SRID);
		return p;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param SRID
	 * @return
	 */
	public static Point createPoint2D(double x, double y, int SRID) {
		Coordinate c = new Coordinate(x, y);
		return createPoint(c, SRID);
	}
	
	/**
	 * Creates a new LineString. Note: Array is not copied, so do not reuse because otherwise LineString objects refer to the same Coordinate
	 * array
	 * @param coords
	 * @param SRID
	 * @return
	 */
	public static LineString createLineString(Coordinate[] coords, int SRID) {
		LineString ls = gm.createLineString(coords);
		ls.setSRID(SRID);
		return ls;
	}

	/**
	 * Calculates the Length of a linestring in meters with the method from Andoyer
	 * @param geometry
	 * @return
	 */
	public static double calculateLengthMeterFromWGS84LineStringAndoyer(LineString geometry) {
		
		Coordinate coords[] = geometry.getCoordinates();
		double length = 0;
		for (int i = 1; i < coords.length; i++) {
			length += distanceAndoyer(coords[i - 1], coords[i]);
		}
		return length;
	}

	 /**
	  * @param start
  	  * @param end
  	  * @return
  	  */
	public static double distanceAndoyer(Point start, Point end) {
		return distanceAndoyer(start.getCoordinate(), end.getCoordinate());
	}
	
	 /**
	  * nach https://de.wikibooks.org/wiki/Astronomische_Berechnungen_f%C3%BCr_Amateure/_Distanzen/_Erdglobus#Abstand_zweier_Punkte_auf_der_Erdoberfl.C3.A4che
	  * siehe auch https://de.wikipedia.org/wiki/Henri_Andoyer
	  * 
	  * NOTE Coordinates are assumed to be in WGS84 Format and not checked.
	  * 
	  * @param start
	  * @param end
	  * @return
	  */
	public static double distanceAndoyer(Coordinate start, Coordinate end) {

      double lambdaA = Math.toRadians(start.x);
      double phiA = Math.toRadians(start.y);
      double lambdaB = Math.toRadians(end.x);
      double phiB = Math.toRadians(end.y);
      
      double F = (phiA + phiB) / 2;
      double G = (phiA - phiB) / 2;
      double L = (lambdaA - lambdaB) / 2;
      
      if (G == 0 && L == 0) return 0;
      
      double squSinL = Math.pow(Math.sin(L), 2);
      double squCosL = Math.pow(Math.cos(L), 2);        
      double squSinG = Math.pow(Math.sin(G), 2);
      double squCosG =  Math.pow(Math.cos(G), 2);
      double squSinF = Math.pow(Math.sin(F), 2);
      double squCosF =  Math.pow(Math.cos(F), 2);
      
      double S = squSinG * squCosL + squCosF * squSinL;
      double C = squCosG * squCosL + squSinF * squSinL;
      
      double w = Math.atan(Math.sqrt(S / C));
      
      double R = Math.sqrt(S * C) / w;
      
      double D = 2 * w * wgs84_a;
      
      double H1 = (3 * R - 1) / (2 * C);
      double H2 = (3 * R + 1) / (2 * S);
   
      
      double d = D * (1 + wgs84_f * H1 * squSinF * squCosG - wgs84_f * H2 * squCosF * squSinG);
      
      if (Double.isNaN(d)) {
          throw new RuntimeException("GeometryUtils.distanceAndoyer() result is not a number");
      }
      return d;
  }

	/**
	 * Based on @see DistanceOp which computest 
	 * 
	 * "Computes the distance between the nearest points" 
	 * 
	 * Points have to be WGS84, are not checked.
	 * 
	 * Distance calculated then with distanceSphericalSimple(coords[0], coords[1]) (nearest points)
	 * @param point
	 * @param ws
	 * @return
	 */
	public static double distanceMeters(Geometry g1, Geometry g2) {
		DistanceOp distanceOp = new DistanceOp(g1, g2);
		Coordinate[] coords = distanceOp.nearestPoints();
		return GeometryUtils.distanceSphericalSimple(coords[0], coords[1]);
	}

	/**
	 * Computes the distance between a point and a linestring. Distance will be calculated between point and the projection of the point on the linestring
	 * 
	 * @param point
	 * @param ws
	 * @return
	 */
	public static double distanceMeters(LineString l, Point p) {
		Point pointOnLs = GeometryUtils.projectPointOnLineString(p, l);
		return GeometryUtils.distanceAndoyer(p, pointOnLs);
	}

    /**
     * returns a simple spherical distance in meters.
     * Expects coordinates to be in WGS84 format. Mean earthradius  is considered
     * 6371229 which gives best results for latitude around 47°
     * 
     * @param start
     * @param end
     * @return
     */
	public static double distanceSphericalSimple(Coordinate start, Coordinate end) {

        double lat1 = start.y;
        double lat2 = end.y;
        double lon1 = start.x;
        double lon2 = end.x;

       	double meanRad = 6371229;
        
       	double radLat = Math.cos(Math.toRadians(lat1)) * meanRad;

        double dx = lon2 - lon1;
        double dy = lat2 - lat1;

        double factorX = Math.PI * radLat / 180;
        double factorY = Math.PI * (meanRad) / 180;

        double dxmet = factorX * dx;
        double dymet = factorY * dy;
        
        double d = Math.sqrt(dxmet * dxmet + dymet * dymet);

        if (Double.isNaN(d)) {
            throw new RuntimeException("GeometryUtils.distanceSphericalSimple() result is not a number");
        }
        return d;
    }

	/**
	 * Calculates Envelope around Point p with sidelength of sideLengthMeters.
	 * 
	 * Formula
	 * <pre>
	 *  sideLengthMeters * 180
	 *  ----------------------		= dLon [°]
	 *  meanEarthRadius * cos(point.lat) * Pi
	 * 
	 * 
	 *  sidelengthMeters * 180
	 *  ----------------------  = dLat [°]
	 *  meanEarthRadius * Pi
	 *  
	 *  </pre>
	 * 
	 * earthRadius is 6371229 which gives best results for latitude around 47°
	 * 
	 * @param p
	 * @param sideLengthMeters
	 * @return
	 * @throws IllegalArgumentException if SRID != 4326 (WGS84)
	 */
	public static Envelope createEnvelopeInMeters(Point p, double sideLengthMeters) {
		
		if (p.getSRID() != WGS84) throw new IllegalArgumentException("SRID of Point has to be " + WGS84);
		
		double radius = 6371229;
		double lon = p.getCoordinate().x;
		double lat = p.getCoordinate().y;
		
		double dLon = sideLengthMeters * 180 / radius / Math.PI / Math.cos(lat / 180 * Math.PI) / 2;
		double dLat = sideLengthMeters * 180 / radius / Math.PI / 2;
		
		Envelope env = new Envelope(lon - dLon, lon + dLon, lat - dLat, lat + dLat);
		return env;
	}
	
	/**
	 * Creates an envelop around Point with distance distance
	 * @param p
	 * @param distance in units of the p SRID
	 * @return
	 */
	public static Envelope createEnvelope(Point p, double distance) {
		Envelope env = new Envelope(p.getX() - distance, p.getX() + distance, p.getY() - distance, p.getY() + distance);
		return env;
	}

	/**
	 * 
	 * Note: coordinate of endindex will be included
	 *
	 * @param geometry
	 * @param startIndex
	 * @param endIndex The index of the last point which shall be included in the resulting linestring !!!
	 * @return
	 */
	public static Geometry subGeometry(Geometry geometry, int startIndex,
			int endIndex) {
		
		Geometry geom = null;
		Coordinate[] coords = geometry.getCoordinates();
		int length = endIndex + 1 - startIndex;
		if (length == 1) {
			// point
			Coordinate coord = coords[startIndex];
			geom = gm.createPoint(coord);
			geom.setSRID(geometry.getSRID());
			
		} else if (length > 1) {
			// linestring
			Coordinate[] newCoords = new Coordinate[length];	
			System.arraycopy(coords, startIndex, newCoords, 0, length);
			geom = gm.createLineString(newCoords);
			geom.setSRID(geometry.getSRID());
		}
		return geom;
	}

	public static Polygon createPolygon(Coordinate[] coordinates, int SRID) {
		LinearRing r = gm.createLinearRing(coordinates);

		Polygon p = gm.createPolygon(r, null);
		p.setSRID(SRID);
		return p;
	}

	public static Polygon createRectangleWithSideLengthInMeters(Point p, double sideLengthMeters) {
		Envelope env = createEnvelopeInMeters(p, sideLengthMeters);
		Coordinate[] coords = new Coordinate[5];
		coords[0] = new Coordinate(env.getMinX(), env.getMinY());
		coords[1] = new Coordinate(env.getMinX(), env.getMaxY());
		coords[2] = new Coordinate(env.getMaxX(), env.getMaxY());
		coords[3] = new Coordinate(env.getMaxX(), env.getMinY());
		coords[4] = new Coordinate(env.getMinX(), env.getMinY());
		return gm.createPolygon(coords);
	}
	
	public static String createRectangleWithSideLengthInMetersAsWkt(Point p, double sideLengthMeters) {
		Polygon poly = createRectangleWithSideLengthInMeters(p, sideLengthMeters);
		WKTWriter wktWriter = new WKTWriter();
		return wktWriter.write(poly);
	}

	public static Geometry createGeometryFromWkt(String wkt, int srid) throws ParseException {
		Geometry geom = createGeometryFromWkt(wkt);
		geom.setSRID(srid);
		return geom;
	}
	
	public static Geometry createGeometryFromWkt(String wkt) throws ParseException {
		WKTReader wktReader = new WKTReader();
		return wktReader.read(wkt);	
	}
}