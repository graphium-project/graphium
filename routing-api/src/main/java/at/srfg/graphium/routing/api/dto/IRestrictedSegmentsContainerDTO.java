package at.srfg.graphium.routing.api.dto;

import java.util.List;

/**
 * @author mwimmer
 *
 */
public interface IRestrictedSegmentsContainerDTO {
	
	List<IRestrictedSegmentDTO> getSegments();
	
	void setSegments(List<IRestrictedSegmentDTO> segments);

}
