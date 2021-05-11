package at.srfg.graphium.routing.api.dto.impl;

import at.srfg.graphium.routing.api.dto.IDirectedSegmentDTO;

public class DirectedSegmentDTOImpl implements IDirectedSegmentDTO {
	
	private long id;
	private boolean linkDirectionForward;

	public DirectedSegmentDTOImpl() {}
	
	public DirectedSegmentDTOImpl(long id, boolean linkDirectionForward) {
		this.id = id;
		this.linkDirectionForward = linkDirectionForward;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean isLinkDirectionForward() {
		return linkDirectionForward;
	}

	@Override
	public void setLinkDirectionForward(boolean linkDirectionForward) {
		this.linkDirectionForward = linkDirectionForward;
	}

}
