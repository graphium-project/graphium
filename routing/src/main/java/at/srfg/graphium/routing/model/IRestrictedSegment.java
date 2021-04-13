package at.srfg.graphium.routing.model;

import java.time.LocalDateTime;

/**
 * @author mwimmer
 *
 */
public interface IRestrictedSegment extends IDirectedSegment {

	LocalDateTime getValidFrom();

	void setValidFrom(LocalDateTime validFrom);

	LocalDateTime getValidTo();

	void setValidTo(LocalDateTime validTo);

}
