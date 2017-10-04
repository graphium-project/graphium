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
package at.srfg.graphium.tutorial.xinfo.postgis.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import at.srfg.graphium.postgis.persistence.rowmapper.ColumnFinder;
import at.srfg.graphium.tutorial.xinfo.model.IRoadDamage;
import at.srfg.graphium.tutorial.xinfo.model.impl.RoadDamageImpl;

/**
 * @author mwimmer
 *
 */
public class RoadDamageRowMapper extends AbstractXInfoModelTypeAware<IRoadDamage> implements ISegmentXInfoRowMapper<IRoadDamage> {

	private static Logger log = LoggerFactory.getLogger(RoadDamageRowMapper.class);
	
	public RoadDamageRowMapper() {
		super(new RoadDamageImpl());
	}

	private static final String QUERY_PREFIX = "rdm";
	private static final String ATTRIBUTES = "segment_id AS " + QUERY_PREFIX + "_segment_id, " +
											"direction_tow AS " + QUERY_PREFIX + "_direction_tow, " +
											"graphversion_id AS " + QUERY_PREFIX + "_graphversion_id, " +
											"start_offset AS " + QUERY_PREFIX + "_start_offset, " +
											"end_offset AS " + QUERY_PREFIX + "_end_offset, " +
											"type AS " + QUERY_PREFIX + "_type";

	@Override
	public IBaseSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoadDamageImpl rd = new RoadDamageImpl();
		boolean valid = false;
		try {
			ColumnFinder colFinder = new ColumnFinder(rs);

			if (colFinder.getColumnIndex(QUERY_PREFIX + "_segment_id") > -1) {
				rd.setSegmentId(rs.getLong(QUERY_PREFIX + "_segment_id"));
				if (!rs.wasNull()) {
					valid = true;
				}
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_direction_tow") > -1) {
				rd.setDirectionTow(rs.getBoolean(QUERY_PREFIX + "_direction_tow"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_graphversion_id") > -1) {
				rd.setGraphVersionId(rs.getLong(QUERY_PREFIX + "_graphversion_id"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_start_offset") > -1) {
				rd.setStartOffset((float)rs.getDouble(QUERY_PREFIX + "_start_offset"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_end_offset") > -1) {
				rd.setEndOffset((float)rs.getDouble(QUERY_PREFIX + "_end_offset"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_type") > -1) {
				rd.setType(rs.getString(QUERY_PREFIX + "_type"));
			}
		} catch (SQLException e) {}

		if (valid) {
			IBaseSegment segment = new BaseSegment();
			segment.setId(rd.getSegmentId());
			segment.addXInfo(rd);
			return segment;
		} else {
			return null;
		}
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
	public boolean isApplicable(ResultSet rs) {
		boolean applicable = false;
		
		int i = 1;
		try {
			while (!applicable && i <= rs.getMetaData().getColumnCount()) {
				if (rs.getMetaData().getColumnLabel(i).startsWith(QUERY_PREFIX)) {
					applicable = true;
				}
				i++;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			applicable = false;
		}
		return applicable;
	}

}
