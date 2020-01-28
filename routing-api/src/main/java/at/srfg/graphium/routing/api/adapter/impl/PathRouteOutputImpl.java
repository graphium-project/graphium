package at.srfg.graphium.routing.api.adapter.impl;

import at.srfg.graphium.routing.api.adapter.IRouteOutput;
import at.srfg.graphium.routing.api.dto.impl.PathRouteDTOImpl;

public class PathRouteOutputImpl implements IRouteOutput<PathRouteDTOImpl, Float> {
	
	@Override
	public String getName() {
		return "path";
	}

	@Override
	public Class<PathRouteDTOImpl> adaptsToClass() {
		return PathRouteDTOImpl.class;
	}

}
