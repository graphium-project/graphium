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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphMetadataFactory;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */
public class WayGraphVersionMetadataDaoImpl extends AbstractWayGraphDaoImpl implements
		IWayGraphVersionMetadataDao {
	
	private final String QUERY_ATTRIB_SELECTION_CLAUSE = "SELECT md.id, graph_id, graphname, version, origin_graphname, origin_version, state," +
			" valid_from, valid_to, ST_AsEWKB(covered_area) AS covered_area, segments_count, connections_count, accesstypes::integer[], tags," +
			" source.id AS source_id, source.name AS source_name, type, description, creation_timestamp, storage_timestamp, creator, origin_url";
	
	private final String QUERY_WITH_VIEW_ATTRIB_SELECTION_CLAUSE = "SELECT md.id, md.graph_id, vw.viewname AS graphname, version, md.graphname AS origin_graphname, "
			+ "origin_version, state, valid_from, valid_to, "
			+ "CASE WHEN vw.covered_area IS NOT NULL THEN ST_AsEWKB(vw.covered_area) ELSE ST_AsEWKB(md.covered_area) END AS covered_area, "
			+ "vw.segments_count, vw.connections_count, accesstypes::integer[], vw.tags, source.id AS source_id, source.name AS source_name, type, "
			+ "description, vw.creation_timestamp, storage_timestamp, creator, origin_url";
	
	private RowMapper<IWayGraphVersionMetadata> metadataRowMapper;
	private RowMapper<IWayGraph> wayGraphRowMapper;
	private WKTWriter wktWriter;
	private Set<State> statesOfCurrentGraph;
	private IWayGraphMetadataFactory metadataFactory;
	
	@PostConstruct
	public void setup() {
		wktWriter = new WKTWriter();
		statesOfCurrentGraph = new HashSet<>();
		statesOfCurrentGraph.add(State.ACTIVE);
	}

	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata() {
		return metadataFactory.newWayGraphVersionMetadata();
	}

	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata(long id, long graphId, String graphName, String version,
			String originGraphName, String originVersion, State state, 
			Date validFrom, Date validTo, Polygon coveredArea,
			int segmentsCount, int connectionsCount, Set<Access> accessTypes,
			Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl) {
		return metadataFactory.newWayGraphVersionMetadata(id, graphId, graphName, version, originGraphName, originVersion, state, 
				validFrom, validTo, coveredArea, segmentsCount, connectionsCount, accessTypes, tags, source, type, 
				description, creationTimestamp, storageTimestamp, creator, originUrl);
	}
	
	@Override
	public IWayGraphVersionMetadata getWayGraphVersionMetadata(long id) {
		Object[] args = new Object[1];
		args[0] = id;
		return getJdbcTemplate().queryForObject(QUERY_ATTRIB_SELECTION_CLAUSE +
				" FROM " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE md.id = ?", args, metadataRowMapper);
	}

	@Override
	@Transactional(readOnly=true)
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName) {
		return getCurrentWayGraphVersionMetadata(graphName, statesOfCurrentGraph);
	}
	
	@Override
	@Transactional(readOnly=true)
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName, Set<State> states) {


		// TODO: Consider view names!!!
		
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		
		List<String> stateNames = getStatesAsString(states);
		if (!stateNames.isEmpty()) {
			paramMap.put("states", stateNames);
		}
		
		List<IWayGraphVersionMetadata> md = getNamedParameterJdbcTemplate().query(QUERY_ATTRIB_SELECTION_CLAUSE  +
				" FROM " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE graphname = :graphName" +
				" AND state IN (:states)" +
				" ORDER BY valid_from DESC LIMIT 1", 
				paramMap, metadataRowMapper);
		
		if (md != null && !md.isEmpty()) {
			return md.get(0);
		} else {
			return null;
		}
	}
	
	protected List<String> getStatesAsString(Set<State> states) {
		List<String> stateNames = new ArrayList<>();
		if (states != null) {
			for (State state : states) {
				stateNames.add(state.name());
			}
		}
		return stateNames;
	}
	
	@Override
	@Transactional(readOnly=true)
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadataForView(String viewName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("viewName", viewName);
//		paramMap.put("states", statesOfCurrentGraph);
		List<String> stateNames = getStatesAsString(statesOfCurrentGraph);
		if (!stateNames.isEmpty()) {
			paramMap.put("states", stateNames);
		}
		
		List<IWayGraphVersionMetadata> md = getNamedParameterJdbcTemplate().query(
				"WITH views AS (SELECT * FROM " + schema + VIEW_TABLE_NAME + " WHERE viewname = :viewName )" +
						QUERY_WITH_VIEW_ATTRIB_SELECTION_CLAUSE + " FROM views AS vw, " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE md.graph_id = vw.graph_id" +
				" AND state IN (:states)" +
				" ORDER BY valid_from DESC LIMIT 1", 
				paramMap, metadataRowMapper);
		
		if (md != null && !md.isEmpty()) {
			return md.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public IWayGraphVersionMetadata getWayGraphVersionMetadata(
			String graphName, String version) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		paramMap.put("version", version);
		
		List<IWayGraphVersionMetadata> md = getNamedParameterJdbcTemplate().query(QUERY_ATTRIB_SELECTION_CLAUSE  +
				" FROM " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE graphname = :graphName AND version = :version", 
				paramMap, metadataRowMapper);
		
		if (md != null && !md.isEmpty()) {
			return md.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public IWayGraphVersionMetadata getWayGraphVersionMetadataForView(
			String viewName, String version) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("viewName", viewName);
		paramMap.put("version", version);
		
		List<IWayGraphVersionMetadata> md = getNamedParameterJdbcTemplate().query(
				"WITH views AS (SELECT * FROM " + schema + VIEW_TABLE_NAME + " WHERE viewname = :viewName )" +
				QUERY_WITH_VIEW_ATTRIB_SELECTION_CLAUSE + " FROM views AS vw, " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE md.graph_id = vw.graph_id AND version = :version", 
				paramMap, metadataRowMapper);
		
		if (md != null && !md.isEmpty()) {
			return md.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName) {
		Object[] args = new Object[1];
		args[0] = graphName;
		return getJdbcTemplate().query(QUERY_ATTRIB_SELECTION_CLAUSE +
				" FROM " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE graphname = ? ORDER BY md.id", 
				args, metadataRowMapper);
	}

	@Override
	@Transactional(readOnly=true)
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataListForOriginGraphname(
			String originGraphName) {
		Object[] args = new Object[1];
		args[0] = originGraphName;
		return getJdbcTemplate().query(QUERY_ATTRIB_SELECTION_CLAUSE +
				" FROM " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + schema + SOURCE_TABLE_NAME + 
				" AS source ON source.id = md.source_id WHERE origin_graphname = ?", 
				args, metadataRowMapper);
	}

	@Override
	@Transactional(readOnly=true)
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName, State state, Date validFrom,
			Date validTo, Set<Access> accessTypes) {

		StringBuilder query = new StringBuilder("WITH views AS (SELECT * FROM " + schema + VIEW_TABLE_NAME + " WHERE viewname = :graphName )" +
				QUERY_WITH_VIEW_ATTRIB_SELECTION_CLAUSE + " FROM views AS vw, " + schema + METADATA_TABLE_NAME + " AS md LEFT JOIN " + 
				schema + SOURCE_TABLE_NAME + " AS source ON source.id = md.source_id WHERE md.graph_id = vw.graph_id");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		
		if (state != null) {
			query.append(" AND state = :state");
			paramMap.put("state", state.name());
		}
		
		if (validFrom != null && validTo != null) {
			query.append(" AND :validFrom <= :validTo AND valid_from <= :validTo AND (valid_to >= :validFrom OR valid_to IS NULL)");
			paramMap.put("validFrom", validFrom);
			paramMap.put("validTo", validTo);

		} else {
		
			if (validFrom != null) {
				query.append(" AND (valid_to >= :validFrom OR valid_to IS NULL)");
				paramMap.put("validFrom", validFrom);
			} else if (validTo != null) {
				query.append(" AND valid_from <= :validTo");
				paramMap.put("validTo", validTo);
			}
			
		}

		if (accessTypes != null) {
			query.append(" AND (");
			int i = 0;
			for (Access access : accessTypes) {
				if (i > 0) {
					query.append(" OR ");
				}
				query.append(access.getId() + " = ANY(access)");
				i++;
			}
			query.append(")");
		}

		return getNamedParameterJdbcTemplate().query(query.toString(), paramMap, metadataRowMapper);
	}

	@Override
	public void saveGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		Object[] args = new Object[20];
		try {
			args[0] = convertToArray(getJdbcTemplate().getDataSource().getConnection(), graphMetadata.getAccessTypes());
		} catch (SQLException e) {
			throw new InvalidDataAccessResourceUsageException(e.getMessage(), e);
		}
		args[1] = graphMetadata.getConnectionsCount();
		args[2] = "SRID=4326;"+wktWriter.write(graphMetadata.getCoveredArea());
		args[3] = graphMetadata.getState().name();
		args[4] = graphMetadata.getCreationTimestamp();
		args[5] = graphMetadata.getCreator();
		args[6] = graphMetadata.getDescription();
		args[7] = graphMetadata.getGraphId();
		args[8] = graphMetadata.getGraphName();
		args[9] = graphMetadata.getOriginGraphName();
		args[10] = graphMetadata.getOriginUrl();
		args[11] = graphMetadata.getOriginVersion();
		args[12] = graphMetadata.getSegmentsCount();
		args[13] = graphMetadata.getSource().getId();
		args[14] = graphMetadata.getStorageTimestamp();
		if (graphMetadata.getTags() != null) {
			args[15] = graphMetadata.getTags();
		} else {
			args[15] = null;
		}
		args[16] = graphMetadata.getType();
		args[17] = graphMetadata.getValidFrom();
		args[18] = graphMetadata.getValidTo();
		args[19] = graphMetadata.getVersion();

		getJdbcTemplate().update("INSERT INTO " + schema + METADATA_TABLE_NAME + " (accesstypes, connections_count, covered_area," +
				"state, creation_timestamp, creator, description, graph_id, graphname, origin_graphname, origin_url, origin_version, " +
				"segments_count, source_id, storage_timestamp, tags, type, valid_from, valid_to, version) " +
				"VALUES (?,?,ST_GeomFromEWKT(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", args);
	}

	@Override
	public void updateGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		Object[] args = new Object[18];
		try {
			args[0] = convertToArray(getJdbcTemplate().getDataSource().getConnection(), graphMetadata.getAccessTypes());
		} catch (SQLException e) {
			throw new InvalidDataAccessResourceUsageException(e.getMessage(), e);
		}
		args[1] = graphMetadata.getConnectionsCount();
		args[2] = "SRID=4326;"+wktWriter.write(graphMetadata.getCoveredArea());
		args[3] = graphMetadata.getCreationTimestamp();
		args[4] = graphMetadata.getCreator();
		args[5] = graphMetadata.getDescription();
		args[6] = graphMetadata.getOriginGraphName();
		args[7] = graphMetadata.getOriginUrl();
		args[8] = graphMetadata.getOriginVersion();
		args[9] = graphMetadata.getSegmentsCount();
		args[10] = graphMetadata.getSource().getId();
		args[11] = graphMetadata.getStorageTimestamp();
		if (graphMetadata.getTags() != null) {
			args[12] = graphMetadata.getTags();
		} else {
			args[12] = null;
		}
		args[13] = graphMetadata.getType();
		args[14] = graphMetadata.getValidFrom();
		args[15] = graphMetadata.getValidTo();
		args[16] = graphMetadata.getGraphName();
		args[17] = graphMetadata.getVersion();

		getJdbcTemplate().update("UPDATE " + schema + METADATA_TABLE_NAME + " SET accesstypes=?, connections_count=?, covered_area=ST_GeomFromEWKT(?)," +
				"creation_timestamp=?, creator=?, description=?, origin_graphname=?, origin_url=?, origin_version=?, " +
				"segments_count=?, source_id=?, storage_timestamp=?, tags=?, type=?, valid_from=?, valid_to=? " +
				"WHERE graphname =? AND version=?", args);
	}

	@Override
	//@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	@Transactional(readOnly=false)
	public void setGraphVersionState(String graphName, String version, State state) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		paramMap.put("version", version);
		paramMap.put("state", state.name());
		
		getNamedParameterJdbcTemplate().update("UPDATE " + schema + METADATA_TABLE_NAME + " SET state = :state " +
				"WHERE graphname = :graphName AND version = :version", paramMap);
	}

	@Override
	public void setValidToTimestampOfPredecessorGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphMetadata.getGraphName());
		paramMap.put("validTo", graphMetadata.getValidFrom());
		paramMap.put("state", graphMetadata.getState().name());
		
		getNamedParameterJdbcTemplate().update("UPDATE " + schema + METADATA_TABLE_NAME + " SET valid_to = :validTo " +
				"WHERE graphname = :graphName AND valid_from < :validTo AND valid_to IS NULL AND state = :state", 
				paramMap);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean checkIfGraphExists(String graphName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		return getNamedParameterJdbcTemplate().queryForObject("SELECT EXISTS (SELECT name FROM " + schema + "waygraphs WHERE name = :graphName)", paramMap, Boolean.class);
	}

	@Override
	@Transactional(readOnly=true)
	public IWayGraph getGraph(String graphName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		List<IWayGraph> wayGraphs = getNamedParameterJdbcTemplate().query("SELECT * FROM " + schema + "waygraphs WHERE name = :graphName", paramMap, wayGraphRowMapper);
		if (wayGraphs != null && !wayGraphs.isEmpty()) {
			return wayGraphs.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public IWayGraph getGraph(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		List<IWayGraph> wayGraphs = getNamedParameterJdbcTemplate().query("SELECT * FROM " + schema + "waygraphs WHERE id = :id", paramMap, wayGraphRowMapper);
		if (wayGraphs != null && !wayGraphs.isEmpty()) {
			return wayGraphs.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=false)
	public long saveGraph(String graphName) {
		Object[] args = new Object[1];
		args[0] = graphName;
		
		Map<String, Object>  params = new HashMap<String, Object>(); 
		params.put("name", graphName);
		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		getNamedParameterJdbcTemplate().update("INSERT INTO " + schema + "waygraphs (name) VALUES (:name)", 
				sqlParameterSource, keyHolder, new String[] {"id"});

		return Long.class.cast(keyHolder.getKey());
	}

	public RowMapper<IWayGraphVersionMetadata> getMetadataRowMapper() {
		return metadataRowMapper;
	}

	public void setMetadataRowMapper(RowMapper<IWayGraphVersionMetadata> metadataRowMapper) {
		this.metadataRowMapper = metadataRowMapper;
	}

	public RowMapper<IWayGraph> getWayGraphRowMapper() {
		return wayGraphRowMapper;
	}

	public void setWayGraphRowMapper(RowMapper<IWayGraph> wayGraphRowMapper) {
		this.wayGraphRowMapper = wayGraphRowMapper;
	}

	@Override
	@Transactional(readOnly=true)
	public List<String> getGraphs() {
		return getJdbcTemplate().query("SELECT name FROM " + schema + "waygraphs", new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("name");
			}
			
		});
	}

	@Override
	@Transactional(readOnly=true)
	public String checkNewerVersionAvailable(String viewName, String version) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("viewName", viewName);
		List<String> states = new ArrayList<String>();
		states.add(State.PUBLISH.name());
		states.add(State.SYNCHRONIZED.name());
		states.add(State.ACTIVE.name());
		paramMap.put("states", states);
		
		String query;
		if (version == null) {
			query = "WITH views AS (SELECT * FROM " + schema + VIEW_TABLE_NAME + " WHERE viewname = :viewName) " +
					"SELECT md.version FROM " + schema + METADATA_TABLE_NAME + " AS md, views AS vw " +
					"WHERE md.graph_id = vw.graph_id AND state IN (:states)";
		} else {
			query = "WITH views AS (SELECT * FROM " + schema + VIEW_TABLE_NAME + " WHERE viewname = :viewName) " +
					"SELECT md.version FROM " + schema + METADATA_TABLE_NAME + " AS md, views AS vw " +
					"WHERE md.graph_id = vw.graph_id AND valid_from > (" +
							"SELECT valid_from FROM " + schema + METADATA_TABLE_NAME + " AS md2, views AS vw2 " +
							"WHERE md2.graph_id = vw2.graph_id AND version = :version) " +
					"AND state IN (:states)";
			paramMap.put("version", version);
		}

		List<String> resultList = getNamedParameterJdbcTemplate().query(
				query, 
				paramMap, 
				new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}}
				);
		if (resultList == null || resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	@Override
	public void deleteWayGraphVersionMetadata(String graphName, String version, boolean keepMetadata) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("graphName", graphName);
		paramMap.put("version", version);
		
		String query = null;
		if (keepMetadata) {
			paramMap.put("state", State.DELETED.name());
			query = "UPDATE " + schema + METADATA_TABLE_NAME + " SET state = :state WHERE graphname = :graphName AND version = :version";
		} else {
			query = "DELETE FROM " + schema + METADATA_TABLE_NAME + " WHERE graphname = :graphName AND version = :version";
		}
		
		getNamedParameterJdbcTemplate().update(query, paramMap);
	}

	public Set<State> getStatesOfCurrentGraph() {
		return statesOfCurrentGraph;
	}

	public void setStatesOfCurrentGraph(Set<State> statesOfCurrentGraph) {
		this.statesOfCurrentGraph = statesOfCurrentGraph;
	}

	public IWayGraphMetadataFactory getMetadataFactory() {
		return metadataFactory;
	}

	public void setMetadataFactory(IWayGraphMetadataFactory metadataFactory) {
		this.metadataFactory = metadataFactory;
	}

}