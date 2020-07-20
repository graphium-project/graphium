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

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import at.srfg.graphium.model.Access;

/**
 * @author mwimmer
 *
 */
public abstract class AbstractWayGraphDaoImpl extends AbstractDaoImpl {

	public final static String PARENT_SEGMENT_TABLE_NAME = "waysegments";
	public final static String PARENT_CONNECTION_TABLE_NAME = "waysegment_connections";
	public final static String SEGMENT_TABLE_PREFIX = "waysegments_";
	public final static String CONNECTION_TABLE_PREFIX = "waysegment_connections_";
	public final static String METADATA_TABLE_NAME = "waygraphmetadata";
	public final static String VIEW_TABLE_NAME = "waygraph_view_metadata";
	public final static String WAYGRAPH_TABLE_NAME = "waygraphs";
	public final static String SOURCE_TABLE_NAME = "sources";
	
	protected Array convertToArray(Connection con, Set<Access> accessTypes) throws SQLException {
		Integer[] accessIds;
		if (accessTypes != null) {
			accessIds = new Integer[accessTypes.size()];
			int j = 0;
			for (Access access : accessTypes) {
				accessIds[j++] = access.getId();
			}
		} else {
			accessIds = new Integer[0];
		}
		return con.createArrayOf("smallint", accessIds);
	}


}

