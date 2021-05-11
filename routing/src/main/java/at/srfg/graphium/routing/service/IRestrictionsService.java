package at.srfg.graphium.routing.service;

import java.time.LocalDateTime;
import java.util.List;

import at.srfg.graphium.routing.model.IRestrictedSegment;

/**
 * @author mwimmer
 *
 */
public interface IRestrictionsService {

	int setRestrictions(String graphName, List<IRestrictedSegment> segments);

	int addRestrictions(String graphName, List<IRestrictedSegment> segments);

	void clearRestrictions(String graphName);

	boolean isRestrictedSegment(String graphName, long segmentId, boolean towards, LocalDateTime routingTimestamp);

}