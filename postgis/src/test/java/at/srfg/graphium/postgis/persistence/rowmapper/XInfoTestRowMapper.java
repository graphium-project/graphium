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

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.postgis.model.IXInfoTest;
import at.srfg.graphium.postgis.model.impl.XInfoTest;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;

/**
 * @author mwimmer
 *
 */
public class XInfoTestRowMapper extends AbstractXInfoModelTypeAware<IXInfoTest> implements ISegmentXInfoRowMapper<IXInfoTest> {

	/**
	 * @param object
	 */
	public XInfoTestRowMapper() {
		super(new XInfoTest());
	}

	private static final String QUERY_PREFIX = "xit";
	private static final String ATTRIBUTES = "segment_id AS " + QUERY_PREFIX + "_segment_id, " +
											"direction_tow AS " + QUERY_PREFIX + "_direction_tow, " +
											"graph_id AS " + QUERY_PREFIX + "_graph_id, " +
											"directed_id AS " + QUERY_PREFIX + "_directed_id ";

	@Override
	public IBaseSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
		IXInfoTest xit = new XInfoTest();
		ColumnFinder colFinder = new ColumnFinder(rs);
		
		if (colFinder.getColumnIndex(QUERY_PREFIX + "_segment_id") > -1) {
			xit.setSegmentId(rs.getLong(QUERY_PREFIX + "_segment_id"));
			xit.setDirectedId(rs.getLong(QUERY_PREFIX + "_directed_id"));
			xit.setDirectionTow(rs.getBoolean(QUERY_PREFIX + "_direction_tow"));
			xit.setGraphId(rs.getLong(QUERY_PREFIX + "_graph_id"));
			IBaseSegment segment = new BaseSegment();
			segment.addXInfo(xit);
			segment.setId(xit.getSegmentId());
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
		return false;
	}

}
