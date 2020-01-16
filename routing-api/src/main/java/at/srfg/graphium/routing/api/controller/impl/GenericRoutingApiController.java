/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.api.controller.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.routing.api.dto.impl.RouteDtoImpl;
import at.srfg.graphium.routing.api.dto.impl.RouteSegmentDtoImpl;
import at.srfg.graphium.routing.api.dto.impl.RoutingErrorDtoImpl;
import at.srfg.graphium.routing.exception.RoutingException;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.IRoutingOptionsFactory;
import at.srfg.graphium.routing.service.IRoutingService;

public abstract class GenericRoutingApiController<T extends IBaseWaySegment> {

	private static Logger log = LoggerFactory
			.getLogger(GenericRoutingApiController.class);

	private IRoutingOptionsFactory routingOptionsFactory;
	private IRoutingService<T> routeService;
	private IAdapter<RouteDtoImpl<T>, IRoute<T>> routeAdapter;
	private IAdapter<List<RouteSegmentDtoImpl>, List<T>> routeSegmentsAdapter;

	@PostConstruct
	public void setup() {
		log.info(this.getClass().getSimpleName() + " set up");
	}

	@RequestMapping(value = "/graphs/{graphName}/routing/getRoute.do", method=RequestMethod.GET)
	public ModelAndView getRouteSimple(
			@PathVariable(value = "graphName") String graphName,
			@RequestParam(value = "timestamp", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date routingTimestamp,
			@RequestParam(value = "startX", required = true) double startX,
			@RequestParam(value = "startY", required = true) double startY,
			@RequestParam(value = "endX", required = true) double endX,
			@RequestParam(value = "endY", required = true) double endY,
			@RequestParam(value = "cutsegments", required = false, defaultValue = "true") boolean cutStartAndEndSegment,
			@RequestParam(value = "routingMode", required = false, defaultValue = "CAR") String routingMode,
			@RequestParam(value = "routingCriteria", required = false, defaultValue = "LENGTH") String routingCriteria,
			@RequestParam(value = "routingAlgorithm", required = false, defaultValue = "DIJKSTRA") String routingAlgorithm)
			throws RoutingException {
		log.info("got routing request in graph " + graphName + " from "
				+ startX + "," + startY + " to " + endX + "," + endY);
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		IRoutingOptions options = routingOptionsFactory.createRoutingOptions(
				graphName, null, routingTimestamp, routingMode, routingCriteria, routingAlgorithm);

		IRoute<T> route = routeService.route(options, startX, startY, endX,
				endY, cutStartAndEndSegment);
		
		RouteDtoImpl<T> routeDto = null;
		
		if (route != null) {
			routeDto = routeAdapter.adapt(route);
		}
		
		ModelAndView modelAndView = new ModelAndView(view, "route", routeDto);

		return modelAndView;
	}

	// coordinates --> Array mit x/y Pairs
	// output --> overview, default / path?, details
	// avoid --> bbox? 
	// insgesamt, routing GET mit weniger Optionen, routing POST mit mehr?
	@RequestMapping(value = "/graphs/{graphName}/versions/{graphVersion}/routing/getRoute.do", method=RequestMethod.GET)
	public ModelAndView getRouteSimpleForVersion(
			@PathVariable(value = "graphName") String graphName,
			@PathVariable(value = "graphVersion") String graphVersion,
			@RequestParam(value = "startX", required = true) double startX,
			@RequestParam(value = "startY", required = true) double startY,
			@RequestParam(value = "endX", required = true) double endX,
			@RequestParam(value = "endY", required = true) double endY,
			@RequestParam(value = "cutsegments", required = false, defaultValue = "true") boolean cutStartAndEndSegment,
			@RequestParam(value = "routingMode", required = false, defaultValue = "CAR") String routingMode,
			@RequestParam(value = "routingCriteria", required = false, defaultValue = "LENGTH") String routingCriteria,
			@RequestParam(value = "routingAlgorithm", required = false, defaultValue = "DIJKSTRA") String routingAlgorithm)
			throws RoutingException {
		log.info("got routing request in graph " + graphName + " from "
				+ startX + "," + startY + " to " + endX + "," + endY + " / Options: cutSegments=" + cutStartAndEndSegment + 
				", routingMode=" + routingMode + ", routingCriteria=" + routingCriteria);
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		IRoutingOptions options = routingOptionsFactory.createRoutingOptions(
				graphName, graphVersion, null, routingMode, routingCriteria, routingAlgorithm);

		IRoute<T> route = routeService.route(options, startX, startY, endX,
				endY, cutStartAndEndSegment);
				
		RouteDtoImpl<T> routeDto = routeAdapter.adapt(route);
		
		ModelAndView modelAndView = new ModelAndView(view, "route", routeDto);

		return modelAndView;
	}

	@RequestMapping(value = "/graphs/{graphName}/routing/getRouteSegments.do", method=RequestMethod.GET)
	public ModelAndView getRouteSegments(
			@PathVariable(value = "graphName") String graphName,
			@RequestParam(value = "timestamp", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date routingTimestamp,
			@RequestParam(value = "startX", required = true) double startX,
			@RequestParam(value = "startY", required = true) double startY,
			@RequestParam(value = "endX", required = true) double endX,
			@RequestParam(value = "endY", required = true) double endY,
			@RequestParam(value = "cutsegments", required = false, defaultValue = "true") boolean cutStartAndEndSegment,
			@RequestParam(value = "routingMode", required = false, defaultValue = "CAR") String routingMode,
			@RequestParam(value = "routingCriteria", required = false, defaultValue = "LENGTH") String routingCriteria,
			@RequestParam(value = "routingAlgorithm", required = false, defaultValue = "DIJKSTRA") String routingAlgorithm)
			throws RoutingException {
		IRoutingOptions options = routingOptionsFactory.createRoutingOptions(
				graphName, null, routingTimestamp, routingMode, routingCriteria, routingAlgorithm);
		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		IRoute<T> route = routeService.route(options, startX, startY, endX,
				endY, cutStartAndEndSegment);
		view.setExtractValueFromSingleKeyModel(true);
		ModelAndView modelAndView = new ModelAndView(view, "segments", routeSegmentsAdapter.adapt(route.getSegments()));
		return modelAndView;
	}
	
	@RequestMapping(value = "/graphs/{graphName}/versions/{graphVersion}/routing/getRouteSegments.do", method=RequestMethod.GET)
	public ModelAndView getRouteSegmentsForVersion(
			@PathVariable(value = "graphName") String graphName,
			@PathVariable(value = "graphVersion") String graphVersion,
			@RequestParam(value = "startX", required = true) double startX,
			@RequestParam(value = "startY", required = true) double startY,
			@RequestParam(value = "endX", required = true) double endX,
			@RequestParam(value = "endY", required = true) double endY,
			@RequestParam(value = "cutsegments", required = false, defaultValue = "true") boolean cutStartAndEndSegment,
			@RequestParam(value = "routingMode", required = false, defaultValue = "CAR") String routingMode,
			@RequestParam(value = "routingCriteria", required = false, defaultValue = "LENGTH") String routingCriteria,
			@RequestParam(value = "routingAlgorithm", required = false, defaultValue = "DIJKSTRA") String routingAlgorithm)
			throws RoutingException {
		IRoutingOptions options = routingOptionsFactory.createRoutingOptions(
				graphName, graphVersion, null, routingMode, routingCriteria, routingAlgorithm);
		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		IRoute<T> route = routeService.route(options, startX, startY, endX,
				endY, cutStartAndEndSegment);
		view.setExtractValueFromSingleKeyModel(true);
		ModelAndView modelAndView = new ModelAndView(view, "segments", routeSegmentsAdapter.adapt(route.getSegments()));
		return modelAndView;
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RoutingException.class)
	public ModelAndView handleException(RoutingException exception) {
		log.warn(exception.getMessage());
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		ModelAndView modelAndView = new ModelAndView(view, "error",
				new RoutingErrorDtoImpl(exception.getMessage()));
		return modelAndView;
	}

	public IRoutingService<T> getRouteService() {
		return routeService;
	}

	public void setRouteService(IRoutingService<T> routeService) {
		this.routeService = routeService;
	}

	public IRoutingOptionsFactory getRoutingOptionsFactory() {
		return routingOptionsFactory;
	}

	public void setRoutingOptionsFactory(
			IRoutingOptionsFactory routingOptionsFactory) {
		this.routingOptionsFactory = routingOptionsFactory;
	}

	public IAdapter<RouteDtoImpl<T>, IRoute<T>> getRouteAdapter() {
		return routeAdapter;
	}

	public void setRouteAdapter(IAdapter<RouteDtoImpl<T>, IRoute<T>> routeAdapter) {
		this.routeAdapter = routeAdapter;
	}

	public IAdapter<List<RouteSegmentDtoImpl>, List<T>> getRouteSegmentsAdapter() {
		return routeSegmentsAdapter;
	}

	public void setRouteSegmentsAdapter(IAdapter<List<RouteSegmentDtoImpl>, List<T>> routeSegmentsAdapter) {
		this.routeSegmentsAdapter = routeSegmentsAdapter;
	}

}