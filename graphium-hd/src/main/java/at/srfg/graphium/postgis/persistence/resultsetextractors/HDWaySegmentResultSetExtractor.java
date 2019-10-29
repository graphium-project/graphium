/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.postgis.persistence.resultsetextractors;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.postgis.persistence.rowmapper.ColumnFinder;

/**
 * @author mwimmer
 *
 */
public class HDWaySegmentResultSetExtractor<S extends IBaseSegment, W extends IHDWaySegment> extends WaySegmentResultSetExtractor<S, W> {
	
	private static final String HD_ATTRIBUTES = ATTRIBUTES + 
			"ST_AsEWKB(left_boarder_geometry) AS " + QUERY_PREFIX + "_left_boarder_geometry, " +
			"left_boarder_startnode_id AS " + QUERY_PREFIX + "_left_boarder_startnode_id, " +
			"left_boarder_endnode_id AS " + QUERY_PREFIX + "_left_boarder_endnode_id, " +
			"ST_AsEWKB(right_boarder_geometry) AS " + QUERY_PREFIX + "_right_boarder_geometry, " +
			"right_boarder_startnode_id AS " + QUERY_PREFIX + "_right_boarder_startnode_id, " +
			"right_boarder_endnode_id AS " + QUERY_PREFIX + "_right_boarder_endnode_id, ";

	/**
	 * @param waySegment
	 * @param rs
	 * @throws SQLException 
	 */
	@Override
	protected void mapSegment(W waySegment, ResultSet rs) throws SQLException {
		super.mapSegment(waySegment, rs);
		
		ColumnFinder colFinder = new ColumnFinder(rs);
		
		waySegment.setLeftBoarderGeometry((LineString) bp.parse(rs.getBytes(QUERY_PREFIX + "_left_boarder_geometry")));
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_left_boarder_startnode_id") > -1) {
			waySegment.setLeftBoarderStartNodeId(rs.getLong(QUERY_PREFIX + "_left_boarder_startnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_left_boarder_endnode_id") > -1) {
			waySegment.setLeftBoarderEndNodeId(rs.getLong(QUERY_PREFIX + "_left_boarder_endnode_id"));
		}
		
		waySegment.setRightBoarderGeometry((LineString) bp.parse(rs.getBytes(QUERY_PREFIX + "_right_boarder_geometry")));
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_right_boarder_startnode_id") > -1) {
			waySegment.setRightBoarderStartNodeId(rs.getLong(QUERY_PREFIX + "_right_boarder_startnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_right_boarder_endnode_id") > -1) {
			waySegment.setRightBoarderEndNodeId(rs.getLong(QUERY_PREFIX + "_right_boarder_endnode_id"));
		}
	}

}