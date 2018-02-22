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
import org.springframework.jdbc.core.RowMapper;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.model.view.impl.WayGraphView;

public class WayGraphViewRowMapper implements RowMapper<IWayGraphView> {
	
	private JtsBinaryParser bp = new JtsBinaryParser();
	
	@Override
	public IWayGraphView mapRow(ResultSet rs, int rowNum) throws SQLException {
		byte[] coveredAreaBytes = rs.getBytes("covered_area");
		return new WayGraphView(rs.getString("viewname"), 
								new WayGraph(rs.getLong("graph_id"), rs.getString("graph_name")), 
								rs.getString("dbviewname"),
								rs.getBoolean("waysegments_included"),
								(coveredAreaBytes == null ? null : (Polygon) bp.parse(coveredAreaBytes)), 
								rs.getInt("segments_count"), 
								rs.getInt("connections_count"),
								(Map<String, String>) rs.getObject("tags"));
	}
	
}
