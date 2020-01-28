package at.srfg.graphium.routing.api.adapter.impl;

import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.dto.impl.OverviewRouteDTOImpl;

public class OverviewRouteOutputImpl implements IRouteOutput<OverviewRouteDTOImpl, Float> {
	
	@Override
	public String getName() {
		return "overview";
	}

	@Override
	public Class<OverviewRouteDTOImpl> adaptsToClass() {
		return OverviewRouteDTOImpl.class;
	}

}
