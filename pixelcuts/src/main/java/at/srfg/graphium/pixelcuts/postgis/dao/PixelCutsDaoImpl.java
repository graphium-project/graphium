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
package at.srfg.graphium.pixelcuts.postgis.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.pixelcuts.model.IPixelCut;
import at.srfg.graphium.pixelcuts.model.impl.PixelCut;
import at.srfg.graphium.postgis.persistence.impl.AbstractSegmentXInfoTypeAwareDao;

/**
 * @author mwimmer
 *
 */
public class PixelCutsDaoImpl extends AbstractSegmentXInfoTypeAwareDao<IPixelCut> implements IXInfoDao<IPixelCut> {

	private static Logger log = LoggerFactory.getLogger(PixelCutsDaoImpl.class);
	
	private final String TABLE_NAME = "pixelcuts";
	private String CREATE_STMT = "CREATE TABLE " + "%SCHEMA%" + TABLE_NAME + " (" +
								" segment_id bigint NOT NULL, " +
								" graphversion_id bigint NOT NULL, " +
								" start_cut_right double precision, " +
								" start_cut_left double precision, " +
								" end_cut_right double precision, " +
								" end_cut_left double precision, " +
								" CONSTRAINT pk_"+ TABLE_NAME + "_id PRIMARY KEY (segment_id, graphversion_id)," +
								" CONSTRAINT graphs_"+ TABLE_NAME + "_waygraphmetadata_fk FOREIGN KEY (graphversion_id) " +
							    "  REFERENCES graphs.waygraphmetadata (id) MATCH SIMPLE " +
							    "  ON UPDATE NO ACTION ON DELETE CASCADE " +
								" ) WITH ( OIDS=FALSE );";
	
	public PixelCutsDaoImpl() {
		super(new PixelCut());
	}

	@Override
	public void setSchema(String schema) {
		super.setSchema(schema);
		CREATE_STMT = CREATE_STMT.replace("%SCHEMA%", schema);
	}

	@Override
	public void setup() {
		if (!checkIfTableExists(TABLE_NAME)) {
			getJdbcTemplate().execute(CREATE_STMT);
			log.info("Created database table " + schema + TABLE_NAME);
		}
	}
	
	@Override
	protected String getFilteredQuery(IWayGraphView view, String version) throws GraphNotExistsException {
		String query = super.getFilteredQuery(view, version);
		return query + " WHERE xi.graphversion_id = " + getGraphVersionId(view.getViewName(), version);
	}
	
	@Override
	public void save(String graphName, String version, IPixelCut xInfo) throws GraphNotExistsException {
		Long graphVersionId = getGraphVersionId(graphName, version);
		getJdbcTemplate().update("INSERT INTO " + schema + TABLE_NAME + " (segment_id, graphversion_id, start_cut_right, start_cut_left, end_cut_right, end_cut_left) VALUES (?,?,?,?,?,?)",
			new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int pos = 1;
					ps.setLong(pos++, xInfo.getSegmentId());
					ps.setLong(pos++, graphVersionId);
					ps.setDouble(pos++, xInfo.getStartCutRight());
					ps.setDouble(pos++, xInfo.getStartCutLeft());
					ps.setDouble(pos++, xInfo.getEndCutRight());
					ps.setDouble(pos++, xInfo.getEndCutLeft());
				}
				
			}
		);
	}

	@Override
	public void save(String graphName, String version, List<IPixelCut> xInfoList) throws GraphNotExistsException {
		if (xInfoList != null) {
			preProcessList(graphName, version, xInfoList);
			getJdbcTemplate().batchUpdate("INSERT INTO " + schema + TABLE_NAME + " (segment_id, graphversion_id, start_cut_right, start_cut_left, end_cut_right, end_cut_left) VALUES (?,?,?,?,?,?)",
				new BatchPreparedStatementSetter() {
					IPixelCut pixelCut;
					
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						pixelCut = xInfoList.get(i);
						int pos = 1;
						ps.setLong(pos++, pixelCut.getSegmentId());
						if (pixelCut.getGraphVersionId() != null) {
							ps.setLong(pos++, pixelCut.getGraphVersionId());
						} else {
							ps.setNull(pos++, Types.BIGINT);
						}
						ps.setDouble(pos++, pixelCut.getStartCutRight());
						ps.setDouble(pos++, pixelCut.getStartCutLeft());
						ps.setDouble(pos++, pixelCut.getEndCutRight());
						ps.setDouble(pos++, pixelCut.getEndCutLeft());
					}
					
					@Override
					public int getBatchSize() {
						return xInfoList.size();
					}
				}
			);
		}
	}

	@Override
	public void update(String graphName, String version, IPixelCut xInfo) throws GraphNotExistsException {
		Long graphVersionId = getGraphVersionId(graphName, version);
		getJdbcTemplate().update("UPDATE " + schema + TABLE_NAME + " SET start_cut_right=?, start_cut_left=?, end_cut_right=?, end_cut_left=? WHERE segment_id=? AND graphversion_id=?",
			new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int pos = 1;
					ps.setDouble(pos++, xInfo.getStartCutRight());
					ps.setDouble(pos++, xInfo.getStartCutLeft());
					ps.setDouble(pos++, xInfo.getEndCutRight());
					ps.setDouble(pos++, xInfo.getEndCutLeft());
					ps.setLong(pos++, xInfo.getSegmentId());
					ps.setLong(pos++, graphVersionId);
				}
				
			}
		);
	}

	@Override
	public void update(String graphName, String version, List<IPixelCut> xInfoList) throws GraphNotExistsException {
		if (xInfoList != null) {
			preProcessList(graphName, version, xInfoList);
			getJdbcTemplate().batchUpdate("UPDATE " + schema + TABLE_NAME + " SET start_cut_right=?, start_cut_left=?, end_cut_right=?, end_cut_left=? WHERE segment_id=? AND graphversion_id=?",
				new BatchPreparedStatementSetter() {
					IPixelCut pixelCut;
					
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						pixelCut = xInfoList.get(i);
						int pos = 1;
						ps.setDouble(pos++, pixelCut.getStartCutRight());
						ps.setDouble(pos++, pixelCut.getStartCutLeft());
						ps.setDouble(pos++, pixelCut.getEndCutRight());
						ps.setDouble(pos++, pixelCut.getEndCutLeft());
						ps.setLong(pos++, pixelCut.getSegmentId());
						if (pixelCut.getGraphVersionId() != null) {
							ps.setLong(pos++, pixelCut.getGraphVersionId());
						} else {
							ps.setNull(pos++, Types.BIGINT);
						}
					}
					
					@Override
					public int getBatchSize() {
						return xInfoList.size();
					}
				}
			);
		}
	}

	@Override
	public void delete(String graphName, String version, IPixelCut xInfo) throws GraphNotExistsException {
		if (xInfo.getGraphVersionId() == null) {
			Long graphVersionId = getGraphVersionId(graphName, version);
			xInfo.setGraphVersionId(graphVersionId);
		}
		getJdbcTemplate().update("DELETE FROM " + schema + TABLE_NAME + " WHERE segment_id=? AND graphversion_id=?", xInfo.getSegmentId(), xInfo.getGraphVersionId());
	}

	@Override
	public void deleteAll(String graphName, String version) throws GraphNotExistsException {
		Long graphVersionId = getGraphVersionId(graphName, version);
		getJdbcTemplate().update("DELETE FROM " + schema + TABLE_NAME + " WHERE graphversion_id=?", graphVersionId);
	}

	private void preProcessList(String graphName, String version, List<IPixelCut> xInfoList) throws GraphNotExistsException {
		if (!xInfoList.isEmpty()) {
			Long graphVersionId = getGraphVersionId(graphName, version);
			for (IPixelCut xi : xInfoList) {
				xi.setGraphVersionId(graphVersionId);
			}
		}
	}

	@Override
	protected String getSelectionAttributesAsString() {
		return rowMapper.getAttributes();
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

}