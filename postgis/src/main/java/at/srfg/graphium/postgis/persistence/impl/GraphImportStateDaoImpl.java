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

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.persistence.IGraphImportStateDao;
import at.srfg.graphium.model.management.IGraphImportState;

public class GraphImportStateDaoImpl extends AbstractDaoImpl implements IGraphImportStateDao {

	private final String GRAPH_IMPORT_STATES_TABLE = "graph_import_states";
	
	private RowMapper<IGraphImportState> rowMapper;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void update(IGraphImportState graphImportState) {
		Object[] args = new Object[3];
		args[0] = graphImportState.getServerName();
		args[1] = graphImportState.getViewName();
		args[2] = graphImportState.getVersion();

		Boolean entryExists = getJdbcTemplate().queryForObject("SELECT EXISTS (SELECT servername FROM " + schema + 
				GRAPH_IMPORT_STATES_TABLE + " WHERE servername = ? AND viewname = ? AND version = ?)", args, Boolean.class);
		
		if (entryExists != null && entryExists) {
			args = new Object[4];
			args[0] = graphImportState.getState().toString();
			args[1] = graphImportState.getServerName();
			args[2] = graphImportState.getViewName();
			args[3] = graphImportState.getVersion();
			getJdbcTemplate().update("UPDATE " + schema + GRAPH_IMPORT_STATES_TABLE + " SET state = ? "
					+ "WHERE servername = ? AND viewname = ? AND version = ?",
					args);
		} else {
			args = new Object[4];
			args[0] = graphImportState.getServerName();
			args[1] = graphImportState.getViewName();
			args[2] = graphImportState.getVersion();
			args[3] = graphImportState.getState().toString();
			getJdbcTemplate().update("INSERT INTO " + schema + GRAPH_IMPORT_STATES_TABLE
					+ " (servername, viewname, version, state, timestamp) "
					+ " VALUES (?, ?, ?, ?, now())",
					args);			
		}

	}

	@Override
	public List<IGraphImportState> listGraphImportStatesForGraphVersion(String graphName, String version) {
		Object[] args = new Object[2];
		args[0] = graphName;
		args[1] = version;
		
		return getJdbcTemplate().query("SELECT * FROM " + schema + GRAPH_IMPORT_STATES_TABLE + " WHERE viewname = ? AND version = ?", 
				args, rowMapper);
	}

	public RowMapper<IGraphImportState> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<IGraphImportState> rowMapper) {
		this.rowMapper = rowMapper;
	}

}
