package at.srfg.graphium.routing.api.dto;

import java.util.List;

public interface IDirectedSegmentSetDTO {

	public long getId();

	public void setId(long id);

	public Double getStartCoordX();

	public void setStartCoordX(Double startCoordX);

	public Double getStartCoordY();

	public void setStartCoordY(Double startCoordY);

	public Double getEndCoordX();

	public void setEndCoordX(Double endCoordX);

	public Double getEndCoordY();

	public void setEndCoordY(Double endCoordY);

	public List<IDirectedSegmentDTO> getSegments();

	public void setSegments(List<IDirectedSegmentDTO> segments);

}
