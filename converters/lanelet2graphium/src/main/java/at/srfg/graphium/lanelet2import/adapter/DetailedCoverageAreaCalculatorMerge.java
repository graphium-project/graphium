package at.srfg.graphium.lanelet2import.adapter;

import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDArea;
import com.vividsolutions.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedCoverageAreaCalculatorMerge implements IDetailedCoverageAreaCalculator {

    private static Logger log = LoggerFactory.getLogger(DetailedCoverageAreaCalculatorMerge.class);
    private List<Geometry> geoms = new ArrayList<>();
    GeometryFactory gf = new GeometryFactory();


    // create polygons
    @Override
    public void expandCoverage(IHDWaySegment segment) {
        Coordinate[] lineStringCoords = new Coordinate[segment.getLeftBorderGeometry().getCoordinates().length
                + segment.getRightBorderGeometry().getCoordinates().length + 1];
        int i;
        for(i = 0; i < segment.getLeftBorderGeometry().getCoordinates().length; i++) {
            lineStringCoords[i] = segment.getLeftBorderGeometry().getCoordinates()[i];
        }

        for(int j = 0; j < segment.getRightBorderGeometry().getCoordinates().length; j++) {
            lineStringCoords[i+j] = segment.getRightBorderGeometry().getCoordinates()[j];
        }
        lineStringCoords[lineStringCoords.length -1] = lineStringCoords[0];
        Polygon poly = gf.createPolygon(lineStringCoords);
        Polygon hull = (Polygon) poly.convexHull();
        geoms.add(hull);
    }

    @Override
    public void expandCoverage(IHDArea area) {

    }

    protected Geometry getGeometry() {
        Geometry[] geomArray = new Geometry[geoms.size()];
        geomArray = geoms.toArray(geomArray);
        GeometryCollection collection = new GeometryCollection(geomArray, gf);
        Polygon combined = (Polygon) collection.union();
        combined.getExteriorRing();
        Polygon outline = gf.createPolygon(combined.getExteriorRing().getCoordinates());
        return outline;
    }

    @Override
    public IHDArea createCoverageHdArea(long id) {
        Polygon outline = (Polygon) getGeometry();
        IHDArea area = new  HDArea(id, outline, "graph_coverage", new HashMap<>());
        return area;
    }

}
