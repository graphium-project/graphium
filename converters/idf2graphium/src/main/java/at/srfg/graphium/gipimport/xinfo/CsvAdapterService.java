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
package at.srfg.graphium.gipimport.xinfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.srfg.graphium.io.csv.ICsvXInfoFactory;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.model.ISegmentXInfo;
import au.com.bytecode.opencsv.CSVReader;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * Created by mwimmer
 */
public class CsvAdapterService {

	public TLongObjectMap<List<ISegmentXInfo>> adaptCsvFile(String fileName, String className, String encoding) {
		TLongObjectMap<List<ISegmentXInfo>> xinfoMap = null;
		BufferedReader file = null;
		CSVReader reader = null;
		
		try {
			// create XInfo factory class - has to be from type ICsvXInfoFactory
			Class<?> factoryClass = Class.forName(className);
			@SuppressWarnings("unchecked")
			ICsvXInfoFactory<ISegmentXInfo, ISegmentXInfoDTO> factory = (ICsvXInfoFactory<ISegmentXInfo, ISegmentXInfoDTO>) factoryClass.newInstance();
			
			// read CSV file
			InputStream inputStream = new FileInputStream(fileName);
			file = new BufferedReader(new InputStreamReader(inputStream, encoding));
			String line = file.readLine();
			
			// in case of UTF-8 first line could begin with BOM
			line = line.replace("\uFEFF", "");
			
			reader = new CSVReader(new StringReader(line), ';');
			
			if (line != null) {
				xinfoMap = new TLongObjectHashMap<>();
				String[] header = reader.readNext();
				reader.close();
				line = file.readLine();
	
				//adapt lines into XInfo objects
				while (line != null) {
					Map<String, String> attributes = new HashMap<>();
					reader = new CSVReader(new StringReader(line), ';');
					String[] values = reader.readNext();
					for (int i=0; i<header.length; i++) {
						attributes.put(header[i], values[i]);
					}
					
					ISegmentXInfo xinfo = factory.adapt(attributes);
					
					if (!xinfoMap.containsKey(xinfo.getSegmentId())) {
						xinfoMap.put(xinfo.getSegmentId(), new ArrayList<>());
					}
					xinfoMap.get(xinfo.getSegmentId()).add(xinfo);

					line = file.readLine();
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return xinfoMap;
	}
	
}