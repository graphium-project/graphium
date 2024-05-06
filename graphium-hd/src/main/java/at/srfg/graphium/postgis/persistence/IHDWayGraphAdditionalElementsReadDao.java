package at.srfg.graphium.postgis.persistence;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.view.IWayGraphView;
import com.vividsolutions.jts.geom.Polygon;

import java.util.Set;

public interface IHDWayGraphAdditionalElementsReadDao<T extends IHDWaySegment> {

    void streamInfraAndSigns(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds,
            String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

    void streamInfraAndSigns(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds, Set<Long> ids,
            IWayGraphView view, String graphVersion) throws WaySegmentSerializationException;

    void streamAreas(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds,
            String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException;

    void streamAreas(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds, Set<Long> ids,
            IWayGraphView view, String graphVersion) throws WaySegmentSerializationException;
}
