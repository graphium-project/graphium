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
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.tutorial.xinfo.dao;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.model.view.impl.WayGraphView;
import at.srfg.graphium.postgis.persistence.impl.WayGraphViewDaoImpl;

/**
 * @author mwimmer
 */
public class ViewDao<T extends IBaseSegment, X extends ISegmentXInfo> extends WayGraphViewDaoImpl<T, X> {

	public void saveTestXInfoView(IWayGraph graph, String viewName) {
		getJdbcTemplate().execute(
				"CREATE OR REPLACE VIEW graphs.vw_" + viewName + " AS " + 
				" SELECT wayseg.*, " +
				"		rdm.segment_id AS rdm_segment_id, " +
				"	    rdm.direction_tow AS rdm_direction_tow, " +
				"	    rdm.graphversion_id AS rdm_graphversion_id, " +
				"	    rdm.start_offset AS rdm_start_offset, " +
				"	    rdm.end_offset AS rdm_end_offset, " +
				"	    rdm.type AS rdm_type " +
				" FROM ( " +
				"  SELECT wayseg.id, " + 
				"    wayseg.graphversion_id AS wayseg_graphversion_id, " + 
				"    wayseg.geometry AS wayseg_geometry, " + 
				"    wayseg.length AS wayseg_length, " + 
				"    wayseg.name AS wayseg_name, " + 
				"    wayseg.maxspeed_tow AS wayseg_maxspeed_tow, " + 
				"    wayseg.maxspeed_bkw AS wayseg_maxspeed_bkw, " + 
				"    wayseg.speed_calc_tow AS wayseg_speed_calc_tow, " + 
				"    wayseg.speed_calc_bkw AS wayseg_speed_calc_bkw, " + 
				"    wayseg.lanes_tow AS wayseg_lanes_tow, " + 
				"    wayseg.lanes_bkw AS wayseg_lanes_bkw, " + 
				"    wayseg.frc AS wayseg_frc, " + 
				"    wayseg.formofway AS wayseg_formofway, " + 
				"    wayseg.streettype AS wayseg_streettype, " + 
				"    wayseg.way_id AS wayseg_way_id, " + 
				"    wayseg.startnode_id AS wayseg_startnode_id, " + 
				"    wayseg.startnode_index AS wayseg_startnode_index, " + 
				"    wayseg.endnode_id AS wayseg_endnode_id, " + 
				"    wayseg.endnode_index AS wayseg_endnode_index, " + 
				"    wayseg.access_tow::integer[] AS wayseg_access_tow, " + 
				"    wayseg.access_bkw::integer[] AS wayseg_access_bkw, " + 
				"    wayseg.tunnel AS wayseg_tunnel, " + 
				"    wayseg.bridge AS wayseg_bridge, " + 
				"    wayseg.urban AS wayseg_urban, " + 
				"    wayseg.\"timestamp\" AS wayseg_timestamp, " + 
				"    wayseg.tags AS wayseg_tags, " + 
				"    array_agg(con_start.*::character varying) AS startnodesegments, " + 
				"    array_agg(con_end.*::character varying) AS endnodesegments " + 
				"   FROM graphs.waysegments wayseg " + 
				"     LEFT OUTER JOIN graphs.waysegment_connections con_start ON con_start.node_id = wayseg.startnode_id AND con_start.from_segment_id = wayseg.id AND con_start.to_segment_id <> wayseg.id AND con_start.graphversion_id = wayseg.graphversion_id " + 
				"     LEFT OUTER JOIN graphs.waysegment_connections con_end ON con_end.node_id = wayseg.endnode_id AND con_end.from_segment_id = wayseg.id AND con_end.to_segment_id <> wayseg.id AND con_end.graphversion_id = wayseg.graphversion_id " + 
				"  GROUP BY wayseg.id) AS wayseg " +
				"LEFT OUTER JOIN graphs.roaddamages rdm ON (rdm.segment_id = wayseg.id AND rdm.graphversion_id = wayseg.wayseg_graphversion_id);" 
				);
		
		IWayGraphView view = new WayGraphView(viewName, graph, "vw_roaddamages", true, null, 0, 0, null);
		saveView(view);
	}
	
}
