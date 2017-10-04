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
package at.srfg.graphium.io.adapter.registry.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.NoSegmentAdapterFoundException;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.model.IBaseSegment;

public class SegmentAdapterRegistryImpl<O extends IBaseSegmentDTO, I extends IBaseSegment> 
	implements ISegmentAdapterRegistry<O, I> {

	private static Logger log = LoggerFactory.getLogger(SegmentAdapterRegistryImpl.class);
	
	private Map<Class<? extends IBaseSegmentDTO>, ISegmentAdapter<O, I>> dtoToAdapter = new HashMap<>();
	private Map<Class<? extends IBaseSegment>, ISegmentAdapter<O, I>> modelToAdapter = new HashMap<>();
	private Map<String, ISegmentAdapter<O, I>> typeToAdapter = new HashMap<>();
	
	public SegmentAdapterRegistryImpl() {}
	
	public SegmentAdapterRegistryImpl(List<ISegmentAdapter<O, I>> adapters) {
		this.setAdapters(adapters);
	}
	
	@Override
	public ISegmentAdapter<O, I> getAdapterForModal(Class<I> modelClass) throws NoSegmentAdapterFoundException{
		ISegmentAdapter<O, I> adapter = modelToAdapter.get(modelClass);		
		return returnOrThrowExecption(adapter, modelClass);
	}

	@Override
	public ISegmentAdapter<O, I> getAdapterForDto(Class<O> dtoClass) throws NoSegmentAdapterFoundException {
		ISegmentAdapter<O, I> adapter = dtoToAdapter.get(dtoClass);		
		return returnOrThrowExecption(adapter, dtoClass);
	}
	
	@Override
	public void setAdapters(List<ISegmentAdapter<O, I>> adapters) {
		if(adapters != null && !adapters.isEmpty()) {
			dtoToAdapter.clear();
			modelToAdapter.clear();
			for(ISegmentAdapter<O,I> adapter : adapters) {
				dtoToAdapter.put(adapter.getDtoClass(),adapter);
				modelToAdapter.put(adapter.getModelClass(),adapter);
				try {
					typeToAdapter.put(adapter.getDtoClass().newInstance().getSegmentType(), adapter);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("error initialising dto class with generics", e);
				}
			}
		}
	}

	@Override
	public ISegmentAdapter<O, I> getAdapterForType(String type)
			throws NoSegmentAdapterFoundException {
		ISegmentAdapter<O, I> adapter = typeToAdapter.get(type);		
		if(adapter == null) {
			throw new NoSegmentAdapterFoundException("no adapter found for type " + type);
		}
		return adapter;
	}
	
	@Override
	public String getSegmentDtoType(Class<I> modelClass) {
		ISegmentAdapter<O, I> adapter = modelToAdapter.get(modelClass);				
		try {
			return adapter.getDtoClass().newInstance().getSegmentType();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("error initialising dto class with generics", e);
		}
		return null;
	}
	
	private ISegmentAdapter<O, I> returnOrThrowExecption(
			ISegmentAdapter<O, I> adapter, Class typeClass)  throws NoSegmentAdapterFoundException {
		if(adapter == null) {
			throw new NoSegmentAdapterFoundException("no adapter found for class " + typeClass.getCanonicalName());
		}
		return adapter;
	}

}
