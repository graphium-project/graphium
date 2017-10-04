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
package at.srfg.graphium.io.inputformat.impl.jackson;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.adapter.impl.AbstractSegmentDTOAdapter;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.exception.XInfoDeserializationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializer for connection XInfos cannot be used with anntoations but has to be set in an module and registered
 * to the objectmapper. Can be set during runtime.
 *
 * Created by shennebe on 10.10.2016.
 */
public class ConnectionXInfoDeserializer extends JsonDeserializer<IConnectionXInfoDTO> {

    private AbstractSegmentDTOAdapter segmentAdapter;

    public ConnectionXInfoDeserializer( ISegmentAdapter segmentAdapter) {
        if (segmentAdapter instanceof AbstractSegmentDTOAdapter) {
            this.segmentAdapter = (AbstractSegmentDTOAdapter) segmentAdapter;
        }
    }

    @Override
    public Class<IConnectionXInfoDTO> handledType() {
        return IConnectionXInfoDTO.class;
    }

    @Override
    public IConnectionXInfoDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String type = (String) ctxt.getAttribute("xinfo_key");
        try {
            return (IConnectionXInfoDTO) jp.readValueAs(this.segmentAdapter.getConnectionXInfoAdapterRegistry().getObjectForType(type).getDtoClass());
        } catch (XInfoNotSupportedException e) {
            throw new XInfoDeserializationException("XInfo is not supported",e);
        }
    }
}
