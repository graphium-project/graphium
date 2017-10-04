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
package at.srfg.graphium.gipimport.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FrcMapper {

	public static String frcSetToString(Set<Integer> frcList) {
		StringBuilder frcStr = new StringBuilder("frc=");
		for (Integer frcEl : frcList) {
			frcStr.append(frcEl).append(',');
		}
		return frcStr.deleteCharAt(frcStr.lastIndexOf(",")).toString();
	}

	public static Set<Integer> frcStringToSet(String frcStr) {
		if (frcStr == null || 
			frcStr.isEmpty() || 
			frcStr.equals("frc=") ||
			!frcStr.startsWith("frc=")) {
			return null;
		}
		String[] result = frcStr.substring(4).split(",");
		if (result.length == 0) {
			return null;
		}
		Set<String> frcStringSet = new HashSet<>();
		Collections.addAll(frcStringSet,result);
		Set<Integer> frcSet = new HashSet<Integer>();
		for (String frc : frcStringSet) {
			frcSet.add(Integer.parseInt(frc));
		}
		return frcSet;
	}
	
}