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

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.helper.GraphVersionHelper;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.core.persistence.IXInfoDaoRegistry;
import at.srfg.graphium.model.*;
import at.srfg.graphium.postgis.persistence.ISegmentToSqlParameterSetConverter;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author mwimmer
 *
 */
public class WayBaseGraphWriteDaoImpl<W extends IBaseWaySegment> 
	extends AbstractWayGraphDaoImpl implements IWayGraphWriteDao<W>, ISegmentToSqlParameterSetConverter<W> {

	private static Logger log = LoggerFactory.getLogger(WayBaseGraphWriteDaoImpl.class);

	private static int counter = 0;
	
	protected WKTWriter wktWriter;
	protected IXInfoDaoRegistry<ISegmentXInfo,IConnectionXInfo> xInfoDaoRegistry;
	protected IWayGraphVersionMetadataDao metadataDao;
	
	@PostConstruct
	public void setup() {
		wktWriter = new WKTWriter();
	}
	
	@Override
	public void createGraph(String graphName, String version, boolean overrideGraphIfExsists)
			throws GraphAlreadyExistException, GraphNotExistsException {
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		if (checkIfSegmentTableExists(graphVersionName)) {
			if (overrideGraphIfExsists) {
				deleteSegments(graphName, version);
			} else {
				throw new GraphAlreadyExistException("graph " + graphVersionName + " already exists");
			}
		}

		getJdbcTemplate().execute("CREATE TABLE " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + 
			" (CONSTRAINT " + SEGMENT_TABLE_PREFIX + graphVersionName + "_pk PRIMARY KEY (id)) INHERITS (" +
				schema + PARENT_SEGMENT_TABLE_NAME + ") WITH (OIDS=FALSE)");

		getJdbcTemplate().execute("CREATE TABLE " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + 
			" () INHERITS (" +	schema + PARENT_CONNECTION_TABLE_NAME + ") WITH (OIDS=FALSE)");
}

	@Override
	public void createGraphVersion(String graphName, String version,  boolean overrideGraphIfExsists,
			boolean createConnectionConstraint)
			throws GraphAlreadyExistException, GraphNotExistsException {
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		createGraph(graphName, version, overrideGraphIfExsists);
		
		if (createConnectionConstraint) {
			getJdbcTemplate().execute("ALTER TABLE " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + 
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_pk PRIMARY KEY" +
				" (node_id, from_segment_id, to_segment_id)," +
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_from_segment_id_fk FOREIGN KEY (from_segment_id)" +
				" REFERENCES " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id) MATCH SIMPLE" +
				" ON UPDATE NO ACTION ON DELETE NO ACTION, " +
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_to_segment_id_fk FOREIGN KEY (to_segment_id)" +
				" REFERENCES " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id) MATCH SIMPLE" +
				" ON UPDATE NO ACTION ON DELETE NO ACTION");
		}
	}
	
	@Override
	public void postCreateGraph(IWayGraphVersionMetadata graphVersionMeta) {
		String graphName = graphVersionMeta.getGraphName();
		String version = graphVersionMeta.getVersion();
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
		int graphVersionId = (int) metadata.getId();
		
		getJdbcTemplate().execute("ALTER TABLE " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + 
				" ADD CONSTRAINT " + SEGMENT_TABLE_PREFIX + graphVersionName + "_id_check CHECK (graphversion_id = " + graphVersionId + ")");
		
		getJdbcTemplate().execute("ALTER TABLE " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + 
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_id_check CHECK (graphversion_id = " + graphVersionId + ")");
		
	}

	protected boolean checkIfSegmentTableExists(String graphVersionName)
	{
		String segmentTableName = SEGMENT_TABLE_PREFIX + graphVersionName;
		String query = "SELECT count(1) FROM pg_tables WHERE tablename = '" + segmentTableName + "'";
		return getJdbcTemplate().queryForObject(query, Integer.class) > 0;
	}

	@Override
	public void createConnectionContstraints(String graphVersionName)
			throws GraphNotExistsException {
		if (!checkIfSegmentTableExists(graphVersionName)) {
			throw new GraphNotExistsException("Could not add connection constraints to non existing graph " + graphVersionName, graphVersionName);
		}
		
		getJdbcTemplate().execute("ALTER TABLE " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + 
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_pk PRIMARY KEY" +
				" (node_id, from_segment_id, to_segment_id)," +
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_from_segment_id_fk FOREIGN KEY (from_segment_id)" +
				" REFERENCES " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id) MATCH SIMPLE" +
				" ON UPDATE NO ACTION ON DELETE NO ACTION, " +
				" ADD CONSTRAINT " + CONNECTION_TABLE_PREFIX + graphVersionName + "_to_segment_id_fk FOREIGN KEY (to_segment_id)" +
				" REFERENCES " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id) MATCH SIMPLE" +
				" ON UPDATE NO ACTION ON DELETE NO ACTION");
	}

	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public void saveSegments(final List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		doBatchUpdate(segments, graphName, version);
	
		// save XInfos
		saveSegmentXInfos(segments, graphName, version);
		
	}

	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public void saveSegments(final List<W> segments, String graphName, String version, List<String> excludedXInfosList) throws GraphStorageException, GraphNotExistsException {
		doBatchUpdate(segments, graphName, version);

		// save XInfos
		saveSegmentXInfos(segments, graphName, version, excludedXInfosList);

	}

	private void doBatchUpdate(final List<W> segments, String graphName, String version) throws  GraphStorageException{
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
		int graphVersionId = (int) metadata.getId();
		try {
			getNamedParameterJdbcTemplate().batchUpdate(getInsertStatement(graphVersionName), getParamSource(segments, graphVersionId));
		} catch (SQLException e) {
			throw new GraphStorageException("error inserting segments", e);
		}
	}


	@Override
	public SqlParameterSource[] getParamSource(List<W> segments, Integer graphVersionId) throws SQLException { 
		final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		SqlParameterSource[] argArray = new SqlParameterSource[segments.size()];
		int i = 0;
		for (W segment : segments) {
			MapSqlParameterSource args = getParamSource(segment, now);
			if (graphVersionId != null) {
				args.addValue("graphVersionId", graphVersionId);
			}
			argArray[i] = args;
			i++;
		}		
		return argArray;
	}
	
	@Override
	public MapSqlParameterSource getParamSource(W segment, Timestamp now) throws SQLException {
		MapSqlParameterSource args = new MapSqlParameterSource();
		args.addValue("id", segment.getId());
		args.addValue("geometry","SRID=4326;"+wktWriter.write(segment.getGeometry()));
		args.addValue("name", segment.getName());
		args.addValue("length", segment.getLength());
		args.addValue("streetType", segment.getStreetType());
		args.addValue("wayId", segment.getWayId());
		args.addValue("startNodeId", segment.getStartNodeId());
		args.addValue("startNodeIndex", segment.getStartNodeIndex());
		args.addValue("endNodeId", segment.getEndNodeId());
		args.addValue("endNodeIndex", segment.getEndNodeIndex());
		args.addValue("timestamp", now);
		args.addValue("tags", segment.getTags());
		return args;
	}
	
	protected String getInsertStatement(String graphVersionName) {
		return "INSERT INTO "+ schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id, graphversion_id, geometry, length, name, streettype, way_id, " +
				"startnode_id, startnode_index, endnode_id, endnode_index, timestamp, tags)" +
		  	" VALUES (:id, :graphVersionId, ST_GeomFromEWKT(:geometry), :length, " +
				" :name, :streetType, :wayId, :startNodeId, :startNodeIndex, :endNodeId, :endNodeIndex, :timestamp, :tags)";
	}
	
	protected String getUpdateStatement(String graphVersionName) {
		return "UPDATE "+ schema + SEGMENT_TABLE_PREFIX + graphVersionName + " SET geometry=ST_GeomFromEWKT(:geometry), "
				+ "length=:length, name=:name, streettype=:streetType, way_id=:wayId, startnode_id=:startNodeId, "
				+ "startnode_index=:startNodeIndex, endnode_id=endNodeId, endnode_index=endNodeIndex, " +
	 		" timestamp=:timestamp, tags=:tags" +
	 		" WHERE id=:id";	 		
	}

	@Override
	public void updateConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		this.saveConnectionXInfos(segments,graphName,version, null,false);
	}

	@Override
	public void updateSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphNotExistsException {
		this.saveSegmentXInfos(segments,graphName,version,false);
	}

	@Override
	public void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		this.saveConnectionXInfos(segments,graphName,version, null, true);
	}

    @Override
    public void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfosList) throws GraphStorageException, GraphNotExistsException {
        this.saveConnectionXInfos(segments,graphName,version, excludedXInfosList, true);
    }

	@Override
	public void deleteConnectionXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException {
		for (String type : types) {
			this.xInfoDaoRegistry.getConnectionXInfoDao(type).deleteAll(graphName,version);
		}
	}

	@Override
	public void deleteSegmentXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException {
		for (String type : types) {
			this.xInfoDaoRegistry.getSegmentXInfoDao(type).deleteAll(graphName, version);
		}
	}

	private void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfoList,  boolean insert) throws GraphStorageException, GraphNotExistsException {
		final Map<String, List<IConnectionXInfo>> xInfoMap = new HashMap<>();
		segments.stream().filter(w -> w.getCons() != null && w.getCons().isEmpty())
				.forEach(segment -> segment.getCons().stream().filter(con -> con.getXInfo() != null && !con.getXInfo().isEmpty())
						.forEach(con -> {
							con.getXInfo().forEach(xInfo -> {
								if (excludedXInfoList != null && !excludedXInfoList.contains(xInfo.getXInfoType())) {
									//System.out.println("!!!: XInfo-type: " + xInfo.getXInfoType());
									if (!xInfoMap.containsKey(xInfo.getXInfoType())) {
										xInfoMap.put(xInfo.getXInfoType(), new ArrayList<>());
									}
									xInfoMap.get(xInfo.getXInfoType()).add(xInfo);
								}
							});
						}));


        for (String type : xInfoMap.keySet()) {
            // get DAO per XInfo type
            IXInfoDao<IConnectionXInfo>  xInfoDao = xInfoDaoRegistry.getConnectionXInfoDao(type);
            // save XInfos
            if (xInfoDao != null) {
                if (insert) {
                    xInfoDao.save(graphName, version, xInfoMap.get(type));
                } else {
                    xInfoDao.update(graphName, version, xInfoMap.get(type));
                }
            } else {
                //TODO evtl. log something but simply ignore additional features
            }
        }
	}

	@Override
	public void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		this.saveSegmentXInfos(segments,graphName,version,true);
	}

	@Override
	public void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfosList) throws GraphStorageException, GraphNotExistsException {
		this.saveSegmentXInfos(segments,graphName,version, excludedXInfosList, true);
	}

    private void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version, boolean insert) throws GraphNotExistsException {
      saveSegmentXInfos(segments, graphName, version, null, insert);
	}

	private void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfoList, boolean insert) throws GraphNotExistsException {
		final Map<String, List<ISegmentXInfo>> xInfoMap = new HashMap<>();
		segments.stream().filter(segment -> segment != null && segment.getXInfo() != null && !segment.getXInfo().isEmpty())
				.forEach(segment -> segment.getXInfo()
						.forEach(xInfo -> {
							if (excludedXInfoList != null && !excludedXInfoList.contains(xInfo.getXInfoType())) {
								//System.out.println("!!!: XInfo-type: " + xInfo.getXInfoType());
								if (!xInfoMap.containsKey(xInfo.getXInfoType())) {
									xInfoMap.put(xInfo.getXInfoType(), new ArrayList<>());
								}
								xInfoMap.get(xInfo.getXInfoType()).add(xInfo);
							}
						}));
		saveXInfos(graphName, version, insert, xInfoMap);
	}


	private void saveXInfos(String graphName, String version, boolean insert, Map<String, List<ISegmentXInfo>> xInfoMap){
		for (String type : xInfoMap.keySet()) {
			// get DAO per XInfo type
			IXInfoDao<ISegmentXInfo>  xInfoDao = xInfoDaoRegistry.getSegmentXInfoDao(type);
			// save XInfos
			if (xInfoDao != null) {
				try {
					if (insert) {
						xInfoDao.save(graphName, version, xInfoMap.get(type));
					} else {
						xInfoDao.update(graphName, version, xInfoMap.get(type));
					}
				} catch (GraphNotExistsException e) {
					log.warn("Could not save XInfo", e);
				}
			} else {
				//TODO evtl. log something but simply ignore additional features
			}
		}
	}


	@Override
	public long updateSegmentAttributes(final List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
	
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		int[] updateCounts = new int[0];
		try {
			updateCounts = getNamedParameterJdbcTemplate().batchUpdate(getUpdateStatement(graphVersionName), getParamSource(segments, null));
		} catch (SQLException e) {
			throw new GraphStorageException("error updating segments", e);
		}
		
		// save XInfos
		updateConnectionXInfos(segments, graphName, version);
				
		int updates = 0;
		for (int update : updateCounts) {
			updates += update;
		}
		
		return updates;
	}

	@Override
	public long saveConnectionsOnSegments(List<W> segmentsWithConnections,
			boolean saveSegments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		if (saveSegments) {
			saveSegments(segmentsWithConnections, graphName, version);
		}
		
		final List<IWaySegmentConnection> connections = new ArrayList<IWaySegmentConnection>();
		for (W seg : segmentsWithConnections) {
			connections.addAll(seg.getCons());
		}
		
		return saveConnections(connections, graphName, version);
	}

	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public long saveConnections(final List<IWaySegmentConnection> connections,
			String graphName, String version) {
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);

		IWayGraphVersionMetadata metadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
		int graphVersionId = (int) metadata.getId();

		int[] updateCounts = getJdbcTemplate().batchUpdate(
				 "INSERT INTO "+ schema + CONNECTION_TABLE_PREFIX + graphVersionName + 
				 " (node_id, from_segment_id, to_segment_id, access, graphversion_id)" +
				 " VALUES (?,?,?,?,?)",
	             new BatchPreparedStatementSetter() {
					 IWaySegmentConnection conn;
	                 public void setValues(PreparedStatement ps, int i) throws SQLException {
	                	 conn = connections.get(i);
	                	 int pos = 1;
	                	 ps.setLong(pos++, conn.getNodeId());
	                	 ps.setLong(pos++, conn.getFromSegmentId());
	                	 ps.setLong(pos++, conn.getToSegmentId());
	                	 ps.setArray(pos++, convertToArray(ps.getConnection(), conn.getAccess()));
	                	 ps.setInt(pos++, graphVersionId);
	                 }
					public int getBatchSize() {
	                     return connections.size();
	                 }
	             } );     
		
		long updatedCount = 0;
		for (int upd : updateCounts) {
			updatedCount += upd;
		}
		
		return updatedCount;
	}

	@Override
	public void updateSegments(List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		updateSegmentAttributes(segments, graphName, version);
	}

	@Override
	public long updateConnections(List<W> segments, String graphName, String version) {
		throw new NotImplementedException("");
	}

	@Override
	public void createIndexes(String graphName, String version) {
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		getJdbcTemplate().execute("CREATE INDEX " + graphVersionName + "_geometry_idx" +
				" ON " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " USING GIST (geometry)");

		getJdbcTemplate().execute("CREATE INDEX " + graphVersionName + "_conns_from_segment_id_idx" +
				" ON " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + " USING btree (from_segment_id)");

		getJdbcTemplate().execute("CREATE INDEX " + graphVersionName + "_conns_to_segment_id_idx" +
				" ON " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + " USING btree (to_segment_id)");

		getJdbcTemplate().execute("CREATE INDEX " + graphVersionName + "_conns_node_id_idx" +
				" ON " + schema + CONNECTION_TABLE_PREFIX + graphVersionName + " USING btree (node_id)");
	}

	@Override
	public void deleteSegments(String graphName, String version) throws GraphNotExistsException {
		deleteSegmentTables(graphName, version);
		deleteXInfos(graphName, version);
	}

	protected void deleteXInfos(String graphName, String version) throws GraphNotExistsException {
		List<IXInfoDao<ISegmentXInfo>> xInfoDaos = xInfoDaoRegistry.getAllSegmentXInfoDaos();
		if (xInfoDaos != null && !xInfoDaos.isEmpty()) {
			for (IXInfoDao<ISegmentXInfo> xInfoDao : xInfoDaos) {
				xInfoDao.deleteAll(graphName, version);
			}
		}
	}
	
	protected void deleteSegmentTables(String graphName, String version) {
		String graphVersionName = GraphVersionHelper.createGraphVersionName(graphName, version);
		getJdbcTemplate().execute("DROP TABLE " + schema + SEGMENT_TABLE_PREFIX + graphVersionName + " CASCADE");
		getJdbcTemplate().execute("DROP TABLE " + schema + CONNECTION_TABLE_PREFIX + graphVersionName);
	}

	public IXInfoDaoRegistry<ISegmentXInfo,IConnectionXInfo> getxInfoDaoRegistry() {
		return xInfoDaoRegistry;
	}

	public void setxInfoDaoRegistry(IXInfoDaoRegistry<ISegmentXInfo,IConnectionXInfo> xInfoDaoRegistry) {
		this.xInfoDaoRegistry = xInfoDaoRegistry;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}
	
}