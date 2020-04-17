/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.routing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluator;
import at.srfg.graphium.routing.cost.ISegmentCostEvaluatorFactory;
import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.impl.RouteImpl;
import at.srfg.graphium.routing.model.impl.RoutingCriteria;
import at.srfg.graphium.routing.service.IDirectedSegmentSetAdapterService;
import at.srfg.graphium.routing.service.IWaySegmentsByIdLoader;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class DirectedSegmentSetToRouteAdapterServiceImpl<T extends IBaseWaySegment, W extends Object> 
	implements IDirectedSegmentSetAdapterService<IRoute<T, W>, T, W> {

	private static Logger log = LoggerFactory.getLogger(DirectedSegmentSetToRouteAdapterServiceImpl.class);
	
	private ISegmentCostEvaluatorFactory<T, Double> costEvaluatorFactory;
	private IWaySegmentsByIdLoader<T> waySegmentsByIdLoader;
	
	public DirectedSegmentSetToRouteAdapterServiceImpl(ISegmentCostEvaluatorFactory<T, Double> costEvaluatorFactory,
			IWaySegmentsByIdLoader<T> waySegmentsByIdLoader
			) {
		this.costEvaluatorFactory = costEvaluatorFactory;
		this.waySegmentsByIdLoader = waySegmentsByIdLoader;
	}
	
	@Override
	public IRoute<T, W> createEmptyRoute() {
		return new RouteImpl<T, W>();
	}

	@Override
	public IRoute<T, W> enrichAndAdapt(IDirectedSegmentSet directedSegments, W calculatedCost, 
			IRoutingOptions options, Float percentageStartWeight, Float percentageEndWeight) {
		IRoute<T, W> route = new RouteImpl<T, W>();
		if(directedSegments == null) {
			// TODO: throw up exception --> no route found			
			return route;
		}
		List<T> segments = new ArrayList<>(directedSegments.getSegments().size());
		
		StopWatch timer = new StopWatch();
		timer.start();
		
		route.setPath(directedSegments.getSegments());
		
	//	List<Long> segmentIds = directedSegments.getSegments().stream().map(segment -> segment.getId()).collect(Collectors.toList());
		Set<Long> segmentIds = new LongOpenHashSet(directedSegments.getSegments().size());
		for(IDirectedSegment ds : directedSegments.getSegments()) {
			segmentIds.add(ds.getId());
		}
		
		// TODO: optimization, avoid recalculation of attribute already calculated by routing algo
		try {
			Map<Long, T> segmentHash = waySegmentsByIdLoader.loadSegments(segmentIds, options.getGraphName(), options.getGraphVersion());
			ISegmentCostEvaluator<T, Double> lengthEvaluator = costEvaluatorFactory.getCostEvaluator(RoutingCriteria.LENGTH);
			ISegmentCostEvaluator<T, Double> durationEvaluator = costEvaluatorFactory.getCostEvaluator(RoutingCriteria.MIN_DURATION);
			
			// loop over second to	second to last element and sum up full costs
			double length = 0f;
			double duration = 0;
			// first element with offset
			IDirectedSegment directedSegment = directedSegments.getSegments().get(0);
			T segment = segmentHash.get(directedSegment.getId());
			length += lengthEvaluator.getCost(segment, directedSegment.isTowards()) * percentageStartWeight;
			duration += durationEvaluator.getCost(segment, directedSegment.isTowards()) * percentageStartWeight;
			segments.add(segment);

			for(int i = 1; i < directedSegments.getSegments().size() -1; i++) {
				directedSegment = directedSegments.getSegments().get(i);
				segment = segmentHash.get(directedSegment.getId());
				length += lengthEvaluator.getCost(segment, directedSegment.isTowards());
				duration += durationEvaluator.getCost(segment, directedSegment.isTowards());
				segments.add(segment);
			}
			
			// last element with offset
			directedSegment = directedSegments.getSegments().get(directedSegments.getSegments().size()-1);
			segment = segmentHash.get(directedSegment.getId());
			length += lengthEvaluator.getCost(segment, directedSegment.isTowards()) * percentageEndWeight;
			duration += durationEvaluator.getCost(segment, directedSegment.isTowards()) * percentageEndWeight;
			segments.add(segment);

			route.setDuration((int) duration);
			route.setLength((float) length);
			
		} catch (GraphNotExistsException e) {
			log.error("error, graph not found", e);
		}
		
		// just logging and debugging 
		 /* switch (options.getCriteria()) {
			case LENGTH: {
				if(route.getLength() != calculatedCost) {
					log.debug("recalculation of length differs from calulcation cost of routing algo. New: " + route.getLength() 
					+ " calculated cost: " + calculatedCost);
				}
				break;
			}
			case CURRENT_DURATION:
			case MIN_DURATION: {
				if(route.getDuration() != calculatedCost) {
					log.debug("recalculation of duration differs from calulcation cost of routing algo. New: " + route.getDuration() 
					+ " calculated cost: " + calculatedCost);
				}
			}				
			default: {
				log.error("Routing criteria: " + options.getCriteria() + " unkown");
				break;
			}
		}*/
		
		
		route.setGraphName(options.getGraphName());
		route.setGraphVersion(options.getGraphVersion());
		route.setSegments(segments);
	
		Coordinate startCoordinate = null;
		Coordinate endCoordinate = null;
		if (options.getCoordinates() != null) {
			startCoordinate = options.getCoordinates().get(0);
			endCoordinate = options.getCoordinates().get(options.getCoordinates().size()-1);
		}
		route.setGeometry(createRouteGeometry(route, startCoordinate, endCoordinate));
		
		timer.stop();
		log.debug("adaption of routing algo result to Route Object took: " + timer.getTime() + " ms");
		return route;
	}
	
	@SuppressWarnings("unchecked")
	protected LineString createRouteGeometry(IRoute<T, W> route, Coordinate startCoord, Coordinate endCoord) {

		Collection<LineString> lineStrings = null;
		if (route.getSegments() != null && !route.getSegments().isEmpty()) {
			LineMerger lineMerger = new LineMerger();
			int i = 0;
			for (T segment : route.getSegments()) {
				if (segment.getGeometry() != null) {
					LineString line = segment.getGeometry();
					if (i == 0 && startCoord != null) {
						line = cutSegment(segment, route.getPath().get(0).isTowards(), startCoord);
					} else if (i == route.getSegments().size() - 1 && endCoord != null) {
						line = cutSegment(segment, !route.getPath().get(route.getPath().size()-1).isTowards(), endCoord);
					}
					lineMerger.add(line);
				}
				i++;
			}
			lineStrings = lineMerger.getMergedLineStrings(); // creates MultiLineStrings!
		}
		
		return lineStrings.iterator().next();
	}

	protected LineString cutSegment(T segment, boolean directionTow, Coordinate coordinate) {
		// refrence startpoint on segment
		LocationIndexedLine indexedStartSeg = new LocationIndexedLine(segment.getGeometry());
	
		// location in line string
		LinearLocation startLoc = indexedStartSeg.project(coordinate);
		
		Geometry cutGeom;
		// depending on direction cut the segment on the correct end
		if (!directionTow) {
			cutGeom = indexedStartSeg.extractLine(indexedStartSeg.getStartIndex(), startLoc);
		} 
		else {
			cutGeom = indexedStartSeg.extractLine(startLoc, indexedStartSeg.getEndIndex());			
		}
		cutGeom.setSRID(segment.getGeometry().getSRID());
		
		// set cut segment
		return (LineString) cutGeom;
	}

}
