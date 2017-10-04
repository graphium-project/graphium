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
package at.srfg.graphium.model.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

public class BaseSegmentGraphModelFactory extends AbstractGraphModelFactory<IBaseSegment> {

	@Override
	public IBaseSegment newSegment() {
		return new BaseSegment();
	}

	@Override
	public IBaseSegment newSegment(long id,
			Map<String, List<ISegmentXInfo>> xInfo) {
		return new BaseSegment(id, xInfo, null);
	}

	@Override
	public IBaseSegment newSegment(long id,
			Map<String, List<ISegmentXInfo>> xInfo,
			List<IWaySegmentConnection> cons) {
		return new BaseSegment(id, xInfo, cons);
	}

	public IBaseSegment newSegment(ISegmentXInfo segmentXInfo) {
		IBaseSegment segment = this.newSegment();
		segment.setId(segmentXInfo.getSegmentId());
		segment.setXInfo(Collections.singletonList(segmentXInfo));
		return segment;
	}
}
