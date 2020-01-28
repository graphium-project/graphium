package at.srfg.graphium.routing.api.adapter;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.dto.IRouteDTO;
import at.srfg.graphium.routing.model.IRoute;

public interface IRouteOutputAdapter<O extends IRouteDTO<W>, W extends Object, T extends IBaseWaySegment> {

	public IRouteOutput<O, W> adaptsTo();
	
	public O adapt(IRoute<T, W> route);
}
