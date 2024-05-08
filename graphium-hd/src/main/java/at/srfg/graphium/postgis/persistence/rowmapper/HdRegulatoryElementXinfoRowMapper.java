package at.srfg.graphium.postgis.persistence.rowmapper;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.hd.HDRegulatoryElementType;
import at.srfg.graphium.model.hd.IHDRegulatoryElement;
import at.srfg.graphium.model.hd.impl.HDRegulatoryElement;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HdRegulatoryElementXinfoRowMapper extends AbstractXInfoModelTypeAware<IHDRegulatoryElement>
        implements ISegmentXInfoRowMapper<IHDRegulatoryElement> {

    private static Logger log = LoggerFactory.getLogger(HdRegulatoryElementXinfoRowMapper.class);

    private static final String QUERY_PREFIX = "rexinfo";
    private static final String ATTRIBUTES = "segment_id AS " + QUERY_PREFIX + "_segment_id, " +
            "direction_tow AS " + QUERY_PREFIX + "_direction_tow, " +
            "graphversion_id AS " + QUERY_PREFIX + "_graphversion_id, " +
            "type AS " + QUERY_PREFIX + "_type";

    public HdRegulatoryElementXinfoRowMapper() {
        super(new HDRegulatoryElement());
    }

    @Override
    public boolean isApplicable(ResultSet rs) {
        boolean applicable = false;

        int i = 1;
        try {
            while (!applicable && i <= rs.getMetaData().getColumnCount()) {
                if (rs.getMetaData().getColumnLabel(i).startsWith(QUERY_PREFIX)) {
                    applicable = true;
                }
                i++;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            applicable = false;
        }
        return applicable;
    }

    @Override
    public boolean fitsPrefix(String prefix) {
        return prefix.equals(QUERY_PREFIX);
    }

    @Override
    public String getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public String getPrefix() {
        return QUERY_PREFIX;
    }

    @Override
    public IBaseSegment mapRow(ResultSet rs, int i) throws SQLException {
        IHDRegulatoryElement regulatoryElement = new HDRegulatoryElement();
        regulatoryElement.setSegmentId(rs.getLong(QUERY_PREFIX + "_segment_id"));
        regulatoryElement.setDirectionTow(rs.getBoolean(QUERY_PREFIX + "_direction_tow"));
        regulatoryElement.setGraphVersionId(rs.getLong(QUERY_PREFIX + "_graphversion_id"));
        regulatoryElement.setType(HDRegulatoryElementType.fromValue(rs.getString(QUERY_PREFIX + "_type")));

        IBaseSegment segment = new BaseSegment();
        segment.setId(regulatoryElement.getSegmentId());
        segment.addXInfo(regulatoryElement);
        return segment;
    }
}
