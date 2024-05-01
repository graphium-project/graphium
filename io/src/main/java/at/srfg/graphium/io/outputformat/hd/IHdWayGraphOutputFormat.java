package at.srfg.graphium.io.outputformat.hd;

import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDWaySegment;

public interface IHdWayGraphOutputFormat<T extends IHDWaySegment> extends IWayGraphOutputFormat<T> {

    void serialize(IHDArea area) throws WaySegmentSerializationException;
}
