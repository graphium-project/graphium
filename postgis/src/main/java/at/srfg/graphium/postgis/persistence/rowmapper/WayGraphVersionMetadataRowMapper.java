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
import java.util.Map;

import org.postgis.jts.JtsBinaryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
public class WayGraphVersionMetadataRowMapper implements RowMapper<IWayGraphVersionMetadata> {

	protected static Logger log = LoggerFactory.getLogger(WayGraphVersionMetadataRowMapper.class);

	private JtsBinaryParser bp = new JtsBinaryParser();
	
	@Override
	public IWayGraphVersionMetadata mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		IWayGraphVersionMetadata md = new WayGraphVersionMetadata(rs.getLong("id"), rs.getLong("graph_id"), rs.getString("graphname"), rs.getString("version"), 
				rs.getString("origin_graphname"), rs.getString("origin_version"), State.valueOf(rs.getString("state")), 
				rs.getTimestamp("valid_from"), rs.getTimestamp("valid_to"), 
				(Polygon) bp.parse(rs.getBytes("covered_area")),
				rs.getInt("segments_count"), rs.getInt("connections_count"), RowMapperUtils.convertAccessTypes(rs, "accesstypes"), 
				(Map<String, String>) rs.getObject("tags"), new Source(rs.getInt("source_id"), rs.getString("source_name")), 
				rs.getString("type"), rs.getString("description"), rs.getTimestamp("creation_timestamp"), 
				rs.getTimestamp("storage_timestamp"), rs.getString("creator"), rs.getString("origin_url"));

		return md;
	}

}
