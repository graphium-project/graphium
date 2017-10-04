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
package at.srfg.graphium.io.adapter.impl;

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.dto.IXInfoDTO;
import at.srfg.graphium.model.IXInfo;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;

/**
 * Abstract Adapter for Converting XInfo into a DTO and vice versa. Additionally it has a function to read the
 * class of the DTO. This is e.g. needed for deserializing JSON with the object mapper
 *
 * Created by shennebe on 22.09.2016.
 */
public abstract class AbstractXInfoDTOAdapter<I extends IXInfo,O extends IXInfoDTO>
        extends AbstractXInfoModelTypeAware<I> implements IXInfoDTOAdapter<I,O> {

    private Class<O> xInfoDTOClass;

    public AbstractXInfoDTOAdapter(I xInfo, O xInfoDto) {
        super(xInfo);
        this.xInfoDTOClass = (Class<O>) xInfoDto.getClass();
    }

    /**
     * Returns the concrete DTO Class this adapter accepts. This info is read from the generics information. All Adapters
     * for Segments and Connections in Plugins should inherit from this class.
     *
     * @return The class of the concrete DTO Object
     */
    @Override
    public Class<O> getDtoClass()  {
        return xInfoDTOClass;
    }

}
