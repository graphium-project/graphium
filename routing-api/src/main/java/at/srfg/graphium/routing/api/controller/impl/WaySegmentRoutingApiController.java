package at.srfg.graphium.routing.api.controller.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.model.IRoutingOptions;

@Controller
public class WaySegmentRoutingApiController extends GenericRoutingApiController<IWaySegment, IRoutingOptions, Float>  {

	public WaySegmentRoutingApiController() { super(); }
	
	@PostConstruct
	public void setup()
	{
		super.setup();
	}
}
