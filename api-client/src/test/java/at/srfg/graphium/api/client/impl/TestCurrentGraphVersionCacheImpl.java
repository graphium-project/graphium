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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.client.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.srfg.graphium.api.client.ICurrentGraphVersionCache;
import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-api-client-template.xml",
		"classpath:/application-context-graphium-api-client-test.xml"})
public class TestCurrentGraphVersionCacheImpl {

	private static Logger log = LoggerFactory.getLogger(TestCurrentGraphVersionCacheImpl.class);
	
	@Autowired
	public ICurrentGraphVersionCache currentGraphVersionCache;

	private String graphName = "osm_at";

    @Test
    public void test(){
        //TODO use mockMVC and fix tests
    }

	//TODO use mockMVC and fix tests
	//@Test
	public void testGetCurrentGraphVersion() {
		IWayGraphVersionMetadata metadata = null;
		try {
			metadata = currentGraphVersionCache.getCurrentGraphVersion(graphName);
		} catch (IllegalArgumentException | GraphiumServerAccessException
				| GraphNotFoundException e) {
			log.error("error accessing graphium api...", e);
		}
		
		Assert.assertNotNull(metadata);
	}
	
}