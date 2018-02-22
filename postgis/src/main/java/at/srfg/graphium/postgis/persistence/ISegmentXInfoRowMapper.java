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
package at.srfg.graphium.postgis.persistence;

import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;

import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IXInfoModelTypeAware;

/**
 * Interface identifies RowMappers for mapping extended information tables.
 * 
 * @author mwimmer
 */
public interface ISegmentXInfoRowMapper<T extends ISegmentXInfo> extends RowMapper<IBaseSegment>, ISegmentMapper, IXInfoModelTypeAware<T> {
	
	public boolean isApplicable(ResultSet rs);

}