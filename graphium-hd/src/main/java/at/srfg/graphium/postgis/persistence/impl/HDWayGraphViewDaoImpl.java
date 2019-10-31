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
package at.srfg.graphium.postgis.persistence.impl;

import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.hd.IHDWaySegment;

/**
 * @author mwimmer
 *
 */
public class HDWayGraphViewDaoImpl<W extends IHDWaySegment, X extends ISegmentXInfo> extends WayGraphViewDaoImpl<W, X> {

	protected String[] hdSegmentsAttributes = new String[] {
			"id AS id",
			"graphversion_id AS " + QUERY_PREFIX + "_graphversion_id",
			"geometry AS " + QUERY_PREFIX + "_geometry",
			"length AS " + QUERY_PREFIX + "_length",
			"name AS " + QUERY_PREFIX + "_name",
			"maxspeed_tow AS " + QUERY_PREFIX + "_maxspeed_tow",
			"maxspeed_bkw AS " + QUERY_PREFIX + "_maxspeed_bkw",
			"speed_calc_tow AS " + QUERY_PREFIX + "_speed_calc_tow",
			"speed_calc_bkw AS " + QUERY_PREFIX + "_speed_calc_bkw",
			"lanes_tow AS " + QUERY_PREFIX + "_lanes_tow",
			"lanes_bkw AS " + QUERY_PREFIX + "_lanes_bkw",
			"frc AS " + QUERY_PREFIX + "_frc",
			"formofway AS " + QUERY_PREFIX + "_formofway",
			"streettype AS " + QUERY_PREFIX + "_streettype",
			"way_id AS " + QUERY_PREFIX + "_way_id",
			"startnode_id AS " + QUERY_PREFIX + "_startnode_id",
			"startnode_index AS " + QUERY_PREFIX + "_startnode_index",
			"endnode_id AS " + QUERY_PREFIX + "_endnode_id",
			"endnode_index AS " + QUERY_PREFIX + "_endnode_index",
			"access_tow::integer[] AS " + QUERY_PREFIX + "_access_tow",
			"access_bkw::integer[] AS " + QUERY_PREFIX + "_access_bkw",
			"tunnel AS " + QUERY_PREFIX + "_tunnel",
			"bridge AS " + QUERY_PREFIX + "_bridge",
			"urban AS " + QUERY_PREFIX + "_urban", 
			"timestamp AS " + QUERY_PREFIX + "_timestamp",
			"tags AS " + QUERY_PREFIX + "_tags",
			"left_boarder_geometry AS " + QUERY_PREFIX + "_left_boarder_geometry",
			"left_boarder_startnode_id AS " + QUERY_PREFIX + "_left_boarder_startnode_id",
			"left_boarder_endnode_id AS " + QUERY_PREFIX + "_left_boarder_endnode_id",
			"right_boarder_geometry AS " + QUERY_PREFIX + "_right_boarder_geometry",
			"right_boarder_startnode_id AS " + QUERY_PREFIX + "_right_boarder_startnode_id",
			"right_boarder_endnode_id AS " + QUERY_PREFIX + "_right_boarder_endnode_id"
		};

	protected String[] getSegmentsAttributes() {
		return hdSegmentsAttributes;
	}

	@Override
	protected String createDefaultViewStatement(String wayGraph, String attributes) {
		return "CREATE OR REPLACE VIEW " + schema + DEFAULT_VIEW_PREFIX + "_" + wayGraph + " AS " +
				"SELECT " + attributes +
			    ", COALESCE(startnodesegments.startnodesegments, '{NULL}') AS startnodesegments" + 
			    ", COALESCE(endnodesegments.endnodesegments, '{NULL}') AS endnodesegments " +
			    "FROM " + schema + HDWayGraphWriteDaoImpl.PARENT_HDSEGMENT_TABLE_NAME + " AS " + waySegmentRowMapper.getPrefix() + 
			   	" LEFT OUTER JOIN" +
			   	" LATERAL (select array_agg(con_start.*::character varying) AS startnodesegments," +
				"  con_start.graphversion_id" +
			    " FROM graphs.waysegment_connections con_start" + 
			    " WHERE con_start.node_id = wayseg.startnode_id" +
			    "   AND con_start.from_segment_id = wayseg.id" +
			    " GROUP BY con_start.from_segment_id, con_start.graphversion_id" +
			    " ) AS startnodesegments" +
			    " ON startnodesegments.graphversion_id = wayseg.graphversion_id" +
			    " LEFT OUTER JOIN" +
			    " LATERAL (select array_agg(con_end.*::character varying) AS endnodesegments,"+
				"  con_end.graphversion_id" +
				" FROM graphs.waysegment_connections con_end" +
				" WHERE con_end.node_id = wayseg.endnode_id" +
				"  AND con_end.from_segment_id = wayseg.id" +
				" GROUP BY con_end.from_segment_id, con_end.graphversion_id" +
				" ) AS endnodesegments" +
				" ON endnodesegments.graphversion_id = wayseg.graphversion_id";
	}

}
