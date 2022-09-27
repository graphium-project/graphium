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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.hd.impl.HDWaySegment;
import at.srfg.graphium.model.impl.WaySegmentConnection;
import at.srfg.graphium.postgis.persistence.rowmapper.ColumnFinder;

/**
 * @author mwimmer
 *
 */
public class HDWaySegmentResultSetExtractor<S extends IBaseSegment, W extends IHDWaySegment> extends WaySegmentResultSetExtractor<S, W> {
	
	private static final String HD_ATTRIBUTES = ATTRIBUTES + 
			"ST_AsEWKB(left_border_geometry) AS " + QUERY_PREFIX + "_left_border_geometry, " +
			"left_border_startnode_id AS " + QUERY_PREFIX + "_left_border_startnode_id, " +
			"left_border_endnode_id AS " + QUERY_PREFIX + "_left_border_endnode_id, " +
			"ST_AsEWKB(right_border_geometry) AS " + QUERY_PREFIX + "_right_border_geometry, " +
			"right_border_startnode_id AS " + QUERY_PREFIX + "_right_border_startnode_id, " +
			"right_border_endnode_id AS " + QUERY_PREFIX + "_right_border_endnode_id, ";

	/**
	 * @param waySegment
	 * @param rs
	 * @throws SQLException 
	 */
	@Override
	protected void mapSegment(W waySegment, ResultSet rs) throws SQLException {
		super.mapSegment(waySegment, rs);
		
		ColumnFinder colFinder = new ColumnFinder(rs);
		
		waySegment.setLeftBorderGeometry((LineString) bp.parse(rs.getBytes(QUERY_PREFIX + "_left_border_geometry_ewkb")));
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_left_border_startnode_id") > -1) {
			waySegment.setLeftBorderStartNodeId(rs.getLong(QUERY_PREFIX + "_left_border_startnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_left_border_endnode_id") > -1) {
			waySegment.setLeftBorderEndNodeId(rs.getLong(QUERY_PREFIX + "_left_border_endnode_id"));
		}
		
		waySegment.setRightBorderGeometry((LineString) bp.parse(rs.getBytes(QUERY_PREFIX + "_right_border_geometry_ewkb")));
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_right_border_startnode_id") > -1) {
			waySegment.setRightBorderStartNodeId(rs.getLong(QUERY_PREFIX + "_right_border_startnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_right_border_endnode_id") > -1) {
			waySegment.setRightBorderEndNodeId(rs.getLong(QUERY_PREFIX + "_right_border_endnode_id"));
		}
	}

	protected IWaySegmentConnection parseSerializedCon(String serializedCon) {
		return HDResultSetExtractorUtils.parseSerializedCon(serializedCon);
	}
	
	protected W createSegment() {
		return (W) new HDWaySegment();
	}

	@Override
	public String getGeometryManipulationClause() {
		return ", st_asewkb(wayseg_geometry) AS wayseg_geometry_ewkb, " +
				 "st_asewkb(wayseg_left_border_geometry) AS wayseg_left_border_geometry_ewkb, " +
				 "st_asewkb(wayseg_right_border_geometry) AS wayseg_right_border_geometry_ewkb";
	}
}