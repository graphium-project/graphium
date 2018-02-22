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
package at.srfg.graphium.postgis.persistence.resultsetextractors.impl;

import java.util.Set;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.postgis.persistence.ISegmentResultSetExtractor;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapperFactory;
import at.srfg.graphium.postgis.persistence.resultsetextractors.ISegmentResultSetExtractorFactory;

/**
 * Factory identifies all ResultSetExtractors and RowMappers by a set of table alias names.
 * A ResultSetExtractor is the root mapping object which maps segment tables and can have several
 * RowMappers to map extended information tables.
 */
public abstract class SegmentResultSetExtractorFactoryImpl<T extends IBaseSegment, X extends ISegmentXInfo> implements ISegmentResultSetExtractorFactory {

	private ISegmentXInfoRowMapperFactory xInfoRowMapperFactory = null;
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBaseSegment, X extends ISegmentXInfo> ISegmentResultSetExtractor<T, X> getResultSetExtractor(Set<String> tableAliases) {
		ISegmentResultSetExtractor<T, X> res = createResultSetExtractor();
		
		// mw 170828: not necessary, already done in WaySegmentResultSetExtractor with @Autowired: finde RowMapper und setze in res ein...
//		if (xInfoRowMapperFactory != null && xInfoRowMapperFactory.getAll() != null) {
//			for (String tableAlias : tableAliases) {
//				for (ISegmentXInfoRowMapper rowMapper : xInfoRowMapperFactory.getAll()) {
//					if (rowMapper.fitsPrefix(tableAlias)) {
//						res.addRowMapper(rowMapper);
//					}
//				}
//			}			
//		}
		
		return res;
	}

	public ISegmentXInfoRowMapperFactory getxInfoRowMapperFactory() {
		return xInfoRowMapperFactory;
	}

	public void setxInfoRowMapperFactory(ISegmentXInfoRowMapperFactory xInfoRowMapperFactory) {
		this.xInfoRowMapperFactory = xInfoRowMapperFactory;
	}

	protected abstract ISegmentResultSetExtractor createResultSetExtractor();

}