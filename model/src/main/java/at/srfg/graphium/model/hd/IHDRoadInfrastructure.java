package at.srfg.graphium.model.hd;

import at.srfg.graphium.model.IBaseSegment;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Map;

public interface IHDRoadInfrastructure extends IBaseSegment {

    Geometry getGeometry();

    void setGeometry(Geometry geometry);

    Map<String, String> getTags();

    void setTags(Map<String, String> tags);

    String getType();

    void setType(String type);
}
