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
package at.srfg.graphium.io.adapter.impl;

import java.util.HashMap;
import java.util.Map;

import at.srfg.graphium.io.dto.impl.HDRegulatoryElementDTO;
import at.srfg.graphium.model.impl.HDRegulatoryElement;

public class HDRegulatoryElementXInfoAdapter extends AbstractXInfoDTOAdapter<HDRegulatoryElement, HDRegulatoryElementDTO> {

	public HDRegulatoryElementXInfoAdapter() {
		super(new HDRegulatoryElement(), new HDRegulatoryElementDTO());
	}

	@Override
	public HDRegulatoryElementDTO adaptReverse(HDRegulatoryElement objectToAdapt) {
		HDRegulatoryElementDTO dto = new HDRegulatoryElementDTO();
		if (objectToAdapt.getTags() != null) {
			Map<String, String> tags = new HashMap<>();
			objectToAdapt.getTags().forEach(tags::put);
			dto.setTags(tags);
		}
        return dto;
	}

	@Override
	public HDRegulatoryElement adapt(HDRegulatoryElementDTO objectToAdapt) {
		HDRegulatoryElement xInfo = new HDRegulatoryElement();
		if (objectToAdapt.getTags() != null) {
			Map<String, String> tags = new HashMap<>();
			objectToAdapt.getTags().forEach(tags::put);
			xInfo.setTags(tags);
		}
        return xInfo;
	}

}
