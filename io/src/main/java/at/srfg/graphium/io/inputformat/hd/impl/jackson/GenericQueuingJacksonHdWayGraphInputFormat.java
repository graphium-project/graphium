/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.io.inputformat.hd.impl.jackson;

import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.inputformat.IQueuingGraphInputFormat;
import at.srfg.graphium.io.inputformat.impl.jackson.GenericQueuingJacksonGraphInputFormat;
import at.srfg.graphium.model.hd.IHDWaySegment;

/**
 * @author mwimmer
 *
 */
public class GenericQueuingJacksonHdWayGraphInputFormat<T extends IHDWaySegment>
        extends GenericQueuingJacksonGraphInputFormat<T> implements IQueuingGraphInputFormat<T> {

    public GenericQueuingJacksonHdWayGraphInputFormat(ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry) {
        super(adapterRegistry);
    }

}
