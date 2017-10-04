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
package at.srfg.graphium.core.adapter.registry;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.adapter.registry.impl.ConnectionXInfoAdapterRegistry;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.model.IConnectionXInfo;

/**
 * Connection XInfo Registry for potential adapters. All adapters that fulfill the contract to change a connection into
 * a connection DTO and vice versa are handled here. Spring injects these adapters by interpreting also the generics.
 * This works only in Spring 4.X and higher.
 *
 * Created by shennebe on 23.09.2016.
 */
public class AutodetectingConnectionXInfoAdapterRegistryImpl<T extends IConnectionXInfo, S extends IConnectionXInfoDTO>
        extends ConnectionXInfoAdapterRegistry<T,S> {

    @Autowired(required = false)
    private List<IXInfoDTOAdapter<T, S>> adapters;

    public AutodetectingConnectionXInfoAdapterRegistryImpl() { }
    
    @PostConstruct
    public void init() {
        super.setAdapters(adapters);
    }   

}
