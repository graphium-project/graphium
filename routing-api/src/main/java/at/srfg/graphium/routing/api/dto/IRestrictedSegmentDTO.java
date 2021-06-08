package at.srfg.graphium.routing.api.dto;

import java.time.LocalDateTime;

/**
 * @author mwimmer
 *
 */
public interface IRestrictedSegmentDTO extends IDirectedSegmentDTO {

	LocalDateTime getValidTo();

	void setValidTo(LocalDateTime validTo);
}
