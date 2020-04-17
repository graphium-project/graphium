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
import at.srfg.graphium.routing.exception.UnkownRoutingAlgoException;
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
	@RequestMapping(value = "/routing/graphs/{graphName}/route.do", method=RequestMethod.GET)
	public ModelAndView route(
			@PathVariable(value = "graphName") String graphName,
			@RequestParam(value = "coords") String coords,
			@RequestParam(value = "output", defaultValue = "overview") String output,
			@RequestParam MultiValueMap<String,String> allRequestParams
			) throws RoutingException, RoutingParameterException, UnkownRoutingAlgoException {
		return doRoute(graphName, null, coords, output, allRequestParams);
	}
	
	@RequestMapping(value = "/routing/graphs/{graphName}/versions/{graphVersion}/route.do", method=RequestMethod.GET)
	public ModelAndView routeForVersion(
			@PathVariable(value = "graphName") String graphName,
			@PathVariable(value = "graphVersion") String graphVersion,
			@RequestParam(value = "coords") String coords,
			@RequestParam(value = "output") String output,
			@RequestParam MultiValueMap<String,String> allRequestParams) throws RoutingException, RoutingParameterException, UnkownRoutingAlgoException  {
		return doRoute(graphName, graphVersion, coords, output, allRequestParams);
	}
	

	protected ModelAndView doRoute(String graphName, String graphVersion, String coordString, 
			String output, MultiValueMap<String, String> allRequestParams) throws RoutingParameterException, UnkownRoutingAlgoException, RoutingException {
		IRouteOutputAdapter<IRouteDTO<W>, W, T> adapter = adapterRegistry.getAdapter(output);
		if(adapter == null) {
			throw new RoutingParameterException("no output format " + output + " available");
		}
		MappingJackson2JsonView view = new MappingJackson2JsonView();		
		O routingOptions = routingOptionsFactory.newRoutingOptions(graphName, graphVersion, coordString, allRequestParams, null);
		IRoute<T, W> route = routeService.route(routingOptions);
		
		IRouteDTO<W> routeDto = adapter.adapt(route);
		ModelAndView modelAndView = new ModelAndView(view, "route", routeDto);
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

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UnkownRoutingAlgoException.class)
	public ModelAndView handleException(UnkownRoutingAlgoException exception) {
		log.warn(exception.getMessage());
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		ModelAndView modelAndView = new ModelAndView(view, "error",
				new RoutingErrorDtoImpl(exception.getMessage()));
		return modelAndView;
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RoutingParameterException.class)
	public ModelAndView handleException(RoutingParameterException exception) {
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