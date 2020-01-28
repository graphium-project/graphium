package at.srfg.graphium.routing.api.controller.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapter;
import at.srfg.graphium.routing.api.adapter.IRouteOutputAdapterRegistry;
import at.srfg.graphium.routing.api.dto.IRouteDTO;
import at.srfg.graphium.routing.api.dto.impl.RoutingErrorDtoImpl;
import at.srfg.graphium.routing.exception.RoutingException;
import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.IRoutingOptionsFactory;
import at.srfg.graphium.routing.service.IRoutingService;

public abstract class GenericRoutingApiController<T extends IBaseWaySegment, O extends IRoutingOptions, W extends Object> {

	private static Logger log = LoggerFactory
			.getLogger(GenericRoutingApiController.class);

	private IRoutingOptionsFactory<O> routingOptionsFactory;
	private IRoutingService<T, W, O> routeService;
	private IRouteOutputAdapterRegistry<IRouteDTO<W>, W, T> adapterRegistry;
	
	@PostConstruct
	public void setup() {
		log.info(this.getClass().getSimpleName() + " set up");
	}

	// coordinates --> Array mit x/y Pairs
		// output --> overview, default / path?, details
		// avoid --> bbox? 
		// insgesamt, routing GET mit weniger Optionen, routing POST mit mehr?				
	@RequestMapping(value = "/graphs/{graphName}/routing/route.do", method=RequestMethod.GET)
	public ModelAndView route(
			@PathVariable(value = "graphName") String graphName,
			@RequestParam(value = "coords") String coords,
			@RequestParam(value = "output", defaultValue = "overview") String output,
			@RequestParam MultiValueMap<String,String> allRequestParams
//			@RequestParam(value = "timestamp", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date routingTimestamp,
//			@RequestParam(value = "startX", required = true) double startX,
//			@RequestParam(value = "startY", required = true) double startY,
//			@RequestParam(value = "endX", required = true) double endX,
//			@RequestParam(value = "endY", required = true) double endY,
//			@RequestParam(value = "cutsegments", required = false, defaultValue = "true") boolean cutStartAndEndSegment,
//			@RequestParam(value = "routingMode", required = false, defaultValue = "CAR") String routingMode,
//			@RequestParam(value = "routingCriteria", required = false, defaultValue = "LENGTH") String routingCriteria,
//			@RequestParam(value = "routingAlgorithm", required = false, defaultValue = "DIJKSTRA") String routingAlgorithm
			) throws RoutingException, RoutingParameterException {
		return doRoute(graphName, null, coords, output, allRequestParams);
		
		/*log.info("got routing request in graph " + graphName + " from "
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

		return modelAndView;*/
	}
	
	@RequestMapping(value = "/graphs/{graphName}/versions/{graphVersion}/routing/route.do", method=RequestMethod.GET)
	public ModelAndView routeForVersion(
			@PathVariable(value = "graphName") String graphName,
			@PathVariable(value = "graphVersion") String graphVersion,
			@RequestParam(value = "coords") String coords,
			@RequestParam(value = "output") String output,
			@RequestParam MultiValueMap<String,String> allRequestParams) throws RoutingException, RoutingParameterException  {
		return doRoute(graphName, graphVersion, coords, output, allRequestParams);
	}
	

	protected ModelAndView doRoute(String graphName, String graphVersion, String coordString, 
			String output, MultiValueMap<String, String> allRequestParams) throws RoutingParameterException {
		IRouteOutputAdapter<IRouteDTO<W>, W, T> adapter = adapterRegistry.getAdapter(output);
		if(adapter == null) {
			throw new RoutingParameterException("no output format " + output + " available");
		}
		MappingJackson2JsonView view = new MappingJackson2JsonView();		
		O routingOptions = routingOptionsFactory.newRoutingOptions(graphName, graphVersion, coordString, allRequestParams);
		IRoute<T, W> route = routeService.route(routingOptions);
		
		IRouteDTO<W> routeDto = adapter.adapt(route);
		ModelAndView modelAndView = new ModelAndView(view, "route", routeDto);
		return modelAndView;
	}
	
	// coordinates --> Array mit x/y Pairs
	// output --> overview, default / path?, details
	// avoid --> bbox? 
	// insgesamt, routing GET mit weniger Optionen, routing POST mit mehr?
/*	@RequestMapping(value = "/graphs/{graphName}/versions/{graphVersion}/routing/route.do", method=RequestMethod.GET)
	public ModelAndView routeForVersion(
			@PathVariable(value = "graphName") String graphName,
			@PathVariable(value = "graphVersion") String graphVersion,
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
	*/

	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RoutingException.class)
	// TODO: handle other exceptions
	public ModelAndView handleException(RoutingException exception) {
		log.warn(exception.getMessage());
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		ModelAndView modelAndView = new ModelAndView(view, "error",
				new RoutingErrorDtoImpl(exception.getMessage()));
		return modelAndView;
	}

	public IRoutingService<T, W, O> getRouteService() {
		return routeService;
	}

	public void setRouteService(IRoutingService<T, W, O> routeService) {
		this.routeService = routeService;
	}

	public IRoutingOptionsFactory<O> getRoutingOptionsFactory() {
		return routingOptionsFactory;
	}

	public void setRoutingOptionsFactory(
			IRoutingOptionsFactory<O> routingOptionsFactory) {
		this.routingOptionsFactory = routingOptionsFactory;
	}

	public IRouteOutputAdapterRegistry<IRouteDTO<W>, W, T> getAdapterRegistry() {
		return adapterRegistry;
	}

	public void setAdapterRegistry(IRouteOutputAdapterRegistry<IRouteDTO<W>, W, T> adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
	}


}