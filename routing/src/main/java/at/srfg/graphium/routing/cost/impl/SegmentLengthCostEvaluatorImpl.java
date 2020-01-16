package at.srfg.graphium.routing.cost.impl;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluator;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public class SegmentLengthCostEvaluatorImpl<T extends IBaseWaySegment> implements ISegmentCostEvaluator<T, Double> {

	@Override
	public Double getCost(T segment, boolean directionTow) {
		return new Double(segment.getLength());
	}

	@Override
	public RoutingCriteria getCriteria() {
		return RoutingCriteria.LENGTH;
	}

}
