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

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import at.srfg.graphium.model.IHDWaySegment;

/**
 * @author mwimmer
 *
 */
public class HDWayGraphWriteDaoImpl<W extends IHDWaySegment> extends WayGraphWriteDaoImpl<W> {
	
	public final static String HDSEGMENT_TABLE_PREFIX = "hdwaysegments_";
	public final static String PARENT_HDSEGMENT_TABLE_NAME = "hdwaysegments";

	@PostConstruct
	public void setup() {
		super.setup();
		segmentTablePrefix = HDSEGMENT_TABLE_PREFIX;
		parentSegmentTableName = PARENT_HDSEGMENT_TABLE_NAME;
	}
	
	@Override
	public MapSqlParameterSource getParamSource(W segment, Timestamp now) throws SQLException {
				
		MapSqlParameterSource args = super.getParamSource(segment, now);
		args.addValue("leftBoarderGeometry","SRID=4326;"+wktWriter.write(segment.getLeftBoarderGeometry()));
		args.addValue("leftBoarderStartNodeId", segment.getLeftBoarderStartNodeId());
		args.addValue("leftBoarderEndNodeId", segment.getLeftBoarderEndNodeId());
		args.addValue("rightBoarderGeometry","SRID=4326;"+wktWriter.write(segment.getRightBoarderGeometry()));
		args.addValue("rightBoarderStartNodeId", segment.getRightBoarderStartNodeId());
		args.addValue("rightBoarderEndNodeId", segment.getRightBoarderEndNodeId());
		
		return args;
	}
	
	protected String getInsertStatement(String graphVersionName) {
		 return "INSERT INTO "+ schema + HDSEGMENT_TABLE_PREFIX + graphVersionName + " (id, graphversion_id, geometry, length, name, maxspeed_tow, maxspeed_bkw," +
	 		" speed_calc_tow, speed_calc_bkw, lanes_tow, lanes_bkw, frc, formofway, streettype, way_id, startnode_id, startnode_index," +
	 		" endnode_id, endnode_index, access_tow, access_bkw, tunnel, bridge, urban, timestamp, tags, left_boarder_geometry, left_boarder_startnode_id," +
	 		" left_boarder_endnode_id, right_boarder_geometry, right_boarder_startnode_id, right_boarder_endnode_id)" +
	 		" VALUES (:id, :graphVersionId, ST_GeomFromEWKT(:geometry), :length, " +
	 		" :name, :maxSpeedTow, :maxSpeedBkw, :speedCalcTow, :speedCalcBkw, :lanesTow, :lanesBkw, :frc, :formOfWay, " +
	 		" :streetType, :wayId, :startNodeId, :startNodeIndex, :endNodeId, :endNodeIndex, :accessTow, :accessBkw, :tunnel," +
	 		" :bridge, :urban, :timestamp, :tags, ST_GeomFromEWKT(:leftBoarderGeometry), :leftBoarderStartNodeId, :leftBoarderEndNodeId," +
	 		" ST_GeomFromEWKT(:rightBoarderGeometry), :rightBoarderStartNodeId, :rightBoarderEndNodeId)";
	}
	
	protected String getUpdateStatement(String graphVersionName) {
		return  "UPDATE "+ schema + HDSEGMENT_TABLE_PREFIX + graphVersionName + " SET geometry=ST_GeomFromEWKT(:geometry), length=:length, " +
	 		" name=:name, maxspeed_tow=:maxSpeedTow, maxspeed_bkw=:maxSpeedBkw, speed_calc_tow=:speedCalcTow, speed_calc_bkw=:speedCalcBkw, lanesTow=:lanesTow," +
	 		" lanesBkw=:lanesBkw, frc=:frc, formofway=:formOfWay, streettype=:streetType, way_id=:wayId, startnode_id=:startNodeId, startnode_index=:startNodeIndex, " + 
	 		" endnode_id=endNodeId, endnode_index=endNodeIndex, access_tow=accessTow, access_bkw=accessBkw, tunnel=tunnel, bridge=bridge, urban=:urban," +
	 		" timestamp=:timestamp, tags=:tags, left_boarder_geometry=ST_GeomFromEWKT(:leftBoarderGeometry), left_boarder_startnode_id=:leftBoarderStartNodeId," +
	 		" left_boarder_endnode_id=:leftBoarderEndNodeId, right_boarder_geometry=ST_GeomFromEWKT(:rightBoarderGeometry), right_boarder_startnode_id=:rightBoarderStartNodeId," +
	 		" right_boarder_endnode_id=:rightBoarderEndNodeId" +
	 		" WHERE id=:id";
	}

}