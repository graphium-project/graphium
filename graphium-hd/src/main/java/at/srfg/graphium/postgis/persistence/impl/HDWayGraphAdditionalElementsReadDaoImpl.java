package at.srfg.graphium.postgis.persistence.impl;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.service.impl.GraphReadOrder;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.postgis.persistence.IHDAreaRowMapper;
import at.srfg.graphium.postgis.persistence.IHDInfraAndSignsRowMapper;
import at.srfg.graphium.postgis.persistence.IHDWayGraphAdditionalElementsReadDao;
import at.srfg.graphium.postgis.utils.ViewParseUtil;
import com.vividsolutions.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HDWayGraphAdditionalElementsReadDaoImpl<T extends IHDWaySegment> extends AbstractWayGraphDaoImpl
        implements IHDWayGraphAdditionalElementsReadDao<T> {

    private static Logger log = LoggerFactory.getLogger(HDWayGraphAdditionalElementsReadDaoImpl.class);
    private IWayGraphViewDao viewDao;
    private IHDAreaRowMapper<IHDArea> areaRowMapper;
    private IHDInfraAndSignsRowMapper<IHDInfraAndSigns> infraAndSignsRowMapper;

    private int fetchSize = 5000;

    @PostConstruct
    public void setup() {

    }

    @Override
    public void streamInfraAndSigns(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds,
            String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException {

        IWayGraphView view = viewDao.getView(viewName);
        streamInfraAndSigns(outputFormat, bounds, null, view, version);
    }

    @Override
    public void streamInfraAndSigns(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds, Set<Long> ids,
            IWayGraphView view, String graphVersion) throws WaySegmentSerializationException {
        // prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...",
        // 		use aliases to avoid duplicate field names, ...)
        Map<String, Set<Long>> idsMap = null;
        if (ids != null && !ids.isEmpty()) {
            idsMap = new HashMap<>();
            idsMap.put("id", ids);
        }
        String infraAndSignsquery = prepareInfraAndSignsViewFilterQuery(view, graphVersion, schema, null,
                "_infra_and_signs", null, idsMap);

        doStreamObject(infraAndSignsquery, infraAndSignsRowMapper, outputFormat);
    }

    @Override
    public void streamAreas(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds,
            String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException {

        IWayGraphView view = viewDao.getView(viewName);
        streamAreas(outputFormat, bounds, null, view, version);
    }

    @Override
    public void streamAreas(
            IHdWayGraphOutputFormat<T> outputFormat, Polygon bounds, Set<Long> ids,
            IWayGraphView view, String graphVersion) throws WaySegmentSerializationException {

        // prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...",
        // 		use aliases to avoid duplicate field names, ...)
        Map<String, Set<Long>> idsMap = null;
        if (ids != null && !ids.isEmpty()) {
            idsMap = new HashMap<>();
            idsMap.put("id", ids);
        }
        String areaQuery = prepareAreaViewFilterQuery(view, graphVersion, schema, null,
                "_areas", null, idsMap);
        doStreamObject(areaQuery, areaRowMapper, outputFormat);
    }

    public static String prepareAreaViewFilterQuery(IWayGraphView view, String graphVersion, String schema,
                                                String geometryManipulationClause, String viewPrefix,
                                                GraphReadOrder order, Map<String, ? extends Object> additionalFilter) {

        String query = "SELECT *, st_asewkb(area_geometry) AS area_geometry_ewkb" +
             //   (view.isWaySegmentsIncluded() ? geometryManipulationClause : "") +
                " FROM " + schema + view.getDbViewName() + viewPrefix;

        return ViewParseUtil.addFiltersAndOrder(query, view, graphVersion,schema, order, additionalFilter, "area_");
    }

    public static String prepareInfraAndSignsViewFilterQuery(IWayGraphView view, String graphVersion, String schema,
                                                    String geometryManipulationClause, String viewPrefix,
                                                    GraphReadOrder order, Map<String, ? extends Object> additionalFilter) {

        String query = "SELECT *, st_asewkb(infra_and_signs_geometry) AS infra_and_signs_geometry_ewkb" +
                //   (view.isWaySegmentsIncluded() ? geometryManipulationClause : "") +
                " FROM " + schema + view.getDbViewName() + viewPrefix;

        return ViewParseUtil.addFiltersAndOrder(query, view, graphVersion,schema, order, additionalFilter, "infra_and_signs_");
    }


    protected void doStreamObject(String query, RowMapper rowMapper,
                                 IHdWayGraphOutputFormat<T> outputFormat) throws WaySegmentSerializationException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.getConnection(getDataSource());
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(query);
            ps.setFetchSize(fetchSize);
            rs = ps.executeQuery();
            int i = 0;

            Object object = null;

            // iterate result set;
            boolean atLeastOneRowFound = rs.next();

            if (atLeastOneRowFound) {
                do {
                    object = rowMapper.mapRow(rs, i);

                    // last one produced
                    if (object != null) {
                        // try to serialize to stream
                        if(object instanceof IHDArea) {
                            outputFormat.serialize((IHDArea) object);
                        }
                        else if(object instanceof IHDInfraAndSigns) {
                            outputFormat.serialize((IHDInfraAndSigns) object);
                        }
                        i++;
                    }

                    if (i % 10000 == 0) {
                        log.info(i + " objects loaded");
                    }
                    // } while (object != null);
                } while (rs.next());
            }

            log.info("{} objects loaded", i);

        } catch (Exception e) {
            throw new WaySegmentSerializationException(e.getMessage(), e);
        }
        finally {
            if (conn != null)
                try {
                    DataSourceUtils.doReleaseConnection(conn, getDataSource());
                } catch (SQLException e) {
                    log.error("error during connection release", e);
                }
            if (ps != null) JdbcUtils.closeStatement(ps);
            if (rs != null) JdbcUtils.closeResultSet(rs);
        }
    }

    public IWayGraphViewDao getViewDao() {
        return viewDao;
    }

    public void setViewDao(IWayGraphViewDao viewDao) {
        this.viewDao = viewDao;
    }

    public IHDAreaRowMapper<IHDArea> getAreaRowMapper() {
        return areaRowMapper;
    }

    public void setAreaRowMapper(IHDAreaRowMapper<IHDArea> areaRowMapper) {
        this.areaRowMapper = areaRowMapper;
    }

    public IHDInfraAndSignsRowMapper<IHDInfraAndSigns> getInfraAndSignsRowMapper() {
        return infraAndSignsRowMapper;
    }

    public void setInfraAndSignsRowMapper(IHDInfraAndSignsRowMapper<IHDInfraAndSigns> infraAndSignsRowMapper) {
        this.infraAndSignsRowMapper = infraAndSignsRowMapper;
    }
}
