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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.IWaySegment;

/**
 * @author mwimmer
 *
 */
public class WayGraphWriteDaoImpl<W extends IWaySegment> extends WayBaseGraphWriteDaoImpl<W> implements IWayGraphWriteDao<W> {

	@Override
	public MapSqlParameterSource getParamSource(W segment, Timestamp now) throws SQLException {
		
		MapSqlParameterSource args = super.getParamSource(segment, now);		
		args.addValue("maxSpeedTow", segment.getMaxSpeedTow());
		args.addValue("maxSpeedBkw", segment.getMaxSpeedBkw());
		args.addValue("speedCalcTow", segment.getSpeedCalcTow());
		args.addValue("speedCalcBkw", segment.getSpeedCalcBkw());
		args.addValue("lanesTow", segment.getLanesTow());
		args.addValue("lanesBkw", segment.getLanesBkw());
		args.addValue("frc", segment.getFrc().getValue());
		 if (segment.getFormOfWay() != null) {
			 args.addValue("formOfWay", segment.getFormOfWay().getValue());
    	 } else {
    		 args.addValue("formOfWay", FormOfWay.NOT_APPLICABLE.getValue());
    	 }		 
		Connection con = getConnection();
		args.addValue("accessTow",  convertToArray(con, segment.getAccessTow()));
		args.addValue("accessBkw",  convertToArray(con, segment.getAccessBkw()));
		
		args.addValue("tunnel", segment.isTunnel());
		args.addValue("bridge", segment.isBridge());
		args.addValue("urban", segment.isUrban());
		
		return args;
	}
	
	protected String getInsertStatement(String graphVersionName) {
		 return "INSERT INTO "+ schema + SEGMENT_TABLE_PREFIX + graphVersionName + " (id, graphversion_id, geometry, length, name, maxspeed_tow, maxspeed_bkw," +
	 		" speed_calc_tow, speed_calc_bkw, lanes_tow, lanes_bkw, frc, formofway, streettype, way_id, startnode_id, startnode_index," +
	 		" endnode_id, endnode_index, access_tow, access_bkw, tunnel, bridge, urban, timestamp, tags)" +
	 		" VALUES (:id, :graphVersionId, ST_GeomFromEWKT(:geometry), :length, " +
	 		" :name, :maxSpeedTow, :maxSpeedBkw, :speedCalcTow, :speedCalcBkw, :lanesTow, :lanesBkw, :frc, :formOfWay, " +
	 		" :streetType, :wayId, :startNodeId, :startNodeIndex, :endNodeId, :endNodeIndex, :accessTow, :accessBkw, :tunnel," +
	 		" :bridge, :urban, :timestamp, :tags)";
	 		
	 		
	}
	
	protected String getUpdateStatement(String graphVersionName) {
		return  "UPDATE "+ schema + SEGMENT_TABLE_PREFIX + graphVersionName + " SET geometry=ST_GeomFromEWKT(:geometry), length=:length, " +
	 		" name=:name, maxspeed_tow=:maxSpeedTow, maxspeed_bkw=:maxSpeedBkw, speed_calc_tow=:speedCalcTow, speed_calc_bkw=:speedCalcBkw, lanesTow=:lanesTow," +
	 		" lanesBkw=:lanesBkw, frc=:frc, formofway=:formOfWay, streettype=:streetType, way_id=:wayId, startnode_id=:startNodeId, startnode_index=:startNodeIndex, " + 
	 		" endnode_id=endNodeId, endnode_index=endNodeIndex, access_tow=accessTow, access_bkw=accessBkw, tunnel=tunnel, bridge=bridge, urban=:urban," +
	 		" timestamp=:timestamp, tags=:tags" +
	 		" WHERE id=:id";
	}
	
}