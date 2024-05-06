package at.srfg.graphium.io.outputformat.hd;

import at.srfg.graphium.io.outputformat.IWayGraphOutputFormatFactory;
import at.srfg.graphium.model.hd.IHDWaySegment;

import java.io.IOException;
import java.io.OutputStream;

public interface IHdWayGraphOutputFormatFactory<T extends IHDWaySegment> extends IWayGraphOutputFormatFactory<T> {

    IHdWayGraphOutputFormat<T> getWayGraphOutputFormat(OutputStream stream) throws IOException;

}
