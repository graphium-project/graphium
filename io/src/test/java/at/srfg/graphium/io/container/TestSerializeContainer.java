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
package at.srfg.graphium.io.container;

import at.srfg.graphium.io.dto.impl.DefaultSegmentXInfoDTO;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by shennebe on 21.09.2016.
 */
public class TestSerializeContainer {

    @Test
    public void testSerializeContainerDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //SegmentXInfoDTOContainer dtoContainer = new SegmentXInfoDTOContainer();
        //container.setType("segmentXInfo");
        List<DefaultSegmentXInfoDTO> list = new ArrayList<>();
        DefaultSegmentXInfoDTO dto = new DefaultSegmentXInfoDTO();
        //dto.setName("HalloHallo");
        dto.setDirectionTow(true);
        dto.setValue("testLong",10l);
        dto.setValue("testString","I am a String");
        dto.setValue("testBoolean",true);
        Map<String,String> testObj = new HashMap<>();
        testObj.put("testkey","testValue");
        dto.getValues().put("testObjectMap",testObj);
        list.add(dto);
        Map<String,List<DefaultSegmentXInfoDTO>> map = new HashMap<>();
        map.put(DefaultSegmentXInfoDTO.class.getSimpleName(),list);
        Set<DefaultSegmentXInfoDTO> set = new HashSet<>();
        set.add(dto);
        //dtoContainer.setSegmentXInfoDTOs(set);
        System.out.println(mapper.writeValueAsString(set));
    }

    @Test
    public void testSerialize() throws IOException {
        String test = "[{\"name\":\"HalloHallo\",\"segmentId\":12345}]";
        InputStream stream = new ByteArrayInputStream( test.getBytes() );
        JsonFactory factory = new MappingJsonFactory();
        JsonParser parser = factory.createParser(stream);
        JsonToken token = parser.nextToken();
        if (token == JsonToken.START_ARRAY) {
            do {
                parser.nextToken();
                DefaultSegmentXInfoDTO segmentXInfoDTO = parser.readValueAs(DefaultSegmentXInfoDTO.class);
            } while (token == JsonToken.END_ARRAY);
        }
    }

}
