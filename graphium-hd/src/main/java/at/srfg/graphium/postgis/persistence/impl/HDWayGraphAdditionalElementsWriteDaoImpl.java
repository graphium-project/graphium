package at.srfg.graphium.postgis.persistence.impl;

import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.helper.GraphVersionHelper;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.postgis.persistence.IHDWayGraphAdditionalElementsWriteDao;
import com.vividsolutions.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class HDWayGraphAdditionalElementsWriteDaoImpl extends AbstractWayGraphDaoImpl
    implements IHDWayGraphAdditionalElementsWriteDao {

    private static Logger log = LoggerFactory.getLogger(HDWayGraphAdditionalElementsWriteDaoImpl.class);

    protected WKTWriter wktWriter;
    protected IWayGraphVersionMetadataDao metadataDao;

    protected String areaTablePrefix = null;
    protected String parentAreaTableName = null;
    protected String infraAndSignTablePrefix = null;
    protected String parentInfraAndSignTableName = null;

    @PostConstruct
    public void setup() {
        wktWriter = new WKTWriter();
        areaTablePrefix = HDWayGraphWriteDaoImpl.HDAREA_TABLE_PREFIX;
        parentAreaTableName = HDWayGraphWriteDaoImpl.PARENT_HDAREA_TABLE_NAME;
        infraAndSignTablePrefix = HDWayGraphWriteDaoImpl.HDINFRA_AND_SIGN_TABLE_PREFIX;
        parentInfraAndSignTableName = HDWayGraphWriteDaoImpl.PARENT_HDINFRA_AND_SIGN_TABLE_NAME;
    }

    @Override
    public void saveHdAreas(List<IHDArea> areas, String graphName, String version) throws GraphStorageException {
        log.info("storing {} areas", areas.size());

        String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
        IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
        int graphVersionId = (int) metadata.getId();
        try {
            getNamedParameterJdbcTemplate().batchUpdate(getAreaInsertStatement(graphVersionName), getAreaParamSource(areas, graphVersionId));
        } catch (SQLException e) {
            throw new GraphStorageException("error inserting areas", e);
        }

    }

    @Override
    public void saveHdInfrastructureAndSigns(List<IHDInfraAndSigns> infrastructureAndSigns,
                                             String graphName, String version) throws GraphStorageException {
        log.info("storing {} infrastructure and signs", infrastructureAndSigns.size());

        String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
        IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
        int graphVersionId = (int) metadata.getId();
        try {
            getNamedParameterJdbcTemplate().batchUpdate(getInfraAndSignInsertStatement(graphVersionName),
                    getInfraAndSignParamSource(infrastructureAndSigns, graphVersionId));
        } catch (SQLException e) {
            throw new GraphStorageException("error inserting areas", e);
        }
    }

    protected SqlParameterSource[] getAreaParamSource(List<IHDArea> areas, Integer graphVersionId) throws SQLException {
        final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
        SqlParameterSource[] argArray = new SqlParameterSource[areas.size()];
        int i = 0;
        for (IHDArea area : areas) {
            MapSqlParameterSource args = getAreaParamSource(area, now);
            if (graphVersionId != null) {
                args.addValue("graphVersionId", graphVersionId);
            }
            argArray[i] = args;
            i++;
        }
        return argArray;
    }

    protected MapSqlParameterSource getAreaParamSource(IHDArea area, Timestamp now) throws SQLException {
        MapSqlParameterSource args = new MapSqlParameterSource();
        args.addValue("id", area.getId());
        args.addValue("geometry","SRID=4326;"+wktWriter.write(area.getGeometry()));
        args.addValue("type", area.getType());
        args.addValue("timestamp", now);
        args.addValue("tags", area.getTags());
        return args;
    }

    protected String getAreaInsertStatement(String graphVersionName) {
        return "INSERT INTO "+ schema + areaTablePrefix + graphVersionName + " (id, graphversion_id, geometry, type, timestamp, tags)" +
                " VALUES (:id, :graphVersionId, ST_GeomFromEWKT(:geometry), :type, :timestamp, :tags)";
    }

    public SqlParameterSource[] getInfraAndSignParamSource(
            List<IHDInfraAndSigns> infraAndSigns, Integer graphVersionId) throws SQLException {
        final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
        SqlParameterSource[] argArray = new SqlParameterSource[infraAndSigns.size()];
        int i = 0;
        for (IHDInfraAndSigns infraAndSign : infraAndSigns) {
            MapSqlParameterSource args = getInfraAndSignParamSource(infraAndSign, now);
            if (graphVersionId != null) {
                args.addValue("graphVersionId", graphVersionId);
            }
            argArray[i] = args;
            i++;
        }
        return argArray;
    }

    protected MapSqlParameterSource getInfraAndSignParamSource(IHDInfraAndSigns infraAndSigns, Timestamp now) throws SQLException {
        MapSqlParameterSource args = new MapSqlParameterSource();
        args.addValue("id", infraAndSigns.getId());
        args.addValue("geometry","SRID=4326;"+wktWriter.write(infraAndSigns.getGeometry()));
        args.addValue("type", infraAndSigns.getType());
        args.addValue("timestamp", now);
        args.addValue("tags", infraAndSigns.getTags());
        return args;
    }

    protected String getInfraAndSignInsertStatement(String graphVersionName) {
        return "INSERT INTO "+ schema + infraAndSignTablePrefix + graphVersionName + " (id, graphversion_id, geometry, type, timestamp, tags)" +
                " VALUES (:id, :graphVersionId, ST_GeomFromEWKT(:geometry), :type, :timestamp, :tags)";
    }

    public IWayGraphVersionMetadataDao getMetadataDao() {
        return metadataDao;
    }

    public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
        this.metadataDao = metadataDao;
    }

}
