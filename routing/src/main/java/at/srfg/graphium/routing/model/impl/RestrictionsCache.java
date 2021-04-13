package at.srfg.graphium.routing.model.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.srfg.graphium.routing.model.IRestrictedSegment;

/**
 * @author mwimmer
 *
 */
public class RestrictionsCache {
	
	private String graphName;
	private Map<IRestrictedSegment, IRestrictedSegment> segments;
	
	public RestrictionsCache(String graphName, Set<IRestrictedSegment> segments) {
		super();
		this.graphName = graphName;
		setSegments(segments);
	}
	
	public String getGraphName() {
		return graphName;
	}
	
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	
	public Set<IRestrictedSegment> getSegments() {
		return segments.keySet();
	}
	
	public void setSegments(Set<IRestrictedSegment> segments) {
		this.segments = new HashMap<>();
		addSegments(segments);
	}
	
	public void addSegments(Set<IRestrictedSegment> segments) {
		segments.forEach(seg -> this.segments.put(seg, seg));
	}
	
	public boolean isRestrictedSegment(long segmentId, boolean towards, LocalDateTime routingTimestamp) {
		IRestrictedSegment testSegment = new RestrictedSegmentImpl(segmentId, towards, null, null);
		if (segments.containsKey(testSegment)) {
			if (routingTimestamp != null) {
				IRestrictedSegment segment = segments.get(testSegment);
				if (segment.getValidFrom().isBefore(routingTimestamp) &&
					segment.getValidTo().isAfter(routingTimestamp)) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
}
