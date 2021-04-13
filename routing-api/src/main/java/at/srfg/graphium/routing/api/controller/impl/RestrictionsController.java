package at.srfg.graphium.routing.api.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.srfg.graphium.routing.api.dto.impl.RestrictedSegmentDTOImpl;
import at.srfg.graphium.routing.api.dto.impl.RestrictedSegmentsContainerDTOImpl;
import at.srfg.graphium.routing.model.IRestrictedSegment;
import at.srfg.graphium.routing.model.impl.RestrictedSegmentImpl;
import at.srfg.graphium.routing.service.IRestrictionsService;

/**
 * @author mwimmer
 *
 */
@Controller
@RequestMapping(value="/blockedroads/graphs")
public class RestrictionsController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private IRestrictionsService service;
	
	@RequestMapping(value = "/{graph}", method=RequestMethod.POST)
	@ResponseBody
	public int setBlockedRoads(
			@PathVariable(value = "graph") String graphName,
			@RequestBody RestrictedSegmentsContainerDTOImpl restrictedSegmentsContainer) {
 		int count = service.setRestrictions(graphName, adapt(restrictedSegmentsContainer));
 		return count;
	}

	@RequestMapping(value = "/{graph}", method=RequestMethod.PUT)
	@ResponseBody
	public int addBlockedRoads(
			@PathVariable(value = "graph") String graphName,
			@RequestBody RestrictedSegmentsContainerDTOImpl restrictedSegmentsContainer) {
		int count = service.addRestrictions(graphName, adapt(restrictedSegmentsContainer));
		return count;
	}
	
	@RequestMapping(value = "/{graph}", method=RequestMethod.DELETE)
	@ResponseBody
	public String clearBlockedRoads(
			@PathVariable(value = "graph") String graphName) {
		service.clearRestrictions(graphName);
		return "true";
	}
	
	private List<IRestrictedSegment> adapt(RestrictedSegmentsContainerDTOImpl restrictedSegmentsContainer) {
		List<IRestrictedSegment> segments = new ArrayList<IRestrictedSegment>();
		for (RestrictedSegmentDTOImpl rseg : restrictedSegmentsContainer.getSegments()) {
			segments.add(new RestrictedSegmentImpl(rseg.getId(), rseg.isLinkDirectionForward(), rseg.getValidFrom(), rseg.getValidTo()));
		}
		return segments;
	}

	public IRestrictionsService getService() {
		return service;
	}

	public void setService(IRestrictionsService service) {
		this.service = service;
	}
	
}
