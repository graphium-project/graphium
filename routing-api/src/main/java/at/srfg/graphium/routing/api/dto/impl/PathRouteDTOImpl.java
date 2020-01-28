package at.srfg.graphium.routing.api.dto.impl;

import java.util.List;

import at.srfg.graphium.routing.api.dto.IDirectedSegmentDTO;

public class PathRouteDTOImpl extends OverviewRouteDTOImpl {
	
	private List<IDirectedSegmentDTO> segments;

	public PathRouteDTOImpl(Float weight, float length, int duration, int runtimeInMs, String graphName,
			String graphVersion, String geometry, List<IDirectedSegmentDTO> segments) {
		super(weight, length, duration, runtimeInMs, graphName, graphVersion, geometry);
		this.segments = segments;
	}

	public List<IDirectedSegmentDTO> getSegments() {
		return segments;
	}

	public void setSegments(List<IDirectedSegmentDTO> segments) {
		this.segments = segments;
	}
}
