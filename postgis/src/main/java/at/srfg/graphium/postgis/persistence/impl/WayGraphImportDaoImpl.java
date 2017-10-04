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

import at.srfg.graphium.postgis.persistence.IWayGraphImportDao;

/**
 * @author mwimmer
 *
 */
public class WayGraphImportDaoImpl extends AbstractDaoImpl implements IWayGraphImportDao {

	private final static String SEGMENTTABLEPREFIX = "waysegments_";
	private final static String CONNECTIONTABLEPREFIX = "waysegment_connections_";
	
	@Override
	public void deleteInvalidConnections(String graphVersionName) {
		getJdbcTemplate().update("DELETE FROM " + schema + CONNECTIONTABLEPREFIX + graphVersionName + " WHERE " +
				" from_segment_id NOT IN (select id from " + schema + SEGMENTTABLEPREFIX + graphVersionName + ") " +
				" OR to_segment_id NOT IN (select id from " + schema + SEGMENTTABLEPREFIX + graphVersionName + ")");
	}

}