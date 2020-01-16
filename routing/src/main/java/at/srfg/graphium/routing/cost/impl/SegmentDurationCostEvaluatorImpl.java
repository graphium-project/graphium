package at.srfg.graphium.routing.cost.impl;

import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluator;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public class SegmentDurationCostEvaluatorImpl<T extends IWaySegment> implements ISegmentCostEvaluator<T, Double> {

	@Override
	public Double getCost(T segment, boolean directionTow) {
		return new Double(segment.getMinDuration(directionTow));
	}

	@Override
	public RoutingCriteria getCriteria() {
		return RoutingCriteria.MIN_DURATION;
	}

}
