/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.core.helper;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.NoSegmentAdapterFoundException;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.hd.IHDWaySegment;

/**
 * @author mwimmer
 *
 */
public class GraphMetadataHelper<T extends IBaseSegment> {

	protected ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry;
	
	public boolean isHDGraph(IWayGraphVersionMetadata metadata) throws NoSegmentAdapterFoundException {
		ISegmentAdapter<IBaseSegmentDTO, T> adapter = adapterRegistry.getAdapterForType(metadata.getType());
		boolean hdGraph = false;
		if (adapter != null) {
			hdGraph = IHDWaySegment.class.isAssignableFrom(adapter.getModelClass());
		}
		return hdGraph;
	}

	public ISegmentAdapterRegistry<IBaseSegmentDTO, T> getAdapterRegistry() {
		return adapterRegistry;
	}

	public void setAdapterRegistry(ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
	}
	
}