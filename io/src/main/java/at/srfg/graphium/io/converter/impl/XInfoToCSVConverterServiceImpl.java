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
package at.srfg.graphium.io.converter.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.converter.IXInfoToCSVConverterService;
import at.srfg.graphium.io.converter.IXinfoDTOToCSVAdapter;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IXInfoDTO;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IXInfo;

public abstract class XInfoToCSVConverterServiceImpl<T extends IXInfoDTO, S extends IXInfo> implements IXInfoToCSVConverterService {
	

	private static final String CHARSET = "UTF-8";

	private static Logger log = LoggerFactory.getLogger(ReflectionXInfoToCSVConverterImpl.class);
	
	private IAdapter<String, S> adapter;
	private IXinfoDTOToCSVAdapter<T> dtoAdapter;

	@Override
	public void convert(List<IBaseSegment> segments, Map<String, OutputStream> outputs) throws IOException {
		List<S> xInfos;
		for(IBaseSegment segment : segments) {
			for(Entry<String,OutputStream> output : outputs.entrySet()) {
				xInfos = this.getXInfosFromSegment(output.getKey(),segment);
				serialize(xInfos, output.getValue());				
			}
		}
	}

	abstract List<S> getXInfosFromSegment(String key, IBaseSegment segment);

	abstract Map<Pair<String, Object>[],Map<String, List<T>>> getXInfosFromSegmentDTO(IBaseSegmentDTO segmentDTO);
	
	@Override
	public void convertDTOs(final List<IBaseSegmentDTO> segments,
			final Map<String, OutputStream> outputs) throws IOException {
		
		Map<Pair<String, Object>[],Map<String, List<T>>> xInfosObjectMap;
		final Set<String> headerWritten = new HashSet<>();
		
		for(IBaseSegmentDTO segmentDTO : segments) {
			for(final Entry<String,OutputStream> output : outputs.entrySet()) {
				xInfosObjectMap = this.getXInfosFromSegmentDTO(segmentDTO);
				if (xInfosObjectMap != null) {
					xInfosObjectMap.forEach(((pairs, stringListMap) -> {
						if(stringListMap != null && stringListMap.containsKey(output.getKey())) {
							try {
								List<T> xInfos = stringListMap.get(output.getKey());
								if(!headerWritten.contains(output.getKey()) && !xInfos.isEmpty()) {
                                    output.getValue().write(dtoAdapter.headers(xInfos.get(0),pairs).getBytes(Charset.forName(CHARSET)));
                                    headerWritten.add(output.getKey());
                                }
								serializeDTO(xInfos,output.getValue(), pairs);
							} catch (IOException e) {
								log.error("IO Exception while writing output",e);
							}
						}
					}));
				}
			}
		}
	}
	
	
	@Override
	public void close(Map<String, OutputStream> outputs) throws IOException {
		for(OutputStream outStream : outputs.values()) {
			outStream.flush();
			outStream.close();
		}
	}

	private void serialize(List<S> xInfos, OutputStream outStream) throws IOException {
		if(xInfos != null && !xInfos.isEmpty()) {
			for(S xInfo : xInfos) {
				outStream.write(adapter.adapt(xInfo).getBytes(Charset.forName(CHARSET)));
			}
		}
	}
	
	private void serializeDTO(List<T> xInfos, OutputStream outStream, Pair<String, Object>... additionalFields) throws IOException {
		if(xInfos != null && !xInfos.isEmpty()) {
			for(T xInfo : xInfos) {
				outStream.write(dtoAdapter.adapt(xInfo, additionalFields).getBytes(Charset.forName(CHARSET)));
			}
		}
	}
	
	public IAdapter<String, S> getAdapter() {
		return adapter;
	}

	public void setAdapter(IAdapter<String, S> adapter) {
		this.adapter = adapter;
	}

	public IXinfoDTOToCSVAdapter<T> getDtoAdapter() {
		return dtoAdapter;
	}

	public void setDtoAdapter(IXinfoDTOToCSVAdapter<T> dtoAdapter) {
		this.dtoAdapter = dtoAdapter;
	}
}
