package at.srfg.graphium.routing.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;

public class DirectedSegmentSetImpl implements IDirectedSegmentSet {

	private long id;
	private Coordinate startCoord;
	private Coordinate endCoord;
	private List<IDirectedSegment> segments = new ArrayList<>();
	private double lengthSum;
	
	public DirectedSegmentSetImpl() {}
	
	public DirectedSegmentSetImpl(long id, List<IDirectedSegment> segments) {
		this.id = id;
		this.segments = segments;
	}
	
	public DirectedSegmentSetImpl(long id, Coordinate startCoord, Coordinate endCoord, List<IDirectedSegment> segments) {
		this.id = id;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.segments = segments;
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
	public Coordinate getStartCoord() {
		return startCoord;
	}

	@Override
	public void setStartCoord(Coordinate startCoord) {
		this.startCoord = startCoord;
	}

	@Override
	public Coordinate getEndCoord() {
		return endCoord;
	}

	@Override
	public void setEndCoord(Coordinate endCoord) {
		this.endCoord = endCoord;
	}
	
	@Override
	public List<IDirectedSegment> getSegments() {
		return segments;
	}

	@Override
	public void setSegments(List<IDirectedSegment> segments) {
		this.segments = segments;
	}

}
