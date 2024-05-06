package at.srfg.graphium.service.impl;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.impl.GraphReadServiceImpl;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.postgis.persistence.IHDWayGraphAdditionalElementsReadDao;
import at.srfg.graphium.service.IHDGraphReadService;
import com.vividsolutions.jts.geom.Polygon;

import java.util.Set;

public class HDGraphReadServiceImpl<T extends IHDWaySegment> extends GraphReadServiceImpl<T>
        implements IHDGraphReadService<T> {

    protected IHDWayGraphAdditionalElementsReadDao<T> additionalElementsReadDao;

    @Override
    public void streamStreetSegments(
            ISegmentOutputFormat<T> outputFormat,
            Polygon bounds, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException {
        super.streamStreetSegments(outputFormat, bounds, graphName, version);
        if(outputFormat instanceof IHdWayGraphOutputFormat) {
            IHdWayGraphOutputFormat<T> hdOutputFormat = (IHdWayGraphOutputFormat<T>) outputFormat;
            hdOutputFormat.finishSegments();
            additionalElementsReadDao.streamAreas(hdOutputFormat, bounds, graphName, version);
            hdOutputFormat.finishAreas();
            additionalElementsReadDao.streamInfraAndSigns(hdOutputFormat, bounds, graphName, version);
            hdOutputFormat.finishRoadInfrastructure();
        }
    }

    @Override
    public void streamStreetSegments(
            ISegmentOutputFormat<T> outputFormat,
            Set<Long> ids, String graphName, String version) throws GraphNotExistsException, WaySegmentSerializationException {
        super.streamStreetSegments(outputFormat, ids, graphName, version);
        if(outputFormat instanceof IHdWayGraphOutputFormat) {
            IHdWayGraphOutputFormat<T> hdOutputFormat = (IHdWayGraphOutputFormat<T>) outputFormat;
            hdOutputFormat.finishSegments();
            additionalElementsReadDao.streamAreas(hdOutputFormat, null, graphName, version);
            hdOutputFormat.finishAreas();
            additionalElementsReadDao.streamInfraAndSigns(hdOutputFormat, null, graphName, version);
            hdOutputFormat.finishRoadInfrastructure();
        }

    }

    public IHDWayGraphAdditionalElementsReadDao getAdditionalElementsReadDao() {
        return additionalElementsReadDao;
    }

    public void setAdditionalElementsReadDao(IHDWayGraphAdditionalElementsReadDao additionalElementsReadDao) {
        this.additionalElementsReadDao = additionalElementsReadDao;
    }
}
