package at.srfg.graphium.routing.model;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public interface ICoordinateSet {

	public long getId();

	public void setId(long id);

	public List<Coordinate> getCoordinates();

	public void setCoordinates(List<Coordinate> coordinates);

}
