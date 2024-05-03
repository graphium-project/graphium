package at.srfg.graphium.model.hd.impl;

import at.srfg.graphium.model.hd.IHDRoadInfrastructure;
import at.srfg.graphium.model.impl.BaseSegment;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Map;

public class HDRoadInfrastructure extends BaseSegment implements IHDRoadInfrastructure {

    private Geometry geometry;
    private String type;
    protected Map<String, String> tags;

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HDRoadInfrastructure [geometry=" + geometry + ", id=" + id + ", xInfo=" + xInfo + ", cons=" + cons + "]";
    }

}
