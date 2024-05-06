package at.srfg.graphium.postgis.persistence.rowmapper;

import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.impl.HDArea;
import at.srfg.graphium.postgis.persistence.IHDAreaRowMapper;
import com.vividsolutions.jts.geom.Polygon;
import org.postgis.jts.JtsBinaryParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HDAreaRowMapper implements IHDAreaRowMapper<IHDArea> {

    protected static final String QUERY_PREFIX = "area";
    protected static final String ATTRIBUTES = "id, " +	// ID erhält kein Prefix, darf nur für Segment-Table gelten, erleichtert Suche nach ID in DAO
            "ST_AsEWKB(geometry) AS " + QUERY_PREFIX + "_geometry, " +
            "timestamp AS " + QUERY_PREFIX + "_timestamp, " +
            "type AS " + QUERY_PREFIX + "_type, " +
            "tags AS " + QUERY_PREFIX + "_tags ";
    private JtsBinaryParser bp = new JtsBinaryParser();

    @Override
    public IHDArea mapRow(ResultSet rs, int i) throws SQLException {
        byte[] geometry = rs.getBytes(QUERY_PREFIX + "_geometry_ewkb");
        return new HDArea(rs.getLong( "id"),
                (geometry == null ? null : (Polygon) bp.parse(geometry)),
                rs.getString(QUERY_PREFIX + "_type"),
                (Map<String, String>) rs.getObject(QUERY_PREFIX + "_tags"));
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
}
