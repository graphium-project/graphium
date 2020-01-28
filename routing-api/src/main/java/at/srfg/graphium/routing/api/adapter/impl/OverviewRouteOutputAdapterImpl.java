package at.srfg.graphium.routing.api.adapter.impl;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapter;
import at.srfg.graphium.routing.api.dto.impl.OverviewRouteDTOImpl;
import at.srfg.graphium.routing.model.IRoute;

public class OverviewRouteOutputAdapterImpl<T extends IBaseWaySegment>
	implements IRouteOutputAdapter<OverviewRouteDTOImpl, Float, T> {

	private IRouteOutput<OverviewRouteDTOImpl, Float> output;
	
	public OverviewRouteOutputAdapterImpl() {
		this.output = new OverviewRouteOutputImpl();
	}
	
	@Override
	public IRouteOutput<OverviewRouteDTOImpl, Float> adaptsTo() {
		return output;
	}

	@Override
	public OverviewRouteDTOImpl adapt(IRoute<T, Float> route) {
		// TODO Auto-generated method stub
		return null;
	}

}
