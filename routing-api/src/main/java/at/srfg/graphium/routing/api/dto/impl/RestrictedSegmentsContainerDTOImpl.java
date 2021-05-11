package at.srfg.graphium.routing.api.dto.impl;

import java.util.List;

/**
 * @author mwimmer
 *
 */
public class RestrictedSegmentsContainerDTOImpl {

	private List<RestrictedSegmentDTOImpl> segments;
	
	public RestrictedSegmentsContainerDTOImpl() {}
	
	public RestrictedSegmentsContainerDTOImpl(List<RestrictedSegmentDTOImpl> segments) {
		this.segments = segments;
	}
	
	public List<RestrictedSegmentDTOImpl> getSegments() {
		return segments;
	}

	public void setSegments(List<RestrictedSegmentDTOImpl> segments) {
		this.segments = segments;
	}

}
