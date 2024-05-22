package at.srfg.graphium.lanelet2import.adapter;

import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDWaySegment;
import com.vividsolutions.jts.geom.Geometry;

public interface IDetailedCoverageAreaCalculator {
    // create polygons
    void expandCoverage(IHDWaySegment segment);

    void expandCoverage(IHDArea area);

    IHDArea createCoverageHdArea(long id);
}
