/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.srfg.graphium.api.events.IEvent;
import at.srfg.graphium.api.events.impl.EventImpl;
import at.srfg.graphium.api.events.listener.IEventListener;
import at.srfg.graphium.api.exceptions.ResourceNotFoundException;

@Controller
public class EventApiController {

	protected static Logger log = LoggerFactory.getLogger(EventApiController.class);
		
	private Map<String, Set<IEventListener>> listeners = new HashMap<String, Set<IEventListener>>();
			
	@RequestMapping(value = "/events/notify/{eventname}", method = RequestMethod.PUT, consumes="application/json")	
	@ResponseBody
	public void notify(
			@PathVariable("eventname") String eventName,		
			@RequestBody Map<String, Object> params) throws ResourceNotFoundException
	{		
		String source = null;
		if(params.containsKey(IEventListener.EVENTSOURCEPARAMNAME)) {
			source =  params.get(IEventListener.EVENTSOURCEPARAMNAME).toString();
		}
		IEvent event = new  EventImpl(eventName, source, params);
		
		if(listeners != null && listeners.containsKey(eventName)) {
			Set<IEventListener> eventListeners = listeners.get(eventName);
			if(eventListeners != null && !eventListeners.isEmpty()) {
				for(IEventListener listener : eventListeners) {
					listener.notify(event);
				}
			}
			else {
				String msg = "event listeners for event " + eventName + " null or empty!";
				log.warn(msg);
				throw new ResourceNotFoundException(msg);
			}
		}
		else {
			String msg = "no event listener for event: " + eventName + " registered";
			log.warn(msg);
			throw new ResourceNotFoundException(msg);
		}
	}
	
	public Map<String, Set<IEventListener>> getListeners() {
		return listeners;
	}

	public void setListeners(Map<String, Set<IEventListener>> listeners) {
		this.listeners = listeners;
	}	
	
}
