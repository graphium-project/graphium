package at.srfg.graphium.io.outputformat.hd;

import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.model.hd.IHDWaySegment;

public interface IHdWayGraphOutputFormat<T extends IHDWaySegment> extends IWayGraphOutputFormat<T> {

    void finishSegments() throws WaySegmentSerializationException;

    void finishAreas() throws WaySegmentSerializationException;

    void finishRoadInfrastructure() throws WaySegmentSerializationException;

    void serialize(IHDArea area) throws WaySegmentSerializationException;

    void serialize(IHDInfraAndSigns roadInfra) throws WaySegmentSerializationException;
}
