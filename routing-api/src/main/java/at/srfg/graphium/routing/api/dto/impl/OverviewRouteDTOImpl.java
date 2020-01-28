package at.srfg.graphium.routing.api.dto.impl;

public class OverviewRouteDTOImpl extends BaseRouteDTOImpl<Float> {

	public OverviewRouteDTOImpl(Float weight, float length, int duration, int runtimeInMs, String graphName,
			String graphVersion, String geometry) {
		super(weight, length, duration, runtimeInMs, graphName, graphVersion, geometry);
	}

}
