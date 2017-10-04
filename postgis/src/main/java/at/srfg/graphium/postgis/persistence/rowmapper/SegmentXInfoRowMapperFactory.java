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
package at.srfg.graphium.postgis.persistence.rowmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapper;
import at.srfg.graphium.postgis.persistence.ISegmentXInfoRowMapperFactory;

/**
 * @author mwimmer
 *
 */
public class SegmentXInfoRowMapperFactory implements ISegmentXInfoRowMapperFactory {
	
	private static Logger log = LoggerFactory.getLogger(SegmentXInfoRowMapperFactory.class);
	
	@Autowired(required=false)
	protected List<ISegmentXInfoRowMapper<? extends ISegmentXInfo>> rowMappersList = null;

	protected Map<String, ISegmentXInfoRowMapper<? extends ISegmentXInfo>> rowMappers = new HashMap<>();
	
	@PostConstruct
	public void setup() {
		if (rowMappersList != null) {
			for (ISegmentXInfoRowMapper<? extends ISegmentXInfo> rowMapper : rowMappersList) {
				rowMappers.put(rowMapper.getResponsibleType(), rowMapper);
			}
		}
	}
	
	@Override
	public List<ISegmentXInfoRowMapper<? extends ISegmentXInfo>> getAll() {
		if(rowMappers != null || rowMappers.isEmpty()) {
			return new ArrayList<ISegmentXInfoRowMapper<? extends ISegmentXInfo>>(rowMappers.values());
		}
		else {
			log.debug("no row mappers for segmentXInfos registered, returning empty list");
			return new ArrayList<ISegmentXInfoRowMapper<? extends ISegmentXInfo>>();
		}
	}

	@Override
	public ISegmentXInfoRowMapper<? extends ISegmentXInfo> get(String type) {
		return rowMappers.get(type);
	}

}
