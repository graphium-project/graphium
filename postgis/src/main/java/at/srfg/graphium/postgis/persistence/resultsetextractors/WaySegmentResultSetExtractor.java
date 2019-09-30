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
package at.srfg.graphium.postgis.persistence.resultsetextractors;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.postgis.jts.JtsBinaryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.impl.WaySegmentConnection;
import at.srfg.graphium.postgis.persistence.ISegmentResultSetExtractor;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import at.srfg.graphium.postgis.persistence.rowmapper.ColumnFinder;
import at.srfg.graphium.postgis.persistence.rowmapper.RowMapperUtils;

/**
 * @author mwimmer
 *
 */
public class WaySegmentResultSetExtractor<S extends IBaseSegment> implements ISegmentResultSetExtractor<S, ISegmentXInfo> {

	private static Logger log = LoggerFactory.getLogger(WaySegmentResultSetExtractor.class);

	private static final String ARRAYVALUESEP = ",";
	private static final String QUERY_PREFIX = "wayseg";
	private static final String ATTRIBUTES = "id, " +	// ID erhält kein Prefix, darf nur für Segment-Table gelten, erleichtert Suche nach ID in DAO
											"ST_AsEWKB(geometry) AS " + QUERY_PREFIX + "_geometry, " +
											"length AS " + QUERY_PREFIX + "_length," + 
											"name AS " + QUERY_PREFIX + "_name, " +
											"maxspeed_tow AS " + QUERY_PREFIX + "_maxspeed_tow, " +
											"maxspeed_bkw AS " + QUERY_PREFIX + "_maxspeed_bkw, " +
											"speed_calc_tow AS " + QUERY_PREFIX + "_speed_calc_tow, " +
											"speed_calc_bkw AS " + QUERY_PREFIX + "_speed_calc_bkw, " +
											"lanes_tow AS " + QUERY_PREFIX + "_lanes_tow, " +
											"lanes_bkw AS " + QUERY_PREFIX + "_lanes_bkw, " +
											"frc AS " + QUERY_PREFIX + "_frc, " +
											"formofway AS " + QUERY_PREFIX + "_formofway, " +
											"streettype AS " + QUERY_PREFIX + "_streettype, " +
											"way_id AS " + QUERY_PREFIX + "_way_id, " +
											"startnode_id AS " + QUERY_PREFIX + "_startnode_id, " +
											"startnode_index AS " + QUERY_PREFIX + "_startnode_index, " +
											"endnode_id AS " + QUERY_PREFIX + "_endnode_id, " +
											"endnode_index AS " + QUERY_PREFIX + "_endnode_index, " +
											"access_tow AS " + QUERY_PREFIX + "_access_tow, " +
											"access_bkw AS " + QUERY_PREFIX + "_access_bkw, " +
											"tunnel AS " + QUERY_PREFIX + "_tunnel, " +
											"bridge AS " + QUERY_PREFIX + "_bridge, " +
											"urban AS " + QUERY_PREFIX + "_urban, " +
											"timestamp AS " + QUERY_PREFIX + "_timestamp, " +
											"tags AS " + QUERY_PREFIX + "_tags ";
	
	private JtsBinaryParser bp = new JtsBinaryParser();
	@Autowired(required=false)
	private List<ISegmentXInfoRowMapper> rowMappers;
	
	/**
	 * WaySegmentResultSetExtractor ist immer Root-Extractor, d.h. er ist immer für das Mapping eines Segments verantwortlich und delegiert ggf. an
	 * weitere ResultSetExtractor (falls Joins auf XInfo-Tables vorhanden).
	 * 
	 * - Prüfung, ob Join auf XInfo-Table vorhanden: check in jeweiligem ResultSetExtractor mit rs.getMetaData().getTableName(i), ob für die  
		
	 * - Jeder XInfo-ResultSetExtractor gibt ein XInfo-Objekt zurück. Dieses landet in einem Set (je ResultSetExtractor). Dadurch werden Duplikate gefiltert.
	 *   Wichtig ist eine korrekte equals- und hashCode-Implementierung in den XInfo-Objekten.
	 * - 
	 * 
	 */
	@Override
	public S extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		if (rs == null) {
			return null;
		}
		
		// map segment
		S segment = segment= createSegment(rs, true);
		
		if (segment == null) {
			return null;
		}
		
		Map<ISegmentXInfoRowMapper<? extends ISegmentXInfo>, Set<ISegmentXInfo>> xinfos = new HashMap<>();
		// map extended information - while result set contains same segment ID 
		do {
			if (rowMappers != null) {
				for (ISegmentXInfoRowMapper<ISegmentXInfo> rowMapper : rowMappers) {
					IBaseSegment xInfoSegment = rowMapper.mapRow(rs, 0);
					if (xInfoSegment != null && !xInfoSegment.getXInfo().isEmpty()) {
						if (!xinfos.containsKey(rowMapper)) {
							xinfos.put(rowMapper, new HashSet<>());
						}
						xinfos.get(rowMapper).addAll(xInfoSegment.getXInfo());
					}
				}
			}
		} while (rs.next() && rs.getLong("id") == segment.getId());

		// add extended information to segment
		if (!xinfos.isEmpty()) {
			List<ISegmentXInfo> xinfoList = new ArrayList<>();
			for (Set<ISegmentXInfo> xi : xinfos.values()) {
				xinfoList.addAll(xi);
			}
			segment.addXInfo(xinfoList);
		}
		
		return segment;
	}
	
	private void mapConnections(IWaySegment segment, ResultSet rs) {
		try {
			if (rs.findColumn("startnodesegments") > -1) {
				Array arr = rs.getArray("startnodesegments");
				if (arr != null) {
					segment.setStartNodeCons(createNodeConnections(arr.getArray()));
				}
			}
		} catch (SQLException e) {}
		
		try {
			if (rs.findColumn("endnodesegments") > -1) {
				Array arr = rs.getArray("endnodesegments");
				if (arr != null) {
					segment.setEndNodeCons(createNodeConnections(arr.getArray()));
				}
			}
		} catch (SQLException e) {}
	}

	private S createSegment(ResultSet rs, boolean geometry) throws SQLException {
		boolean createfullWaySegment = false;
		
		if (rs.isAfterLast()) {
			return null;
		}
		
		int i = 1;
		while (!createfullWaySegment && i <= rs.getMetaData().getColumnCount()) {
			if (rs.getMetaData().getColumnLabel(i).startsWith(QUERY_PREFIX)) {
				createfullWaySegment = true;
			}
			i++;
		}
		
		long segmentId = rs.getLong("id");

		// TODO: use factory?
		S segment;
		
		if (createfullWaySegment) {
		
			IWaySegment waySegment = new WaySegment();
			waySegment.setId(segmentId);

			mapSegment(waySegment, rs);

			if (geometry) {
				waySegment.setGeometry((LineString) bp.parse(rs.getBytes(QUERY_PREFIX + "_geometry_ewkb")));
			}			

			// map connections
			mapConnections(waySegment, rs);

			segment = (S) waySegment;

		} else {
			segment = (S) new BaseSegment(segmentId, null);
		}
		return segment;
	}

	/**
	 * @param waySegment
	 * @param rs
	 * @throws SQLException 
	 */
	private void mapSegment(IWaySegment waySegment, ResultSet rs) throws SQLException {
		// TODO: graphId is missing - do we need it?

		ColumnFinder colFinder = new ColumnFinder(rs);
		
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_access_bkw") > -1) {
			waySegment.setAccessBkw(RowMapperUtils.convertAccessTypes(rs, QUERY_PREFIX + "_access_bkw"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_access_tow") > -1) {
			waySegment.setAccessTow(RowMapperUtils.convertAccessTypes(rs, QUERY_PREFIX + "_access_tow"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_bridge") > -1) {
			waySegment.setBridge(rs.getBoolean(QUERY_PREFIX + "_bridge"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_endnode_id") > -1) {
			waySegment.setEndNodeId(rs.getLong(QUERY_PREFIX + "_endnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_endnode_index") > -1) {
			waySegment.setEndNodeIndex(rs.getInt(QUERY_PREFIX + "_endnode_index"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_frc") > -1) {
			Short frcCode = rs.getShort(QUERY_PREFIX + "_frc");
			FuncRoadClass frc = null;
			if (!rs.wasNull() && frcCode != null) {
				frc = FuncRoadClass.getFuncRoadClassForValue(frcCode);
			}
			waySegment.setFrc(frc);
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_formofway") > -1) {
			Short formOfWayCode = rs.getShort(QUERY_PREFIX + "_formofway");
			FormOfWay fow = null;
			if (!rs.wasNull() && formOfWayCode != null) {
				fow = FormOfWay.getFormOfWayForValue(formOfWayCode);
			}
			waySegment.setFormOfWay(fow);
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_lanes_bkw") > -1) {
			waySegment.setLanesBkw(rs.getShort(QUERY_PREFIX + "_lanes_bkw"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_lanes_tow") > -1) {
			waySegment.setLanesTow(rs.getShort(QUERY_PREFIX + "_lanes_tow"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_length") > -1) {
			waySegment.setLength(rs.getFloat(QUERY_PREFIX + "_length"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_maxspeed_bkw") > -1) {
			waySegment.setMaxSpeedBkw(rs.getShort(QUERY_PREFIX + "_maxspeed_bkw"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_maxspeed_tow") > -1) {
			waySegment.setMaxSpeedTow(rs.getShort(QUERY_PREFIX + "_maxspeed_tow"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_name") > -1) {
			waySegment.setName(rs.getString(QUERY_PREFIX + "_name"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_speed_calc_bkw") > -1) {
			waySegment.setSpeedCalcBkw(rs.getShort(QUERY_PREFIX + "_speed_calc_bkw"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_speed_calc_tow") > -1) {
			waySegment.setSpeedCalcTow(rs.getShort(QUERY_PREFIX + "_speed_calc_tow"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_startnode_id") > -1) {
			waySegment.setStartNodeId(rs.getLong(QUERY_PREFIX + "_startnode_id"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_startnode_index") > -1) {
			waySegment.setStartNodeIndex(rs.getInt(QUERY_PREFIX + "_startnode_index"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_streettype") > -1) {
			waySegment.setStreetType(rs.getString(QUERY_PREFIX + "_streettype"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_tags") > -1) {
			waySegment.setTags((Map<String, String>) rs.getObject(QUERY_PREFIX + "_tags"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_timestamp") > -1) {
			waySegment.setTimestamp(rs.getTimestamp(QUERY_PREFIX + "_timestamp"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_tunnel") > -1) {
			waySegment.setTunnel(rs.getBoolean(QUERY_PREFIX + "_tunnel"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_urban") > -1) {
			waySegment.setUrban(rs.getBoolean(QUERY_PREFIX + "_urban"));
		}
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_way_id") > -1) {
			waySegment.setWayId(rs.getLong(QUERY_PREFIX + "_way_id"));
		}
	}

	private List<IWaySegmentConnection> createNodeConnections(Object array) {
		if(array != null && array instanceof String[])
		{
			List<IWaySegmentConnection> cons = new ArrayList<IWaySegmentConnection>();
			String[] serializedCons = (String[]) array;
			IWaySegmentConnection currentCon = null;
			for(String serializedCon : serializedCons)
			{
				currentCon = parseSerializedCon(serializedCon);
				if(currentCon != null)
					cons.add(currentCon);
			}
			return cons;
		}
		return null;
	}

	private IWaySegmentConnection parseSerializedCon(String serializedCon) { // (100000833,960301,101021339,"{15,4,22,2,9,19,11,13,12,1,10,3}",24)
		if (serializedCon == null) {
			return null;
		}
		
		String stripedSerializedCon = StringUtils.removeStart(serializedCon, "(");
		stripedSerializedCon = StringUtils.removeEnd(stripedSerializedCon, ")");
		stripedSerializedCon = stripedSerializedCon.substring(0, stripedSerializedCon.indexOf("}")); // ignore everything after access types
		stripedSerializedCon = stripedSerializedCon.replace("\"", ""); //100000833,960301,101021339,{15,4,22,2,9,19,11,13,12,1,10,3,24
		String[] splitCons = stripedSerializedCon.split("\\{"); //[100000833,960301,101021339,, 15,4,22,2,9,19,11,13,12,1,10,3,24]
		String[] tokens = splitCons[0].split(ARRAYVALUESEP);
		String[] accessTypeIdsArray = splitCons[1].split(ARRAYVALUESEP); //[15, 4, 22, 2, 9, 19, 11, 13, 12, 1, 10, 3, 24] 

//		String s1 = StringUtils.removePattern(stripedSerializedCon, "\\\"\\{[0-9,]*\\}\\\"");
		int[] accessTypeIds = new int[accessTypeIdsArray.length];
		int i = 0;
		for (String accessTypeId : accessTypeIdsArray) {
			accessTypeIds[i++] = Integer.parseInt(accessTypeId);
		}
		Set<Access> accessTypesTow = Access.getAccessTypes(accessTypeIds);

		return new WaySegmentConnection(
				Long.parseLong(tokens[0]), 
				Long.parseLong(tokens[1]),
				Long.parseLong(tokens[2]),
				accessTypesTow);	
	}

	@Override
	public boolean fitsPrefix(String prefix) {
		return prefix.equals(QUERY_PREFIX);
	}

	@Override
	public String getAttributes() {
		return ATTRIBUTES;
	}
	
	@Override
	public String getPrefix() {
		return QUERY_PREFIX;
	}

	@Override
	public List<ISegmentXInfoRowMapper> getRowMappers() {
		return rowMappers;
	}

	@Override
	public void setRowMappers(List<ISegmentXInfoRowMapper> rowMappers) {
		this.rowMappers = rowMappers;
	}

	@Override
	public void addRowMapper(ISegmentXInfoRowMapper rowMapper) {
		if (rowMappers == null) {
			rowMappers = new ArrayList<>();
		}
		
		boolean found = false;
		for (ISegmentXInfoRowMapper rm : rowMappers) {
			if (rm.equals(rowMapper)) {
				found = true;
			}
		}
		
		if (!found) {
			rowMappers.add(rowMapper);
		}
	}
}