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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.postgis.persistence.impl;

import java.util.List;

import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.postgis.model.IXInfoTest;
import at.srfg.graphium.postgis.model.impl.XInfoTest;

/**
 * @author mwimmer
 */
public class XInfoTestDaoImpl extends AbstractSegmentXInfoTypeAwareDao<IXInfoTest> implements IXInfoDao<IXInfoTest> {

	private final String TABLENAME = "xinfo_test";
	private String CREATE_STMT = "CREATE TABLE " + "%SCHEMA%" + TABLENAME + "	( " +
			  " graph_id bigint, directed_id bigint ) INHERITS (graphs.xinfo) WITH ( OIDS=FALSE )";

	public XInfoTestDaoImpl() {
		super(new XInfoTest());
	}

	@Override
	public void setup() {
		if (!checkIfTableExists()) {
			getJdbcTemplate().execute(CREATE_STMT);
		}
	}

	@Override
	public void streamSegments(ISegmentOutputFormat<IBaseSegment> outputFormat, String graphName, String version) {

	}

	@Override
	protected String getSelectionAttributesAsString() {
		//TODO implement me
		return null;
	}

	private boolean checkIfTableExists() {
		String query = "SELECT count(1) FROM pg_tables WHERE tablename = '" + TABLENAME + "'";
		return getJdbcTemplate().queryForObject(query, Integer.class) > 0;
	}

	@Override
	public void setSchema(String schema) {
		super.setSchema(schema);
		CREATE_STMT = CREATE_STMT.replace("%SCHEMA%", schema);
	}

	@Override
	public IBaseSegment get(String graphName, String version, long segmentId) {
		return null;
	}

	@Override
	public void save(String graphName, String version, IXInfoTest xInfo) {
		getJdbcTemplate().update("INSERT INTO " + schema + TABLENAME + " (segment_id, direction_tow, graph_id, directed_id) VALUES (?,?,?,?)",
				xInfo.getSegmentId(), xInfo.isDirectionTow(), xInfo.getGraphId(), xInfo.getDirectedId());
	}

	@Override
	public void save(String graphName, String version, List<IXInfoTest> xInfoList) {
		for (IXInfoTest xInfo : xInfoList) {
			save(graphName, version, xInfo);
		}
	}

	@Override
	public void update(String graphName, String version, IXInfoTest xInfo) {
	}

	@Override
	public void update(String graphName, String version, List<IXInfoTest> xInfoList) {
	}

	@Override
	public void delete(String graphName, String version, IXInfoTest xInfo) {
	}

	@Override
	public void deleteAll(String graphName, String version) {
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

}
