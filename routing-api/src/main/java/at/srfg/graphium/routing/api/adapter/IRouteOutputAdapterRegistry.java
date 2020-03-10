/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.routing.api.adapter;

import java.util.Set;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.dto.IRouteDTO;

public interface IRouteOutputAdapterRegistry<O extends IRouteDTO<W>, W extends Object, T extends IBaseWaySegment> {

	public IRouteOutputAdapter<O, W, T> getAdapter(IRouteOutput<O, W> output);
	
	public IRouteOutputAdapter<O, W, T> getAdapter(String output);
	
	public Set<String> registeredOutputNames();

	public void register(IRouteOutputAdapter<O, W, T> adapter);
	
	
}
