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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.model.ISource;

/**
 * @author mwimmer
 *
 */
public class SourceDaoImpl extends AbstractDaoImpl implements ISourceDao {

	private RowMapper<ISource> rowMapper;
	
	@Override
	@Transactional(readOnly=true)
	public ISource getSource(int id) {
		Object[] args = new Object[1];
		args[0] = id;
		try {
			ISource source = getJdbcTemplate().queryForObject("SELECT * FROM " + schema + "sources WHERE id = ?", args, rowMapper);
			return source;
		} catch (DataAccessException e) {
			return null;
		}
	}


	@Override
	public List<ISource> getSources() {
		return getJdbcTemplate().query("SELECT * FROM " + schema + "sources", rowMapper);
	}

	@Override
	public ISource getSource(String name) {
		Object[] args = new Object[1];
		args[0] = name;
		try {
			ISource source = getJdbcTemplate().queryForObject("SELECT * FROM " + schema + "sources WHERE name = ?", args, rowMapper);
			return source;
		} catch (DataAccessException e) {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void save(ISource source) {
		Map<String, Object>  params = new HashMap<String, Object>(); 
		params.put("sourceName", source.getName());
		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		getNamedParameterJdbcTemplate().update("INSERT INTO " + schema + "sources (name) VALUES (:sourceName)", 
				sqlParameterSource, keyHolder, new String[] {"id"});
		
		int id = Integer.class.cast(keyHolder.getKey());
		source.setId(id);
	}

	public RowMapper<ISource> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<ISource> rowMapper) {
		this.rowMapper = rowMapper;
	}

}