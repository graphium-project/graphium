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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.postgis.persistence.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.model.IDefaultSegmentXInfo;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.impl.DefaultSegmentXInfo;

/**
 * @author mwimmer
 */
public class DefaultSegmentXInfoDaoImpl extends AbstractSegmentXInfoTypeAwareDao<IDefaultSegmentXInfo> implements IXInfoDao<IDefaultSegmentXInfo> {

	private final String TABLENAME = "default_xinfo";
	private String CREATE_STMT = "CREATE TABLE " + "%SCHEMA%" + TABLENAME + "	( " +
			  " graphversion_id integer, tags hstore) INHERITS (%SCHEMA%xinfo) WITH ( OIDS=FALSE )";

	public DefaultSegmentXInfoDaoImpl() {
		super(new DefaultSegmentXInfo());
	}

	@Override
	public void setup() {
		if (!checkIfTableExists(TABLENAME)) {
			getJdbcTemplate().execute(CREATE_STMT);
		}
	}

	@Override
	public void setSchema(String schema) {
		super.setSchema(schema);
		CREATE_STMT = CREATE_STMT.replace("%SCHEMA%", schema);
	}

	@Override
	public void save(String graphName, String version, IDefaultSegmentXInfo xInfo) throws GraphNotExistsException {
		IWayGraphVersionMetadata graphMetadata = getGraphMetadata(graphName, version);
		getJdbcTemplate().update("INSERT INTO " + schema + TABLENAME + " (segment_id, direction_tow, graphversion_id, tags) VALUES (?,?,?,?)",
				xInfo.getSegmentId(), xInfo.isDirectionTow(), graphMetadata.getId(), xInfo.getValues());
	}

	@Override
	public void save(String graphName, String version, List<IDefaultSegmentXInfo> xInfoList) throws GraphNotExistsException {
		if (xInfoList != null) {
			preProcessList(graphName, version, xInfoList);
			getJdbcTemplate().batchUpdate("INSERT INTO " + schema + TABLENAME + " (segment_id, direction_tow, graphversion_id, tags) VALUES (?,?,?,?)",
					new BatchPreparedStatementSetter() {
						
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setLong(1, xInfoList.get(i).getSegmentId());
							if (xInfoList.get(i).isDirectionTow() != null) {
								ps.setBoolean(2, xInfoList.get(i).isDirectionTow());
							} else {
								ps.setNull(2, Types.BOOLEAN);
							}
							ps.setLong(3, xInfoList.get(i).getGraphVersionId());
							ps.setObject(4, xInfoList.get(i).getValues());
						}
						
						@Override
						public int getBatchSize() {
							return xInfoList.size();
						}
					});
		}
	}

	@Override
	public void update(String graphName, String version, IDefaultSegmentXInfo xInfo) throws GraphNotExistsException {
		if (xInfo.getGraphVersionId() == null) {
			Long graphVersionId = getGraphVersionId(graphName, version);
			xInfo.setGraphVersionId(graphVersionId);
		}
		getJdbcTemplate().update("UPDATE " + schema + TABLENAME + " SET tags=? WHERE segment_id=? AND direction_tow=? AND graphversion_id=?",
				xInfo.getValues(), xInfo.getSegmentId(), xInfo.isDirectionTow(), xInfo.getGraphVersionId());
	}

	@Override
	public void update(String graphName, String version, List<IDefaultSegmentXInfo> xInfoList) throws GraphNotExistsException {
		if (xInfoList != null) {
			preProcessList(graphName, version, xInfoList);
			getJdbcTemplate().batchUpdate("UPDATE " + schema + TABLENAME + " SET tags=? WHERE segment_id=? AND direction_tow=? AND graphversion_id=?",
					new BatchPreparedStatementSetter() {
						
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setObject(1, xInfoList.get(i).getValues());
							ps.setLong(2, xInfoList.get(i).getSegmentId());
							ps.setBoolean(3, xInfoList.get(i).isDirectionTow());
							ps.setLong(4, xInfoList.get(i).getGraphVersionId());
						}
						
						@Override
						public int getBatchSize() {
							return xInfoList.size();
						}
					});
		}
	}

	@Override
	public void delete(String graphName, String version, IDefaultSegmentXInfo xInfo) throws GraphNotExistsException {
		if (xInfo.getGraphVersionId() == null) {
			Long graphVersionId = getGraphVersionId(graphName, version);
			xInfo.setGraphVersionId(graphVersionId);
		}
		getJdbcTemplate().update("DELETE FROM " + schema + TABLENAME + " WHERE segment_id=? AND direction_tow=? AND graphversion_id=?",
				xInfo.getSegmentId(), xInfo.isDirectionTow(), xInfo.getGraphVersionId(), xInfo.getValues());
	}

	@Override
	public void deleteAll(String graphName, String version) throws GraphNotExistsException {
		Long graphVersionId = getGraphVersionId(graphName, version);
		getJdbcTemplate().update("DELETE FROM " + schema + TABLENAME + " WHERE graphversion_id=?", graphVersionId);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	private void preProcessList(String graphName, String version, List<IDefaultSegmentXInfo> xInfoList) throws GraphNotExistsException {
		if (!xInfoList.isEmpty()) {
			IWayGraphVersionMetadata graphMetadata = getGraphMetadata(graphName, version);
			for (IDefaultSegmentXInfo xi : xInfoList) {
				xi.setGraphVersionId(graphMetadata.getId());
			}
		}
	}

	@Override
	protected String getSelectionAttributesAsString() {
		return rowMapper.getAttributes();
	}

}
