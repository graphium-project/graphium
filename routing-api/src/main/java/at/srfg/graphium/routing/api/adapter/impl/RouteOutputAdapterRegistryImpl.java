package at.srfg.graphium.routing.api.adapter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapter;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapterRegistry;
import at.srfg.graphium.routing.api.dto.IRouteDTO;

public class RouteOutputAdapterRegistryImpl<O extends IRouteDTO<W>, W extends Object, T extends IBaseWaySegment> 
	implements IRouteOutputAdapterRegistry<O, W, T> {

	private Map<IRouteOutput<O, W>, IRouteOutputAdapter<O, W, T>> adapters;
	private Map<String, IRouteOutputAdapter<O, W, T>> adaptersPerString;
	
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
