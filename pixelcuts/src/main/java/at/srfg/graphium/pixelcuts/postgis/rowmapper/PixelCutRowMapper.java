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
package at.srfg.graphium.pixelcuts.postgis.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.pixelcuts.model.IPixelCut;
import at.srfg.graphium.pixelcuts.model.impl.PixelCut;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import at.srfg.graphium.postgis.persistence.rowmapper.ColumnFinder;

/**
 * @author mwimmer
 *
 */
public class PixelCutRowMapper extends AbstractXInfoModelTypeAware<IPixelCut> implements ISegmentXInfoRowMapper<IPixelCut> {

	private static Logger log = LoggerFactory.getLogger(PixelCutRowMapper.class);
	
	public PixelCutRowMapper() {
		super(new PixelCut());
	}

	private static final String QUERY_PREFIX = "pxc";
	private static final String ATTRIBUTES = "segment_id AS " + QUERY_PREFIX + "_segment_id, " +
											"graphversion_id AS " + QUERY_PREFIX + "_graphversion_id, " +
											"start_cut_right AS " + QUERY_PREFIX + "_start_cut_right, " +
											"start_cut_left AS " + QUERY_PREFIX + "_start_cut_left, " +
											"end_cut_right AS " + QUERY_PREFIX + "_end_cut_right, " +
											"end_cut_left AS " + QUERY_PREFIX + "_end_cut_left ";

	@Override
	public IBaseSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
		PixelCut px = new PixelCut();
		boolean valid = false;
		try {
			ColumnFinder colFinder = new ColumnFinder(rs);

			if (colFinder.getColumnIndex(QUERY_PREFIX + "_segment_id") > -1) {
				px.setSegmentId(rs.getLong(QUERY_PREFIX + "_segment_id"));
				if (!rs.wasNull()) {
					valid = true;
				}
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_graphversion_id") > -1) {
				px.setGraphVersionId(rs.getLong(QUERY_PREFIX + "_graphversion_id"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_start_cut_right") > -1) {
				px.setStartCutRight(rs.getDouble(QUERY_PREFIX + "_start_cut_right"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_start_cut_left") > -1) {
				px.setStartCutLeft(rs.getDouble(QUERY_PREFIX + "_start_cut_left"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_end_cut_right") > -1) {
				px.setEndCutRight(rs.getDouble(QUERY_PREFIX + "_end_cut_right"));
			}
			if (colFinder.getColumnIndex(QUERY_PREFIX + "_end_cut_left") > -1) {
				px.setEndCutLeft(rs.getDouble(QUERY_PREFIX + "_end_cut_left"));
			}
		} catch (SQLException e) {}

		if (valid) {
			IBaseSegment segment = new BaseSegment();
			segment.setId(px.getSegmentId());
			segment.addXInfo(px);
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
