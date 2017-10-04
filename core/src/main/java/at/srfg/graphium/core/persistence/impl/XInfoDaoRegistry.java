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
package at.srfg.graphium.core.persistence.impl;

import java.util.*;

import javax.annotation.PostConstruct;

import at.srfg.graphium.model.IConnectionXInfo;
import org.springframework.beans.factory.annotation.Autowired;

import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.core.persistence.IXInfoDaoRegistry;
import at.srfg.graphium.model.ISegmentXInfo;

/**
 * @author mwimmer
 *
 */
public class XInfoDaoRegistry<S extends ISegmentXInfo, C extends IConnectionXInfo> implements IXInfoDaoRegistry<S,C> {

	@Autowired(required=false)
	private List<IXInfoDao<S>> segmentDaoList = null;

	@Autowired(required=false)
	private List<IXInfoDao<C>> connectionDaoList = null;

	private Map<String,IXInfoDao<S>> segmentDaoMap = new HashMap<>();

	private Map<String,IXInfoDao<C>> connectionDaoMap = new HashMap<>();

	@PostConstruct
	public void init() {
		if (this.segmentDaoList != null) {
			for (IXInfoDao<S> dao : segmentDaoList) {
				this.segmentDaoMap.put(dao.getResponsibleType(),dao);
			}
		}
		if (this.connectionDaoList != null) {
			for (IXInfoDao<C> dao : connectionDaoList) {
				this.connectionDaoMap.put(dao.getResponsibleType(),dao);
			}
		}
	}

	@Override
	public IXInfoDao<S> getSegmentXInfoDao(String type) {
		return segmentDaoMap.get(type);
	}

	@Override
	public IXInfoDao<C> getConnectionXInfoDao(String type) {
		return connectionDaoMap.get(type);
	}

	@Override
	public List<IXInfoDao<S>> getAllSegmentXInfoDaos() {
		return new ArrayList<>(segmentDaoMap.values());
	}

	@Override
	public List<IXInfoDao<C>> getAllConnectionXInfoDaos() {
		return new ArrayList<>(connectionDaoMap.values());
	}

}