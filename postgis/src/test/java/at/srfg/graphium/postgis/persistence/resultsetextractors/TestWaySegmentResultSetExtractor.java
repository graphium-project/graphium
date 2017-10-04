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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @author mwimmer
 *
 */
public class TestWaySegmentResultSetExtractor {

	@Test
	public void testParseConString() {
		String serializedCon = "(100000833,960301,101021339,\"{15,4,22,2,9,19,11,13,12,1,10,3}\",24)";
		
		String stripedSerializedCon = StringUtils.removeStart(serializedCon, "(");
		stripedSerializedCon = StringUtils.removeEnd(stripedSerializedCon, ")");
		stripedSerializedCon = stripedSerializedCon.substring(0, stripedSerializedCon.indexOf("}"));
		stripedSerializedCon = stripedSerializedCon.replace("\"", ""); //100000833,960301,101021339,{15,4,22,2,9,19,11,13,12,1,10,3,24
		String[] splitCons = stripedSerializedCon.split("\\{"); //[100000833,960301,101021339,, 15,4,22,2,9,19,11,13,12,1,10,3,24]
		String[] tokens = splitCons[0].split(",");
		String[] accessTypeIdsArray = splitCons[1].split(","); //[15, 4, 22, 2, 9, 19, 11, 13, 12, 1, 10, 3, 24] 

		System.out.println("tokens:");
		for (String token : tokens) {
			System.out.println(token);
		}

		System.out.println("\naccessTypes:");
		for (String accessTypeId : accessTypeIdsArray) {
			System.out.println(accessTypeId);
		}

	}
	
}
