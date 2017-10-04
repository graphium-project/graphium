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
package at.srfg.graphium.postgis.persistence.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-postgis_test.xml",
		"classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis-datasource.xml",
		"classpath:application-context-graphium-postgis-aliasing.xml"})
public class TestSourceDaoImpl {

	@Autowired
	private ISourceDao dao;
	
	@Test
	@Rollback(value=true)
	public void testStoreAndReadSource() {
		ISource source = new Source(1, "neue Source");
		
		dao.save(source);
		
		ISource savedSource = dao.getSource(1);
		
		Assert.notNull(savedSource);

		System.out.println(source);
		
	}
	
}
