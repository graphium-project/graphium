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
package at.srfg.graphium.postgis.persistence.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
public class SourceRowMapper implements RowMapper<ISource> {

	@Override
	public ISource mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Source(rs.getInt("id"), rs.getString("name"));
	}

}
