package at.srfg.graphium.postgis.persistence.impl;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.model.hd.IHDRegulatoryElement;
import at.srfg.graphium.model.hd.impl.HDRegulatoryElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class HDRegulatoryElementXinfoDao extends AbstractSegmentXInfoTypeAwareDao<IHDRegulatoryElement> implements IXInfoDao<IHDRegulatoryElement> {

    private static Logger log = LoggerFactory.getLogger(HDRegulatoryElementXinfoDao.class);

    private final String TABLE_NAME = "hd_regulatory_element_xinfo";
    private String CREATE_STMT = "CREATE TABLE " + "%SCHEMA%" + TABLE_NAME + " ("
            + " segment_id bigint NOT NULL, "
            + " graphversion_id bigint NOT NULL, "
            + " id bigint NOT NULL, "
            + " direction_tow boolean, "
            + " type character varying (128), "
            + " dynamic boolean NOT NULL default false, "
            + " fallback boolean NOT NULL default false, "
            + " refers_ids bigint[], "
            + " ref_lines_ids bigint[], "
            + " tags hstore, "
            + " CONSTRAINT pk_" + TABLE_NAME + "_id PRIMARY KEY (segment_id, graphversion_id),"
            + " CONSTRAINT graphs_" + TABLE_NAME + "_waygraphmetadata_fk FOREIGN KEY (graphversion_id) "
            + "  REFERENCES graphs.waygraphmetadata (id) MATCH SIMPLE " + "  ON UPDATE NO ACTION ON DELETE CASCADE "
            + " ) WITH ( OIDS=FALSE );";

    private final String insertClause = " (segment_id, graphversion_id, id, direction_tow, type, " +
            "dynamic, fallback, refers_ids, ref_lines_ids, tags) VALUES (?,?,?,?,?,?,?,?,?,?)";

    public HDRegulatoryElementXinfoDao() {
        super(new HDRegulatoryElement());
    }

    @Override
    protected String getSelectionAttributesAsString() {
        return rowMapper.getAttributes();
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void setup() {
        if (!checkIfTableExists(TABLE_NAME)) {
            getJdbcTemplate().execute(CREATE_STMT);
            log.info("Created database table " + schema + TABLE_NAME);
        }
    }

    @Override
    public void setSchema(String schema) {
        super.setSchema(schema);
        CREATE_STMT = CREATE_STMT.replace("%SCHEMA%", schema);
    }

    @Override
    public void save(String graphName, String version, IHDRegulatoryElement xInfo) throws GraphNotExistsException {
        log.info("saving single regulatory element for graph {} in version {} ...", graphName, version);
        Long graphVersionId = getGraphVersionId(graphName, version);
        getJdbcTemplate().update("INSERT INTO " + schema + TABLE_NAME + insertClause, ps -> {
            int pos = 1;
            ps.setLong(pos++, xInfo.getSegmentId());
            ps.setLong(pos++, graphVersionId);
            ps.setLong(pos++, xInfo.getId());
            ps.setBoolean(pos++, xInfo.isDirectionTow());
            ps.setString(pos++, xInfo.getType().getValue());
            ps.setBoolean(pos++, xInfo.isDynamic());
            ps.setBoolean(pos++, xInfo.isFallback());
            ps.setArray(pos++, convertLongsToArray(ps.getConnection(), xInfo.getRefersIds()));
            ps.setArray(pos++, convertLongsToArray(ps.getConnection(), xInfo.getRefLineIds()));
            ps.setObject(pos++, xInfo.getTags());
        });
    }

    @Override
    public void save(String graphName, String version, List<IHDRegulatoryElement> xInfoList) throws GraphNotExistsException {
        log.info("saving {} regulatory elements for graph {} in version {} ...", xInfoList.size(), graphName, version);
        Long graphVersionId = getGraphVersionId(graphName, version);
        if (xInfoList != null) {
            preProcessList(graphName, version, xInfoList);
            getJdbcTemplate().batchUpdate("INSERT INTO " + schema + TABLE_NAME + insertClause, new BatchPreparedStatementSetter() {
                IHDRegulatoryElement xInfo;

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    xInfo = xInfoList.get(i);
                    int pos = 1;
                    ps.setLong(pos++, xInfo.getSegmentId());
                    ps.setLong(pos++, graphVersionId);
                    ps.setLong(pos++, xInfo.getId());
                    ps.setBoolean(pos++, xInfo.isDirectionTow());
                    ps.setString(pos++, xInfo.getType().getValue());
                    ps.setBoolean(pos++, xInfo.isDynamic());
                    ps.setBoolean(pos++, xInfo.isFallback());
                    ps.setArray(pos++, convertLongsToArray(ps.getConnection(), xInfo.getRefersIds()));
                    ps.setArray(pos++, convertLongsToArray(ps.getConnection(), xInfo.getRefLineIds()));
                    ps.setObject(pos++, xInfo.getTags());
                }

                @Override
                public int getBatchSize() {
                    return xInfoList.size();
                }
            });
        }
    }

    protected Array convertLongsToArray(Connection con, Set<Long> ids) throws SQLException {
        return ids != null ? con.createArrayOf("bigint", ids.toArray()) : null;
    }

    @Override
    public void update(String graphName, String version, IHDRegulatoryElement xInfo) throws GraphNotExistsException {
        log.info("updating single regulatory element for graph {} in version {} - NOT IMPLEMENTED JET", graphName, version);
        // TODO implement
    }

    @Override
    public void update(String graphName, String version, List<IHDRegulatoryElement> xInfoList) throws GraphNotExistsException {
        log.info("updating {} regulatory elements for graph {} in version {}  - NOT IMPLEMENTED JET", xInfoList.size(), graphName, version);
        // TODO implement
    }

    @Override
    public void delete(String graphName, String version, IHDRegulatoryElement xInfo) throws GraphNotExistsException {
        Long graphVersionId = getGraphVersionId(graphName, version);
        xInfo.setGraphVersionId(graphVersionId);
        getJdbcTemplate().update("DELETE FROM " + schema + TABLE_NAME + " WHERE segment_id=? AND direction_tow=? AND graphversion_id=?", xInfo.getSegmentId(), xInfo.isDirectionTow(), xInfo.getGraphVersionId());
    }

    @Override
    public void deleteAll(String graphName, String version) throws GraphNotExistsException {
        Long graphVersionId = getGraphVersionId(graphName, version);
        getJdbcTemplate().update("DELETE FROM " + schema + TABLE_NAME + " WHERE graphversion_id=?", graphVersionId);
    }

    private void preProcessList(String graphName, String version, List<IHDRegulatoryElement> xInfoList) throws GraphNotExistsException {
        if (!xInfoList.isEmpty()) {
            Long graphVersionId = getGraphVersionId(graphName, version);
            for (IHDRegulatoryElement xi : xInfoList) {
                xi.setGraphVersionId(graphVersionId);
            }
        }
    }

}
