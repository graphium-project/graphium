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

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.adapter.registry.IXinfoAdapterRegistry;
import at.srfg.graphium.io.dto.IXInfoDTO;
import at.srfg.graphium.model.IXInfo;

/**
 * Abstract Registry for Connection and Segment XInfo. This has been created to prevent duplicate code. The Autowired
 * Property for the adapters is needed in the concrete Subclass because otherwise Spring ist not able to inject
 * DTOs according to generics. This is also a Spring 4.X feature so this is strictly required for this registry.
 *
 * Created by shennebe on 23.09.2016.
 */
public class GenericXInfoAdapterRegistry<T extends IXInfo,S extends IXInfoDTO> implements IXinfoAdapterRegistry<T, S> {

    private Map<String,IXInfoDTOAdapter<T,S>> adapterMap = new HashMap<>();

    public GenericXInfoAdapterRegistry() { super(); }

    public GenericXInfoAdapterRegistry(List<IXInfoDTOAdapter<T,S>> adapters) {
    	super();
        this.setAdapters(adapters);
    }

    /**
     * Function for initialising all adapters fot this registry type
     * @param adapters the adapters to be considered
     */
    @Override
    public void setAdapters(List<IXInfoDTOAdapter<T,S>> adapters) {
        if (adapters != null) {
            for (IXInfoDTOAdapter<T,S> adapter : adapters) {
                if (this.adapterMap.get(adapter.getResponsibleType()) != null) {
                    throw new RuntimeException("Adapter of type " + adapter.getResponsibleType() + " already registered");
                } else {
                    this.adapterMap.put(adapter.getResponsibleType(),adapter);
                }
            }
        }
    }

    /**
     * Returns an adapter for a specific type. The Type is read from the Model Class.
     * @param type the type the for the adapter.
     *
     * @return An adapter object
     */
    @Override
    public IXInfoDTOAdapter<T,S> getObjectForType(String type) throws XInfoNotSupportedException {
    	IXInfoDTOAdapter<T,S> adapter = this.adapterMap.get(type);
        if (adapter == null) {
            throw new XInfoNotSupportedException("No adapter found for XInfo " + type);
        }
        return adapter;
    }
}
