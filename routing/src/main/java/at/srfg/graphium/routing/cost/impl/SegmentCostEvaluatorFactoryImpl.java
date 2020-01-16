package at.srfg.graphium.routing.cost.impl;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluator;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluatorFactory;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;

public class SegmentCostEvaluatorFactoryImpl<T extends IBaseWaySegment, C> implements ISegmentCostEvaluatorFactory<T, C> {

	private ISegmentCostEvaluator<T, C> lengthEvaluator;
	private ISegmentCostEvaluator<T, C> durationEvaluator;
	
	public SegmentCostEvaluatorFactoryImpl( ISegmentCostEvaluator<T, C> lengthEvaluator,
			ISegmentCostEvaluator<T, C> durationEvaluator) {
		this.lengthEvaluator = lengthEvaluator;
		this.durationEvaluator = durationEvaluator;
	}
	
	@Override
	public ISegmentCostEvaluator<T, C> getCostEvaluator(RoutingCriteria costAttribute) {
		switch (costAttribute) {
		case LENGTH:
			return lengthEvaluator;
		case MIN_DURATION:
			return durationEvaluator;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
