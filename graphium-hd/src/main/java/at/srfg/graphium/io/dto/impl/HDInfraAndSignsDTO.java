package at.srfg.graphium.io.dto.impl;

import at.srfg.graphium.io.dto.IHDInfraAndSignsDTO;
import at.srfg.graphium.io.inputformat.impl.jackson.JacksonLineStringDeserializer;
import at.srfg.graphium.io.outputformat.impl.jackson.JacksonGeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDInfraAndSignsDTO extends BaseSegmentDTOImpl implements IHDInfraAndSignsDTO {

    private Geometry geometry;
    private String type;
    protected Map<String, String> tags;

    public HDInfraAndSignsDTO() {}

    public HDInfraAndSignsDTO(Geometry geometry) {
        super();
        this.geometry = geometry;
    }

    @Override
    @JsonSerialize(using = JacksonGeometrySerializer.class)
    @JsonDeserialize(using = JacksonLineStringDeserializer.class)
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
}
