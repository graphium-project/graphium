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
