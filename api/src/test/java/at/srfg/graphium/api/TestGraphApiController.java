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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.*;
import javax.annotation.Resource;

import at.srfg.graphium.api.springconfig.MockBeansConfig;
import at.srfg.graphium.io.dto.impl.GraphVersionMetadataDTOImpl;
import at.srfg.graphium.model.State;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import at.srfg.graphium.core.service.impl.GraphVersionMetadataServiceImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:application-context-graphium-api-test.xml"})
public class TestGraphApiController {
	private static Logger log = LoggerFactory.getLogger(TestGraphApiController.class);

	@Autowired
	private WebApplicationContext wac;

	@Resource(name="graphVersionMetadataService")
	private GraphVersionMetadataServiceImpl metaDataService;

	private MockMvc mockMvc;
	private Map<String,List<String>> graphVersions;

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
	public void testGetGraph() throws Exception {
		List<String> results = new ArrayList<String>();
		for (String graphName : this.graphVersions.keySet()) {
			 results.add(mockMvc.perform(get("/segments/graphs/" + graphName + "/versions/current"))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString().trim());
		}
        //create expected object
        List<GraphVersionMetadataDTOImpl> expectedResults = new ArrayList<GraphVersionMetadataDTOImpl>();
        Set<String> accessTypes = new HashSet<String>();
        accessTypes.add("PRIVATE_CAR");
        String source = "neue Testsource";
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName(), "3.0", MockBeansConfig.getGraphName(), "3.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName2(), "3.0", MockBeansConfig.getGraphName2(), "3.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));

        testOutput(results, expectedResults);
	}

	@Test
	public void testGetGraphSegments() throws Exception {
		List<String> results = new ArrayList<String>();
		for (String graphName : this.graphVersions.keySet()) {
			for (String version : this.graphVersions.get(graphName)) {
				results.add(mockMvc.perform(get("/segments/graphs/"+ graphName + "/versions/" + version))
						.andExpect(status().isOk()).andReturn().getResponse().getContentAsString().trim());
			}
		}
        //create expected object
        List<GraphVersionMetadataDTOImpl> expectedResults = new ArrayList<GraphVersionMetadataDTOImpl>();
        Set<String> accessTypes = new HashSet<String>();
        accessTypes.add("PRIVATE_CAR");
        String source = "neue Testsource";
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName(), "1.0", MockBeansConfig.getGraphName(), "1.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName(), "2.0", MockBeansConfig.getGraphName(), "2.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName(), "3.0", MockBeansConfig.getGraphName(), "3.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName2(), "1.0", MockBeansConfig.getGraphName2(), "1.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName2(), "2.0", MockBeansConfig.getGraphName2(), "2.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));
        expectedResults.add(new GraphVersionMetadataDTOImpl(0, MockBeansConfig.getGraphName2(), "3.0", MockBeansConfig.getGraphName2(), "3.0_orig", State.INITIAL.toString(), MockBeansConfig.getDf().parse("2017-03-01").getTime(), MockBeansConfig.getDf().parse("2017-03-31").getTime(), MockBeansConfig.getBoundsAustria().toString(), 1000, 2000, accessTypes, MockBeansConfig.getTags(), source,
                "Graph fuer Tests", "keine Beschreibung...", MockBeansConfig.getNow().getTime(), MockBeansConfig.getNow().getTime(), "ich", "http://0815.echt.org"));

        testOutput(results, expectedResults);
	}

	private void testOutput(List<String> results, List<GraphVersionMetadataDTOImpl> expectedResults){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			//deserialize metadataDTO from result-json
			//substring: from second occurrence of { to length - 1
			List<GraphVersionMetadataDTOImpl> dtoListResults = new ArrayList<GraphVersionMetadataDTOImpl>();
			for(int i = 0; i < results.size(); i++){
				String metadata = results.get(i).substring(results.get(i).indexOf("{", results.get(i).indexOf("{") + 1), results.get(i).length()-1);
				dtoListResults.add(objectMapper.readValue(metadata, GraphVersionMetadataDTOImpl.class));
			}

			for(int i = 0; i < dtoListResults.size(); i++){
				Assert.assertTrue("Expected GraphVersionMetadataDTOImpl is different to the result! " + dtoListResults.get(i).toString() + " / " + expectedResults.get(i).toString(),dtoListResults.get(i).equals(expectedResults.get(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}