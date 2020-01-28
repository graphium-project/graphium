package at.srfg.graphium.routing.api.adapter;

import at.srfg.graphium.routing.api.dto.IRouteDTO;

public interface IRouteOutput<T extends IRouteDTO<W>, W extends Object> {

	public String getName();
	
	public Class<T> adaptsToClass();
	
}
