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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context-graphium-routing-api-test.xml"})
@WebAppConfiguration
public class TestWaySegmentRoutingApiController {

    private static Logger log = LoggerFactory.getLogger(SpringJUnit4ClassRunner.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;



    @Before
    public void setup() {
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void test() throws Exception {
//    	System.out.println("Hello");
//        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(get("/routing/graphs/osm_at/route.do?coords=47.00,11.00;47.04,11.00")).andExpect(status().isOk())
        		//.andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();
        String content = result.getResponse().getContentAsString();
////        String[] parsedResult = mapper.readValue(content,String[].class);
        log.info(content);
//        Set<String> graphNames = this.graphVersions.keySet();
//        Assert.assertEquals(parsedResult.length,graphNames.size());
//        for (String resultElement : parsedResult) {
//            Assert.assertTrue(graphNames.contains(resultElement));
//        }
    }
}
