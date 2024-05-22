package at.srfg.graphium.lanelet2import.adapter;

import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDWaySegment;
import org.locationtech.jts.algorithm.hull.ConcaveHull;
import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DetailedCoverageAreaCalculatorHull implements IDetailedCoverageAreaCalculator {

    private static Logger log = LoggerFactory.getLogger(DetailedCoverageAreaCalculatorHull.class);
    private List<Geometry> geoms = new ArrayList<>();
    GeometryFactory gf = new GeometryFactory();

    // line approach
   /* public void expandCoverage(IHDWaySegment segment) {
        Coordinate[] lineStringCoords = new Coordinate[segment.getLeftBorderGeometry().getCoordinates().length];
        for(int i = 0; i < segment.getLeftBorderGeometry().getCoordinates().length; i++) {
            com.vividsolutions.jts.geom.Coordinate coord = segment.getLeftBorderGeometry().getCoordinates()[i];
            lineStringCoords[i] = new Coordinate(coord.x, coord.y);
        }
        geoms.add(gf.createLineString(lineStringCoords));
//        geoms.add(gf.createLineString(lineStringCoords).buffer(0.00001));
        lineStringCoords = new Coordinate[segment.getRightBorderGeometry().getCoordinates().length];
        for(int i = 0; i < segment.getRightBorderGeometry().getCoordinates().length; i++) {
            com.vividsolutions.jts.geom.Coordinate coord = segment.getRightBorderGeometry().getCoordinates()[i];
            lineStringCoords[i] = new Coordinate(coord.x, coord.y);
        }
        geoms.add(gf.createLineString(lineStringCoords));
//        geoms.add(gf.createLineString(lineStringCoords).buffer(0.00001));
    }*/

    // create polygons
    @Override
    public void expandCoverage(IHDWaySegment segment) {
        Coordinate[] lineStringCoords = new Coordinate[segment.getLeftBorderGeometry().getCoordinates().length
                + segment.getRightBorderGeometry().getCoordinates().length + 1];
        int i;
        for (i = 0; i < segment.getLeftBorderGeometry().getCoordinates().length; i++) {
            com.vividsolutions.jts.geom.Coordinate coord = segment.getLeftBorderGeometry().getCoordinates()[i];
            lineStringCoords[i] = new Coordinate(coord.x, coord.y);
        }

        for (int j = 0; j < segment.getRightBorderGeometry().getCoordinates().length; j++) {
            com.vividsolutions.jts.geom.Coordinate coord = segment.getRightBorderGeometry().getCoordinates()[j];
            lineStringCoords[i + j] = new Coordinate(coord.x, coord.y);
        }
        lineStringCoords[lineStringCoords.length - 1] = lineStringCoords[0];
        Polygon poly = gf.createPolygon(lineStringCoords);
        Polygon hull = (Polygon) poly.convexHull();
        geoms.add(hull);
    }

    @Override
    public void expandCoverage(IHDArea area) {

    }

    @Override
    public IHDArea createCoverageHdArea(long id) {
        // TODO: unimplemented
        return null;
    }

    protected Geometry getGeometry() {
        Geometry[] geomArray = new Geometry[geoms.size()];
        geomArray = geoms.toArray(geomArray);
        GeometryCollection collection = new GeometryCollection(geomArray, gf);
        ConcaveHull hull = new ConcaveHull(collection);
        hull.setHolesAllowed(false);
        hull.setMaximumEdgeLength(0.00012);
        Geometry hullGeometry = hull.getHull();


        Polygon combined = (Polygon) collection.union();
        Polygon outline = gf.createPolygon(combined.getExteriorRing());
        return hullGeometry;

    }

}
