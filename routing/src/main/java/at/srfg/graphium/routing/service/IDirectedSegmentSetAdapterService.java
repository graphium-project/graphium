package at.srfg.graphium.routing.service;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;

public interface IDirectedSegmentSetAdapterService<T extends IRoute<N, W>, N extends IBaseWaySegment, W extends Object> {

	T enrichAndAdapt(IDirectedSegmentSet directedSegments, W calculatedCost,
			IRoutingOptions options, Float percentageStartWeight, Float percentageEndWeight);

}
