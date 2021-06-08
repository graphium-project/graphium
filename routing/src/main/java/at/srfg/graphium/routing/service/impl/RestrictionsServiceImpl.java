package at.srfg.graphium.routing.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;

import at.srfg.graphium.routing.model.IRestrictedSegment;
import at.srfg.graphium.routing.model.impl.RestrictionsCache;
import at.srfg.graphium.routing.service.IRestrictionsService;

/**
 * @author mwimmer
 *
 */
public class RestrictionsServiceImpl implements IRestrictionsService {
	
	private int maxTTLInSeconds;
	private Map<String, RestrictionsCache> restrictionsCaches = new HashMap<String, RestrictionsCache>(); // a cache per graph

	@Override
	public int setRestrictions(String graphName, List<IRestrictedSegment> segments) {
		validate(segments);
		if (!restrictionsCaches.containsKey(graphName)) {
			restrictionsCaches.put(graphName, new RestrictionsCache(graphName, new HashSet<>(segments)));
		} else {
			restrictionsCaches.get(graphName).setSegments(new HashSet<>(segments));
		}
		return segments.size();
	}

	@Override
	public int addRestrictions(String graphName, List<IRestrictedSegment> segments) {
		validate(segments);
		if (!restrictionsCaches.containsKey(graphName)) {
			restrictionsCaches.put(graphName, new RestrictionsCache(graphName, new HashSet<>(segments)));
		} else {
			restrictionsCaches.get(graphName).addSegments(new HashSet<>(segments));
		}
		return restrictionsCaches.get(graphName).getSegments().size();
	}

	@Override
	public void clearRestrictions(String graphName) {
		if (restrictionsCaches.containsKey(graphName)) {
			restrictionsCaches.remove(graphName);
		}
	}
	
	@Override
	public boolean isRestrictedSegment(String graphName, long segmentId, boolean towards, LocalDateTime routingTimestamp) {
		if (restrictionsCaches.containsKey(graphName)) {
			return restrictionsCaches.get(graphName).isRestrictedSegment(segmentId, towards, routingTimestamp);
		} else {
			return false;
		}
	}
	
	private void validate(List<IRestrictedSegment> segments) {
		LocalDateTime validFrom = LocalDateTime.now();
		LocalDateTime validTo = validFrom.plusSeconds(maxTTLInSeconds);
		for (IRestrictedSegment segment : segments) {
			if (segment.getValidFrom() == null) {
				segment.setValidFrom(validFrom);
			}
			if (segment.getValidTo() == null || segment.getValidTo().isAfter(validTo)) {
				segment.setValidTo(validTo);
			}
		}
	}

	@Scheduled(fixedDelay=60000)
	protected void removeOutdated() {
		LocalDateTime now = LocalDateTime.now();
		for (String key : restrictionsCaches.keySet()) {
			List<IRestrictedSegment> segmentsToRemove = new ArrayList<>();
			RestrictionsCache resCache = restrictionsCaches.get(key);
			for (IRestrictedSegment resSeg : resCache.getSegments()) {
				if (resSeg.getValidTo().isBefore(now)) {
					segmentsToRemove.add(resSeg);
				}
			}
			resCache.getSegments().removeAll(segmentsToRemove);
		}
	}

	public int getMaxTTLInSeconds() {
		return maxTTLInSeconds;
	}

	public void setMaxTTLInSeconds(int maxTTLInSeconds) {
		this.maxTTLInSeconds = maxTTLInSeconds;
	}
	
}
