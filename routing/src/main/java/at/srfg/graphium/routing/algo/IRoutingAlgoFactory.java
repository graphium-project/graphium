package at.srfg.graphium.routing.algo;

import at.srfg.graphium.routing.exception.UnkownRoutingAlgoException;
import at.srfg.graphium.routing.model.IRoutingOptions;

/**
 * interface for factory creating instances of routing algorithms based on passed options
 * 
 * @author anwagner
 *
 * @param <O> type of options object
 * @param <N> type native graph representation object
 * @param <W> type of weight
 */
public interface IRoutingAlgoFactory<O extends IRoutingOptions, N, W> {

	/**
	 * creates instance of the algorithm
	 * 
	 * @param routeOptions options for algorithm
	 * @param startNode start node in native graph representation
	 * @param percentageStartWeight % of weight to use on start node (in digit. direction of waysegment)
	 * @param endNode end node in native graph representation
	 * @param percentageEndWeight  % of weight to use on end node (in digit. direction of waysegment)
	 * @return instance of configured routing algorithm
	 * 
	 * @throws UnkownRoutingAlgoException in case the asked algo implementation can not be supplied by factory instance
	 */
	public IRoutingAlgo<IRoutingOptions, N, W> createInstance(O routeOptions, N startNode, 
			Float percentageStartWeight, N endNode, Float percentageEndWeight) throws UnkownRoutingAlgoException;

}
