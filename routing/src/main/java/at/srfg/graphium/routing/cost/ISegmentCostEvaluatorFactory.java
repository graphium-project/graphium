package at.srfg.graphium.routing.cost;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public interface ISegmentCostEvaluatorFactory<T extends IBaseWaySegment, C> {

	public ISegmentCostEvaluator<T, C> getCostEvaluator(RoutingCriteria costAttribute);
	
}
