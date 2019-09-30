/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.postgis.persistence.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.service.impl.GraphReadOrder;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.impl.QueueWrappingOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.postgis.persistence.ISegmentResultSetExtractor;
import at.srfg.graphium.postgis.persistence.resultsetextractors.ISegmentResultSetExtractorFactory;
import at.srfg.graphium.postgis.utils.ViewParseUtil;

/**
 * @author mwimmer
 *
 */
public class WayGraphReadDaoImpl<T extends IBaseSegment, X extends ISegmentXInfo> extends AbstractWayGraphDaoImpl implements
		IWayGraphReadDao<T> {

	private static Logger log = LoggerFactory.getLogger(WayGraphReadDaoImpl.class);
	
	protected static final int SRID = 4326;
	protected static final String BOUNDINGBOX_PLACEHOLDER = "$BBOXWHERECLAUSE$";
	protected static final String CONNECTION_ACCESSTYPE_CLAUSE_PLACEHOLDER = "$CONNECTIONACCESSTYPEWHERE$";
	protected static final String SEGMENT_ACCESSTYPE_CLAUSE_PLACEHOLDER = "$SEGMENTACCESSTYPEWHERE$";
	protected static final String SEGMENT_TABLE_PLACEHOLDER = "$SEGMENTTABLENAME$";
	protected static final String CONNECTION_TABLE_PLACEHOLDER = "$CONNECTIONTABLENAME$";	

	protected final String CONNECTIONS_SELECTS_CLAUSE = 
			" , array(SELECT con::varchar FROM " + CONNECTION_TABLE_PLACEHOLDER + " con " +
			" WHERE con.node_id = segment.startnode_id  and con.from_segment_id = segment.id and con.to_segment_id != segment.id "
			+ CONNECTION_ACCESSTYPE_CLAUSE_PLACEHOLDER + ") AS startnodesegments " + 
			" , array(SELECT con::varchar FROM " + CONNECTION_TABLE_PLACEHOLDER + " con " + 
			" WHERE con.node_id = segment.endnode_id and con.from_segment_id = segment.id and con.to_segment_id != segment.id "
			+ CONNECTION_ACCESSTYPE_CLAUSE_PLACEHOLDER + ") AS endnodesegments";

	private int fetchSize = 5000;
	
	private IWayGraphViewDao viewDao;
	private ISegmentResultSetExtractorFactory resultSetExtractorFactory;
	
	@PostConstruct
	public void setup() {
		Assert.notNull(viewDao);
	}
	
	@Override
	public T getSegmentById(String graphName, String version, long segmentId,
			boolean includeConnections) throws GraphNotExistsException {
		List<Long> segmentIds = new ArrayList<>(1);
		segmentIds.add(segmentId);
		List<T> segments = getSegmentsById(graphName, version, segmentIds, includeConnections);
		if (segments == null || segments.isEmpty()) {
			return null;
		} else {
			return segments.get(0);
		}
	}

	@Override
	public List<T> getSegmentsById(String graphName, String version, List<Long> segmentIds,
			boolean includeConnections) throws GraphNotExistsException {
		IWayGraphView view = viewDao.getView(graphName);
		
		// parse filter query to detect concerned tables
		Set<String> tableAliases = ViewParseUtil.parseTableAliases(viewDao.getViewDefinition(view));
		
		// use object factory to retrieved all needed ResultSetExtractors
		ISegmentResultSetExtractor<T, X> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		
		// prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...", 
		// 		use aliases to avoid duplicate field names, ...)
		Map<String, Set<Long>> idsMap = null;
		if (segmentIds != null && !segmentIds.isEmpty()) {
			idsMap = new HashMap<>();
			idsMap.put("id", new HashSet<Long>(segmentIds));
		}
		String query = ViewParseUtil.prepareViewFilterQuery(view, version, schema, rsExtractor, null, idsMap);
		
		List<T> segments = queryForList(query, rsExtractor);
		return segments;
	}

	private List<T> queryForList(String query, ISegmentResultSetExtractor<T, X> rsExtractor) {
		List<T> segments = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtils.getConnection(getDataSource());
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(query);
			ps.setFetchSize(fetchSize);
			rs = ps.executeQuery();
			T segment = null;
			rs.next();
			do {
				segment = rsExtractor.extractData(rs);
				if (segment != null) {
					segments.add(segment);
				}
			} while (segment != null);
			
		} catch (SQLException e) {			
			log.error("error executing segment iteration query", e);
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
		return segments;
	}

	@Override
	public List<T> findNearestSegments(String graphName, String version, Point referencePoint,
			double radiusInKm, int maxNrOfSegments) throws GraphNotExistsException {
		IWayGraphView view = viewDao.getView(graphName);
		
		// parse filter query to detect concerned tables
		Set<String> tableAliases = ViewParseUtil.parseTableAliases(viewDao.getViewDefinition(view));
		
		// use object factory to retrieved all needed ResultSetExtractors
		ISegmentResultSetExtractor<T, X> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		
		// prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...", 
		// 		use aliases to avoid duplicate field names, ...)
		String query = ViewParseUtil.prepareViewFilterQuery(view, version, schema, rsExtractor, null);
		
		String bbox = "st_geomFromText('" +  GeometryUtils.createRectangleWithSideLengthInMetersAsWkt(referencePoint, 2*radiusInKm*1000) + "', " + SRID + ")";
		String geoClause = rsExtractor.getPrefix() + ".wayseg_geometry_ewkb && " + bbox +
							" ORDER BY " + rsExtractor.getPrefix() + ".wayseg_geometry_ewkb <-> " + bbox;
		
		query = "SELECT * FROM (" + query + ") AS wayseg WHERE " + geoClause;
		
		if (maxNrOfSegments > 0) {
			query += " LIMIT " + maxNrOfSegments;
		}
		
		List<T> segments = queryForList(query, rsExtractor);
		return segments;
	}

	@Override
	public void streamSegments(
			ISegmentOutputFormat<T> outputFormat, Polygon bounds,
			String viewName, String version) throws GraphNotExistsException, WaySegmentSerializationException {
		streamSegments(outputFormat, bounds, viewName, version, null);
	}
	
	@Override
	public void streamSegments(
			ISegmentOutputFormat<T> outputFormat, Polygon bounds,
			String viewName, String version, GraphReadOrder order) throws GraphNotExistsException, WaySegmentSerializationException {

		IWayGraphView view = viewDao.getView(viewName);

		streamSegments(outputFormat, bounds, null, view, version, order);
	}
		
	@Override
	public void streamSegments(
			ISegmentOutputFormat<T> outputFormat, Polygon bounds,
			String viewName, Date timestamp) throws GraphNotExistsException, WaySegmentSerializationException {

		
		// TODO: Sollen bounds und accessTypes überhaupt mitgegeben werden können? In einer View kann man das doch eh definieren! Wäre sonst doppelt gemoppelt.
		
		IWayGraphView view = viewDao.getView(viewName);
			
		// TODO: includeConnectionTable berücksichtigen und Query um ConnectionTable erweitern (oder sollte das im View angegeben werden? - glaub eher nicht)
		
		String graphVersion = getGraphVersion(view, timestamp);
		
		streamSegments(outputFormat, bounds, null, view, graphVersion, null);
		
	}
	
	@Override
	public void streamSegments(ISegmentOutputFormat<T> outputFormat, String viewName, String version, Set<Long> ids) 
			throws GraphNotExistsException, WaySegmentSerializationException {
		IWayGraphView view = viewDao.getView(viewName);
		
		streamSegments(outputFormat, null, ids, view, version, null);
	}

	protected void streamSegments(
			ISegmentOutputFormat<T> outputFormat, Polygon bounds, Set<Long> ids,
			IWayGraphView view, String graphVersion, GraphReadOrder order) throws WaySegmentSerializationException {

		// parse filter query to detect concerned tables
		Set<String> tableAliases = ViewParseUtil.parseTableAliases(viewDao.getViewDefinition(view));
		
		// use object factory to retrieved all needed ResultSetExtractors
		
		ISegmentResultSetExtractor<T, X> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		
		// prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...", 
		// 		use aliases to avoid duplicate field names, ...)
		Map<String, Set<Long>> idsMap = null;
		if (ids != null && !ids.isEmpty()) {
			idsMap = new HashMap<>();
			idsMap.put("id", ids);
		}
		String query = ViewParseUtil.prepareViewFilterQuery(view, graphVersion, schema, rsExtractor, order, idsMap);
		doStreamTravels(query, rsExtractor, outputFormat);
	}
	
	protected void doStreamTravels(String query, ISegmentResultSetExtractor<T, X> rsExtractor, 
			ISegmentOutputFormat<T> outputFormat) throws WaySegmentSerializationException {
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
			
			T segment = null;
		
			// iterate result set;
			boolean atLeastOneRowFound = rs.next();
			
			if (atLeastOneRowFound) {
				do {
					segment = rsExtractor.extractData(rs);
	
					// last one produced
					if (segment != null) {
						// try to serialize to stream
						outputFormat.serialize(segment);
						i++;
					}
					
					if (i % 10000 == 0) {
						log.info(i + " segments loaded");
					}
				} while (segment != null);
			}
				
			log.info(i + " segments loaded");
			
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

	@Override
	public void streamIncomingConnectedStreetSegments(ISegmentOutputFormat<T> outputFormat, String viewName, String version,
			Set<Long> ids)
			throws WaySegmentSerializationException, GraphNotExistsException {

		IWayGraphView view = viewDao.getView(viewName);
		// parse filter query to detect concerned tables
		Set<String> tableAliases = ViewParseUtil.parseTableAliases(viewDao.getViewDefinition(view));
				
		// use object factory to retrieved all needed ResultSetExtractors
		ISegmentResultSetExtractor<T, X> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		
		// performance version
		// read all IDs of connected segments
		Object[] args = new Object[2];
		args[0] = view.getGraph().getName();
		args[1] = version;
		int[] types = new int[2];
		types[0] = Types.VARCHAR;
		types[1] = Types.VARCHAR;
		
		List<Long> fromSegmentIds = getJdbcTemplate().queryForList(
				"SELECT from_segment_id FROM " + schema + PARENT_CONNECTION_TABLE_NAME +
				" WHERE graphversion_id = f_current_graphversion_immutable(?, ?)" + 
				"   AND to_segment_id in (" + StringUtils.join(ids, ", ") + ")",
				args,
				types,
				Long.class);
		
		// stream connected segments
		Map<String, Set<Long>> idsMap = null;
		if (fromSegmentIds != null && !fromSegmentIds.isEmpty()) {
			idsMap = new HashMap<>();
			idsMap.put("id", new HashSet<>(fromSegmentIds));
		}
		
		String query = ViewParseUtil.prepareViewFilterQuery(view, version, schema, rsExtractor, null, idsMap);
		
		// slow version
		
//		Map<String, Set<Long>> idsMap = null;
//		if (ids != null && !ids.isEmpty()) {
//			idsMap = new HashMap<>();
//			idsMap.put("con.to_segment_id", ids);
//		}
//
//		String query = ViewParseUtil.prepareViewFilterConJoinedQuery(view, version, schema, rsExtractor, null, idsMap);
		
		doStreamTravels(query, rsExtractor, outputFormat);	
	}
	
	/**
	 * @param timestamp
	 * @return
	 */
	@Override
	public String getGraphVersion(IWayGraphView view, Date timestamp) {
		if (timestamp == null) {
			timestamp = new Date();
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("graphName", view.getGraph().getName());
		paramMap.put("timestamp", timestamp);
		List<String> versionList = getNamedParameterJdbcTemplate().query("SELECT version FROM " + schema + METADATA_TABLE_NAME + 
				" WHERE graphname = :graphName AND valid_from <= :timestamp AND (valid_to is null OR valid_to >= :timestamp)" +
				" AND state = 'ACTIVE'", 
				paramMap, 
				new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("version");
					}
				});
		if (versionList == null || versionList.isEmpty()) {
			return null;
		} else {
			return versionList.get(0);
		}
	}

	@Override
	public void readStreetSegments(BlockingQueue<T> queue, String viewName, String version) 
			throws GraphNotExistsException, WaySegmentSerializationException {
		ISegmentOutputFormat<T> format = new QueueWrappingOutputFormat<T>(queue);
		this.streamSegments(format, null, viewName, version);
	}

	@Override
	public void readStreetSegments(BlockingQueue<T> queue, String viewName,
			String version, GraphReadOrder order) 
			throws GraphNotExistsException, WaySegmentSerializationException {
		ISegmentOutputFormat<T> format = new QueueWrappingOutputFormat<T>(queue);
		this.streamSegments(format, null, viewName, version, order);
	}
	
	@Override
	public List<T> getStreetSegments(String viewName, String version) {
		return null;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public IWayGraphViewDao getViewDao() {
		return viewDao;
	}

	public void setViewDao(IWayGraphViewDao viewDao) {
		this.viewDao = viewDao;
	}

	public ISegmentResultSetExtractorFactory getResultSetExtractorFactory() {
		return resultSetExtractorFactory;
	}

	public void setResultSetExtractorFactory(ISegmentResultSetExtractorFactory resultSetExtractorFactory) {
		this.resultSetExtractorFactory = resultSetExtractorFactory;
	}

}