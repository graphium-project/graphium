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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import at.srfg.graphium.core.service.impl.GraphVersionMetadataServiceImpl;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by shennebe on 30.08.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:application-context-graphium-central-api-test.xml"})
@ContextConfiguration({"classpath:application-context-graphium-api-test.xml"})
@WebAppConfiguration
public class TestGraphVersionMetaApiController {

    private static Logger log = LoggerFactory.getLogger(SpringJUnit4ClassRunner.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Resource(name="graphVersionMetadataService")
    private GraphVersionMetadataServiceImpl metaDataService;

    private Map<String,List<String>> graphVersions;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
         this.graphVersions = this.getGraphVersions();
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
    public void test(){
        //TODO replace services with mocks and fix tests
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testReadGraphs() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(get("/metadata/graphs")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        String[] parsedResult = mapper.readValue(content,String[].class);
        Set<String> graphNames = this.graphVersions.keySet();
        log.info(content);
        Assert.assertEquals(parsedResult.length,graphNames.size());
        for (String resultElement : parsedResult) {
            Assert.assertTrue(graphNames.contains(resultElement));
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testReadGraphVersions() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            MvcResult graphVersionResult = mockMvc.perform(get("/metadata/graphs/" + graphName + "/versions")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                    .andReturn();
            String graphVersionResultContent = graphVersionResult.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            GraphVersionMetadataDTOImpl[] versionResult = mapper.readValue(graphVersionResultContent,GraphVersionMetadataDTOImpl[].class);
            log.info(graphVersionResultContent);
            Assert.assertEquals(this.graphVersions.get(graphName).size(),versionResult.length);
            for (GraphVersionMetadataDTOImpl versionMetadataDTO : versionResult) {
                Assert.assertTrue(this.graphVersions.get(graphName).contains(versionMetadataDTO.getVersion()));
            }
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testReadGraphCurrentName() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            MvcResult graphVersionResult = mockMvc.perform(get("/metadata/graphs/" + graphName + "/versions/current/version")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                    .andReturn();
            String graphVersionResultContent = graphVersionResult.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            HashMap versionResult = mapper.readValue(graphVersionResultContent,HashMap.class);
            log.info(versionResult.toString());
            Assert.assertTrue(this.graphVersions.get(graphName).contains((String)versionResult.get("version")));
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testReadGraphCurrentId() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            MvcResult graphVersionResult = mockMvc.perform(get("/metadata/graphs/" + graphName + "/versions/current/id")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                    .andReturn();
            String graphIdResult = graphVersionResult.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            HashMap versionResult = mapper.readValue(graphIdResult, HashMap.class);
            Assert.assertTrue(versionResult.containsKey("id"));
            log.info(versionResult.toString());
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testReadVersion() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
                MvcResult graphVersionResult = mockMvc.perform(get("/metadata/graphs/" + graphName + "/versions/" + version)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                        .andReturn();
                String graphVersionResultAsString = graphVersionResult.getResponse().getContentAsString();
                ObjectMapper mapper = new ObjectMapper();
                GraphVersionMetadataDTOImpl versionResult = mapper.readValue(graphVersionResultAsString,GraphVersionMetadataDTOImpl.class);
                log.info(versionResult.toString());
                Assert.assertEquals(versionResult.getVersion(),version);
                Assert.assertEquals(versionResult.getGraphName(),graphName);
            }
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testCheckUpdate() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            List<String> versions = this.getGraphVersions().get(graphName);
            String version = versions.get(versions.size() - 1);
            MvcResult checkUpdate = mockMvc.perform(get("/metadata/graphs/" + graphName + "/checkupdate?lastImportedVersion=" + version)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
                    .andReturn();
            String updateAvailableAsString = checkUpdate.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            Map updateAvailable = mapper.readValue(updateAvailableAsString, HashMap.class);
            log.info(updateAvailable.toString());
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testGetState() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
                MvcResult stateResult = mockMvc.perform(get("/metadata/graphs/" + graphName + "/versions/" + version + "/state"))
                        .andExpect(status().isOk())
                        .andReturn();
                String stringState = stateResult.getResponse().getContentAsString();
                Assert.assertNotNull(stringState);
                log.info(stringState);
            }
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testChangeState() throws Exception {
        State newState = State.INITIAL;
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
               this.changeState(graphName,version,newState);
            }
        }
        State oldState = State.ACTIVE;
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
                this.changeState(graphName,version,oldState);
            }
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testChangeValidFrom() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
                MvcResult graphVersionResult = mockMvc.perform(put("/metadata/graphs/" + graphName + "/versions/" + version + "/validFrom/" +  "10")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();
                String result = graphVersionResult.getResponse().getContentAsString();
                log.info(result);
                ObjectMapper mapper = new ObjectMapper();
                Map resultMap = mapper.readValue(result,HashMap.class);
                Assert.assertEquals(resultMap.get("validFrom"),10);

                mockMvc.perform(put("/metadata/graphs/" + graphName + "/versions/" + version + "/validTo/" +  "0")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnprocessableEntity())
                        .andReturn();

                mockMvc.perform(delete("/metadata/graphs/" + graphName + "/versions/" + version + "/validTo/")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();
            }
        }
    }

    //TODO replace services with mocks and fix tests
    //@Test
    public void testChangeNotAllowedField() throws Exception {
        for (String graphName : this.graphVersions.keySet()) {
            for (String version : this.graphVersions.get(graphName)) {
                mockMvc.perform(put("/metadata/graphs/" + graphName + "/versions/" + version + "/segmentsCount/" +  "0")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andReturn();
            }
        }
    }

    private void changeState(String graphName, String version, State newState) throws Exception {
        MvcResult graphVersionResult = mockMvc.perform(put("/metadata/graphs/" + graphName + "/versions/" + version + "/state/" + newState.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String graphVersionResultAsString = graphVersionResult.getResponse().getContentAsString();
        log.info(graphVersionResultAsString);
    }
}
