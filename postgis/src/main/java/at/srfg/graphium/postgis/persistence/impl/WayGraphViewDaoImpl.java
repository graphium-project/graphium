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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.io.WKTWriter;

import at.srfg.graphium.core.exception.GraphViewNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.model.view.impl.WayGraphView;
import at.srfg.graphium.postgis.persistence.ISegmentResultSetExtractor;
import at.srfg.graphium.postgis.persistence.resultsetextractors.ISegmentResultSetExtractorFactory;
import at.srfg.graphium.postgis.utils.ViewParseUtil;

public class WayGraphViewDaoImpl<T extends IBaseSegment, X extends ISegmentXInfo> extends AbstractWayGraphDaoImpl implements IWayGraphViewDao {

	private final String DEFAULT_VIEW_PREFIX = "vw";
	private static final String QUERY_PREFIX = "wayseg";
	private String[] segmentsAttributes = new String[] {
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
		"tags AS " + QUERY_PREFIX + "_tags"
	};
	
	private WKTWriter wktWriter;
	private RowMapper<IWayGraphView> rowMapper;
	private ISegmentResultSetExtractor<IBaseSegment, ISegmentXInfo> waySegmentRowMapper;
	private ISegmentResultSetExtractorFactory resultSetExtractorFactory;
	
	@PostConstruct
	public void setup() {
		wktWriter = new WKTWriter();
	}

	@Transactional(readOnly=false, propagation=Propagation.MANDATORY)
	public void saveView(IWayGraphView view) {
		Object[] args = new Object[7];
		args[0] = view.getViewName();
		args[1] = view.getGraph().getId();
		args[2] = view.getDbViewName();
		args[3] = view.isWaySegmentsIncluded();
		if (view.getCoveredArea() != null) {
			args[4] = "SRID=4326;"+wktWriter.write(view.getCoveredArea());
		} else {
			args[4] = null;
		}
		args[5] = view.getSegmentsCount();
		args[6] = view.getConnectionsCount();

		getJdbcTemplate().update("INSERT INTO " + schema + VIEW_TABLE_NAME + 
				" (viewname, graph_id, dbviewname, waysegments_included, covered_area, segments_count, connections_count) " +
				"VALUES (?,?,?,?,?,?,?)", args);
	}
	
	public void saveDefaultView(IWayGraph wayGraph) {

		boolean defaultViewExits = viewExists(wayGraph.getName()); // currently should be graphname
		if (!defaultViewExits) {
			StringBuilder attributes = new StringBuilder();
			for (String attrib : segmentsAttributes) {
				if (attributes.length() > 0) {
					attributes.append(", ");
				}
				if (attrib.contains("geometry")) {
					attributes.append(attrib);
				} else {
					attributes.append(waySegmentRowMapper.getPrefix() + "." + attrib);
				}
			}
			
			String dbView = "CREATE OR REPLACE VIEW " + schema + DEFAULT_VIEW_PREFIX + "_" + wayGraph.getName() + " AS " +
					"SELECT " + attributes.toString() +
				    ", array_agg(distinct con_start.*::character varying) AS startnodesegments," +
					" array_agg(distinct con_end.*::character varying) AS endnodesegments " +
					"FROM " + schema + PARENT_SEGMENT_TABLE_NAME + " AS " + waySegmentRowMapper.getPrefix() + 
				   	" LEFT OUTER JOIN " + schema + PARENT_CONNECTION_TABLE_NAME + " con_start " + 
					" ON (con_start.node_id = " + waySegmentRowMapper.getPrefix() + ".startnode_id AND con_start.from_segment_id = " + waySegmentRowMapper.getPrefix() + ".id AND " +
					" con_start.to_segment_id <> " + waySegmentRowMapper.getPrefix() + ".id AND con_start.graphversion_id = " + waySegmentRowMapper.getPrefix() + ".graphversion_id)" +
					" LEFT OUTER JOIN " + schema + PARENT_CONNECTION_TABLE_NAME + " con_end " +
					" ON (con_end.node_id = " + waySegmentRowMapper.getPrefix() + ".endnode_id AND con_end.from_segment_id = " + waySegmentRowMapper.getPrefix() + ".id " +
					" AND con_end.to_segment_id <> " + waySegmentRowMapper.getPrefix() + ".id AND con_end.graphversion_id = " + waySegmentRowMapper.getPrefix() + ".graphversion_id)" +
					" GROUP BY wayseg.id";   
			getJdbcTemplate().execute(dbView);

			// create new default view
			IWayGraphView view = new WayGraphView(wayGraph.getName(), wayGraph, DEFAULT_VIEW_PREFIX + "_" + wayGraph.getName(), true, null, 0, 0, null);
			saveView(view);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<IWayGraphView> getViewsForGraph(String graphName) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("graphName", graphName);
		
		return getNamedParameterJdbcTemplate().query("SELECT view.*, graph.id AS graph_id, graph.name AS graph_name "
				+ "FROM " + schema + VIEW_TABLE_NAME + " view, " + schema + WAYGRAPH_TABLE_NAME + " graph "
				+ "WHERE graph.id = view.graph_id AND graph.name = :graphName", paramMap, rowMapper);
	}
	
	@Override
	@Transactional(readOnly=true)
	public boolean viewExists(String viewName) {
		List<IWayGraphView> views  = queryForViews(viewName);
		return !(views == null || views.isEmpty());
	}
	
	@Override
	@Transactional(readOnly=true)
	public IWayGraphView getView(String viewName) throws GraphViewNotExistsException {
		List<IWayGraphView> views  = queryForViews(viewName);
		if (views == null || views.isEmpty()) {
			String msg = "View with name " + viewName + " does not exist";
			throw new GraphViewNotExistsException(msg, viewName);
		} else {
			return views.get(0);
		}
	}
	
	protected List<IWayGraphView> queryForViews(String viewName) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("viewName", viewName);
		
		List<IWayGraphView> views = getNamedParameterJdbcTemplate().query("SELECT view.*, graph.id AS graph_id, graph.name AS graph_name "
				+ "FROM " + schema + VIEW_TABLE_NAME + " view, " + schema + WAYGRAPH_TABLE_NAME + " graph "
				+ "WHERE graph.id = view.graph_id AND view.viewname = :viewName", paramMap, rowMapper);
		return views;
	}
	
	@Override
	public boolean isDefaultView(String viewName) {
		Long exists = getNamedParameterJdbcTemplate().query("SELECT count(1) from " + schema + WAYGRAPH_TABLE_NAME + " where name = :viewName",
				Collections.singletonMap("viewName", viewName), new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						rs.next();
						return rs.getLong(1);
					}
				});
		return exists > 0;
	}
	
	@Override
	@Transactional(readOnly=true)
	public int getSegmentsCount(IWayGraphView view, String graphVersion) {
		String query;
		// parse filter query to detect concerned tables
		Set<String> tableAliases = ViewParseUtil.parseTableAliases(getViewDefinition(view));
			
		// use object factory to retrieved all needed ResultSetExtractors
		ISegmentResultSetExtractor<T, X> rsExtractor = resultSetExtractorFactory.getResultSetExtractor(tableAliases);
			
		// prepare filter query (change parent table names with versioned table names; change "SELECT * " to "SELECT id, name, ...", 
		// 		use aliases to avoid duplicate field names, ...)
		query = ViewParseUtil.prepareViewFilterQuery(view, graphVersion, schema, rsExtractor, null);
			
		// count segments
		Integer count = getNamedParameterJdbcTemplate().queryForObject("SELECT count(1) FROM (" + query + ") AS cnt;", 
				new HashMap<String, Object>(), Integer.class);
		if (count == null) {
			count = 0;
		}
		return count;
	}

	@Override
	public String getViewDefinition(IWayGraphView view) {
		String query = "select view_definition from information_schema.views where table_name = '" + view.getDbViewName() + 
					   "' AND table_schema = '" + schema.replace(".", "") + "';";
		return getJdbcTemplate().queryForObject(query, String.class);
	}
	
	public RowMapper<IWayGraphView> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<IWayGraphView> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public ISegmentResultSetExtractor<IBaseSegment, ISegmentXInfo> getWaySegmentRowMapper() {
		return waySegmentRowMapper;
	}

	public void setWaySegmentRowMapper(ISegmentResultSetExtractor<IBaseSegment, ISegmentXInfo> waySegmentRowMapper) {
		this.waySegmentRowMapper = waySegmentRowMapper;
	}
	
}