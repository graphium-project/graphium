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

import at.srfg.graphium.ITestGraphiumPostgis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */

public class SubtestSourceDaoImpl implements ITestGraphiumPostgis {

	@Autowired
	private ISourceDao dao;
	@Value("${db.sourceId}")
	int sourceID;
	@Value("${db.sourceName}")
	String sourceName;
	private static Logger log = LoggerFactory.getLogger(SubtestSourceDaoImpl.class);

	@Override
	public void run(){
		//call all test-methods
		testStoreAndReadSource();
	}

	public void testStoreAndReadSource() {
		ISource source = new Source(sourceID, sourceName);
		dao.save(source);
		ISource savedSource = dao.getSource(sourceID);
		Assert.notNull(savedSource);
		log.info(source.toString());
	}
}
