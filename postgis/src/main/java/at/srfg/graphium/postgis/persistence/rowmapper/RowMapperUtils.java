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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import at.srfg.graphium.model.Access;

public class RowMapperUtils {

	public static Set<Access> convertAccessTypes(ResultSet rs, String fieldName) throws SQLException {
		Array accessTypeArray = rs.getArray(fieldName);
		if (accessTypeArray != null) {
			Integer[] accessTypeIds = (Integer[]) accessTypeArray.getArray();
			int[] accessTypeIds2 = new int[accessTypeIds.length];
			for (int i=0; i<accessTypeIds.length; i++) {
				accessTypeIds2[i] = accessTypeIds[i];
			}
			return Access.getAccessTypes(accessTypeIds2);
		} else {
			return null;
		}
	}
	
}
