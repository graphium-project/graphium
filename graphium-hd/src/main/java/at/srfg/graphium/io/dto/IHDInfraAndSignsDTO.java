package at.srfg.graphium.io.dto;

import com.vividsolutions.jts.geom.Geometry;

import java.util.Map;

public interface IHDInfraAndSignsDTO extends IBaseSegmentDTO {

    Geometry getGeometry();

    void setGeometry(Geometry area);

    Map<String, String> getTags();

    void setTags(Map<String, String> tags);

    String getType();

    void setType(String type);

}
