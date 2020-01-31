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
package at.srfg.graphium.routing.api.adapter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapter;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapterRegistry;
import at.srfg.graphium.routing.api.dto.IRouteDTO;

public class RouteOutputAdapterRegistryImpl<O extends IRouteDTO<W>, W extends Object, T extends IBaseWaySegment> 
	implements IRouteOutputAdapterRegistry<O, W, T> {

	private Map<IRouteOutput<O, W>, IRouteOutputAdapter<O, W, T>> adapters;
	private Map<String, IRouteOutputAdapter<O, W, T>> adaptersPerString;
	
	// autodetected adapters
	@Autowired(required = false)
	private List<IRouteOutputAdapter<O, W, T>> injectedAdapters;

	@PostConstruct
	public void init() {
		for (IRouteOutputAdapter<O, W, T> adapter : injectedAdapters) {
			register(adapter);
		}
	}

	public RouteOutputAdapterRegistryImpl() {
		adapters = new HashMap<>();
		adaptersPerString = new HashMap<>();
	}
	
	@Override
	public IRouteOutputAdapter<O, W, T> getAdapter(IRouteOutput<O, W> output) {
		return adapters.get(output);
	}
	
	@Override
	public IRouteOutputAdapter<O, W, T> getAdapter(String output) {
		return adaptersPerString.get(output);
	}

	@Override
	public Set<String> registeredOutputNames() {
		return adapters.keySet().stream().map(IRouteOutput::getName).collect(Collectors.toSet());
	}
	
	@Override
	public void register(IRouteOutputAdapter<O, W, T> adapter) {
		this.adapters.put(adapter.adaptsTo(), adapter);
		this.adaptersPerString.put(adapter.adaptsTo().getName(), adapter);
	}
	
}
