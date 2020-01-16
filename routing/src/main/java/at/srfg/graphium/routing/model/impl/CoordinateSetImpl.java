package at.srfg.graphium.routing.model.impl;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.routing.model.ICoordinateSet;

public class CoordinateSetImpl implements ICoordinateSet {

	private long id;
	private List<Coordinate> coordinates;
	
	public CoordinateSetImpl() {}
	
	public CoordinateSetImpl(long id, List<Coordinate> coordinates) {
		this.id = id;
		this.coordinates = coordinates;
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
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	@Override
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

}
