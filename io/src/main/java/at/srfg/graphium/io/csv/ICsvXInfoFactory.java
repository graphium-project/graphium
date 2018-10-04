/**
 * Copyright © 2018 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.io.csv;

import java.util.Map;

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.model.ISegmentXInfo;

/**
 * Created by mwimmer
 */
public interface ICsvXInfoFactory<X extends ISegmentXInfo, D extends ISegmentXInfoDTO> extends IXInfoDTOAdapter<X, D> {

	/**
	 * 
	 * @param attributes map with key=CSV header, value=attributes of one CSV line
	 * @return
	 */
	X adapt(Map<String, String> attributes);
		
}
