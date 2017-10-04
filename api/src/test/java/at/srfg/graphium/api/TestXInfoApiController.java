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
package at.srfg.graphium.api;

import at.srfg.graphium.core.service.impl.GraphVersionMetadataServiceImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by shennebe on 21.09.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context-graphium-central-api-test.xml"})
@WebAppConfiguration
public class TestXInfoApiController {

    private static Logger log = LoggerFactory.getLogger(TestXInfoApiController.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private Map<String,List<String>> graphVersions;

    @Resource(name="graphVersionMetadataService")
    private GraphVersionMetadataServiceImpl metaDataService;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.graphVersions = getGraphVersions();
    }

    private Map<String,List<String>> getGraphVersions() {
        Map<String,List<String>> resultMap = new HashMap<>();
        List<String> graphs = metaDataService.getGraphs();
        for (String graphName : graphs) {
            List<IWayGraphVersionMetadata> versionMetadatas = metaDataService.getWayGraphVersionMetadataList(graphName);
            List<String> versions = new ArrayList<>();
            for (IWayGraphVersionMetadata wayGraphVersionMetadata : versionMetadatas) {
                versions.add(wayGraphVersionMetadata.getVersion());
            }
            resultMap.put(graphName,versions);
        }
        return resultMap;
    }

    @Test
    public void testUpdateXInfo() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            mockMvc.perform(post("/segments/graphs/" + graphName + "/versions/current/xinfos/default")
                    .content("[{\"segmentId\":12345,\"directionTow\":true,\"values\":{\"testBoolean\":true,\"testString\":\"I am a String\",\"testObjectMap\":{\"testkey\":\"testValue\"},\"testLong\":10}}]")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void testGetXInfo() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            mockMvc.perform(get("/segments/graphs/" + graphName + "/versions/current/xinfos/default")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}
