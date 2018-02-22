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
package at.srfg.graphium.postgis.persistence.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IDefaultSegmentXInfo;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.model.impl.DefaultSegmentXInfo;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;

/**
 * @author mwimmer
 *
 */
public class DefaultSegmentXInfoRowMapper extends AbstractXInfoModelTypeAware<IDefaultSegmentXInfo> 
	implements ISegmentXInfoRowMapper<IDefaultSegmentXInfo> {

	public DefaultSegmentXInfoRowMapper() {
		super(new DefaultSegmentXInfo());
	}

	private static Logger log = LoggerFactory.getLogger(DefaultSegmentXInfoRowMapper.class);
	
	private static final String QUERY_PREFIX = "def";
	private static final String ATTRIBUTES = "segment_id AS " + QUERY_PREFIX + "_segment_id, " +
											"direction_tow AS " + QUERY_PREFIX + "_direction_tow, " +
											"graphversion_id AS " + QUERY_PREFIX + "_graphversion_id, " +
											"tags AS " + QUERY_PREFIX + "_tags ";

	@Override
	public IBaseSegment mapRow(ResultSet rs, int i) throws SQLException {
		IDefaultSegmentXInfo xInfo = new DefaultSegmentXInfo();
		boolean valid = false;
		
		ColumnFinder colFinder = new ColumnFinder(rs);
	
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_segment_id") > -1) {
			xInfo.setSegmentId(rs.getLong(QUERY_PREFIX + "_segment_id"));
			if (!rs.wasNull()) {
				valid = true;
			}
		}

		if (colFinder.getColumnIndex(QUERY_PREFIX + "_direction_tow") > -1) {
			xInfo.setDirectionTow(rs.getBoolean(QUERY_PREFIX + "_direction_tow"));
		}

		if (colFinder.getColumnIndex(QUERY_PREFIX + "_graphversion_id") > -1) {
			xInfo.setGraphVersionId(rs.getLong(QUERY_PREFIX + "_graphversion_id"));
			if (xInfo.getGraphVersionId() == 0) {
				xInfo.setGraphVersionId(null);
			}
		}

		if (colFinder.getColumnIndex(QUERY_PREFIX + "_tags") > -1) {
			xInfo.setValues((Map<String, Object>) rs.getObject(QUERY_PREFIX + "_tags"));
		}

		if (valid) {
			IBaseSegment segment = new BaseSegment();
			segment.addXInfo(xInfo);
			segment.setId(xInfo.getSegmentId());
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
