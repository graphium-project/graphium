package at.srfg.graphium.routing.cost;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public interface ISegmentCostEvaluator<T extends IBaseWaySegment, C> {

	public C getCost(T segment, boolean directonTow);
	
	public RoutingCriteria getCriteria();
	
}
