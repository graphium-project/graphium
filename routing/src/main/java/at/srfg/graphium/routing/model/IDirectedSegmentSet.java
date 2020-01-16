package at.srfg.graphium.routing.model;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public interface IDirectedSegmentSet {

	public long getId();

	public void setId(long id);

	public Coordinate getStartCoord();

	public void setStartCoord(Coordinate startCoord);

	public Coordinate getEndCoord();

	public void setEndCoord(Coordinate endCoord);

	public List<IDirectedSegment> getSegments();

	public void setSegments(List<IDirectedSegment> segments);
}
