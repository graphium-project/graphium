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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.impl.GraphVersionMetadataServiceImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context-graphium-central-api-test.xml"})
@WebAppConfiguration
public class TestGraphApiController {

	private static Logger log = LoggerFactory.getLogger(TestGraphApiController.class);

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Resource(name="graphVersionMetadataService")
	private GraphVersionMetadataServiceImpl metaDataService;

	private Map<String,List<String>> graphVersions;

	@Before
	public void setup() throws GraphNotExistsException, GraphAlreadyExistException {
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
		for (String graphName : this.graphVersions.keySet()) {
			MvcResult result = mockMvc.perform(get("/metadata/graphs/"))
					.andReturn();
			log.info("Status graphs request: " + result.getResponse().getStatus());
			result = mockMvc.perform(get("/segments/graphs/" + graphName + "/versions/current"))
					.andReturn();
			log.info("Status current graph's version request: " + result.getResponse().getStatus());
		}
	}

	@Test
	public void testGetGraphSegments() throws Exception {
		for (String graphName : this.graphVersions.keySet()) {
			for (String version : this.graphVersions.get(graphName)) {
				MvcResult result = mockMvc.perform(get("/metadata/graphs/"))
						.andReturn();
				log.info("Status graphs request: " + result.getResponse().getStatus());
				result = mockMvc.perform(get("/segments/graphs/"+ graphName + "/versions/" + version))
						.andReturn();
				log.info("Status current graph's version request: " + result.getResponse().getStatus());
			}
		}
	}

}