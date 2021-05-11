package at.srfg.graphium.routing.model.impl;

import java.time.LocalDateTime;

import at.srfg.graphium.routing.model.IRestrictedSegment;

/**
 * @author mwimmer
 *
 */
public class RestrictedSegmentImpl extends DirectedSegmentImpl implements IRestrictedSegment {

	private LocalDateTime validFrom;
	private LocalDateTime validTo;
	
	public RestrictedSegmentImpl(long id, boolean towards, LocalDateTime validFrom, LocalDateTime validTo) {
		super(id, towards);
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	@Override
	public LocalDateTime getValidTo() {
		return validTo;
	}

	@Override
	public void setValidTo(LocalDateTime validTo) {
		this.validTo = validTo;
	}

	@Override
	public LocalDateTime getValidFrom() {
		return validFrom;
	}

	@Override
	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}

}
