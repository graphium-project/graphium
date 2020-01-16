package at.srfg.graphium.routing.model.impl;

import at.srfg.graphium.routing.model.IDirectedSegment;

public class DirectedSegmentImpl implements IDirectedSegment {

	private long id;
	private boolean towards;

	public DirectedSegmentImpl(long id, boolean towards) {
		this.id = id;
		this.towards = towards;
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
	public boolean isTowards() {
		return towards;
	}

	@Override
	public void setTowards(boolean towards) {
		this.towards = towards;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (towards ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectedSegmentImpl other = (DirectedSegmentImpl) obj;
		if (id != other.id)
			return false;
		if (towards != other.towards)
			return false;
		return true;
	}

	
	
}
