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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.postgis.persistence.ISegmentResultSetExtractor;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import at.srfg.graphium.postgis.persistence.resultsetextractors.ISegmentResultSetExtractorFactory;
import at.srfg.graphium.postgis.utils.ViewParseUtil;

/**
 *
 * Created by shennebe on 23.09.2016.
 */
public abstract class AbstractSegmentXInfoTypeAwareDao<T extends ISegmentXInfo>
        extends AbstractWayGraphDaoImpl implements IXInfoDao<T> {

	private static Logger log = LoggerFactory.getLogger(AbstractSegmentXInfoTypeAwareDao.class);
	
	protected IWayGraphViewDao viewDao;
	protected IWayGraphVersionMetadataDao metadataDao;
	protected ISegmentXInfoRowMapper<T> rowMapper;
	protected ISegmentResultSetExtractorFactory resultSetExtractorFactory;
	
	private int fetchSize;
    private String xInfoType;

    public AbstractSegmentXInfoTypeAwareDao(T object) {
        xInfoType = object.getXInfoType();
    }

    @PostConstruct
    public void init() {
        setup();
    }
    
    @Override
    public String getResponsibleType() {
        return this.xInfoType;
    }
    
	@Override
	public IBaseSegment get(String viewName, String version, long segmentId) throws GraphNotExistsException {
		IWayGraphView view = getView(viewName);
		String query = getFilteredQuery(view, version) + "WHERE xinfoView.id = ?";
		
		return getJdbcTemplate().queryForObject(query, new Object[]{segmentId}, rowMapper);
	}
	
	@Override
	public void streamSegments(ISegmentOutputFormat<IBaseSegment> outputFormat, String viewName, String version) throws GraphNotExistsException {
		IWayGraphView view = getView(viewName);
		String query = getFilteredQuery(view, version);

		Set<String> tableAliases = new HashSet<>();
		tableAliases.add(rowMapper.getPrefix());
		ISegmentResultSetExtractor<IBaseSegment, T> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
		
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
			
			IBaseSegment segment = null;
		
			// iterate result set;
			rs.next();
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
			
			log.info(i + " segments loaded");
			
		} catch (SQLException e) {			
			log.error("error executing segment iteration query", e);
		} catch (WaySegmentSerializationException e) {
			log.error("error during streaming segments", e);
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

	protected String getFilteredQuery(IWayGraphView view, String version) throws GraphNotExistsException {
		String query = ViewParseUtil.prepareViewFilterQuery(view, version, schema, null, null);
		query = "WITH xinfoView AS (" + query + ") SELECT id, " + getSelectionAttributesAsString() + " FROM xinfoView JOIN " +
				schema + getTableName() + " AS xi ON xi.segment_id = xinfoView.id AND xi.graphversion_id = xinfoView.wayseg_graphversion_id";
		return query;
	}
	
	protected abstract String getSelectionAttributesAsString();

	protected abstract String getTableName();
	
	protected Long getGraphVersionId(String viewName, String version) throws GraphNotExistsException {
		IWayGraphView view = getView(viewName);
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(view.getGraph().getName(), version);
		if (metadata == null) {
			throw new GraphNotExistsException("Graph with name " + view.getGraph().getName() + " in version " + version + " does not exist", viewName);
		}
		return metadata.getId();
	}
	
	protected IWayGraphVersionMetadata getGraphMetadata(String viewName, String version) throws GraphNotExistsException {
		IWayGraphView view = getView(viewName);
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(view.getGraph().getName(), version);
		if (metadata == null) {
			throw new GraphNotExistsException("Graph with name " + view.getGraph().getName() + " in version " + version + " does not exist", viewName);
		}
		return metadata;
	}
	
	protected IWayGraphView getView(String viewName) throws GraphNotExistsException {
		IWayGraphView view = viewDao.getView(viewName);
		if (view == null) {
			throw new GraphNotExistsException("View with name " + viewName + " does not exist", viewName);
		}
		return view;
	}

	protected boolean checkIfTableExists(String tableName)
	{
		String query = "SELECT count(1) FROM pg_tables WHERE tablename = '" + tableName + "' and schemaname = '" + schema.replace(".", "") + "'";
		return getJdbcTemplate().queryForObject(query, Integer.class) > 0;
	}

	public IWayGraphViewDao getViewDao() {
		return viewDao;
	}

	public void setViewDao(IWayGraphViewDao viewDao) {
		this.viewDao = viewDao;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public ISegmentXInfoRowMapper<T> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(ISegmentXInfoRowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public ISegmentResultSetExtractorFactory getResultSetExtractorFactory() {
		return resultSetExtractorFactory;
	}

	public void setResultSetExtractorFactory(ISegmentResultSetExtractorFactory resultSetExtractorFactory) {
		this.resultSetExtractorFactory = resultSetExtractorFactory;
	}

}
