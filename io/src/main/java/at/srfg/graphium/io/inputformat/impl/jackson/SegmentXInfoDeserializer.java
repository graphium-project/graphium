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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.adapter.impl.AbstractSegmentDTOAdapter;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.exception.XInfoDeserializationException;

/**
 *
 * Deserializer for segment XInfos cannot be used with anntoations but has to be set in an module and registered
 * to the objectmapper. Can be set during runtime.
 *
 * Created by shennebe on 10.10.2016.
 */
public class SegmentXInfoDeserializer extends JsonDeserializer<ISegmentXInfoDTO> {

    private AbstractSegmentDTOAdapter segmentAdapter;

    public SegmentXInfoDeserializer( ISegmentAdapter segmentAdapter) {
        if (segmentAdapter instanceof AbstractSegmentDTOAdapter) {
            this.segmentAdapter = (AbstractSegmentDTOAdapter) segmentAdapter;
        }
    }

    @Override
    public Class<ISegmentXInfoDTO> handledType() {
        return ISegmentXInfoDTO.class;
    }

    @Override
    public ISegmentXInfoDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        //String type = jp.getCurrentName();
        String type = (String) ctxt.getAttribute("xinfo_key");
        try {
            return (ISegmentXInfoDTO) jp.readValueAs(this.segmentAdapter.getSegmentXInfoAdapterRegistry().getObjectForType(type).getDtoClass());
        } catch (XInfoNotSupportedException e) {
            throw new XInfoDeserializationException("XInfo is not supported",e);
        }
    }
}
