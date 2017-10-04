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

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.converter.IXinfoDTOToCSVAdapter;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.dto.IXInfoDTO;
import javafx.util.Pair;
public class ReflectionXInfoDTOToCSVConverterImpl implements IXinfoDTOToCSVAdapter<IXInfoDTO> {

    private static final char SEPERATOR = ';';
    
	private static Logger log = LoggerFactory.getLogger(ReflectionXInfoDTOToCSVConverterImpl.class);
	
	@Override
	public String adapt(IXInfoDTO xInfo, Pair... additionalFields) {
		StringBuilder buffer = new StringBuilder();
		if (xInfo != null) {
            Field[] fields = xInfo.getClass().getDeclaredFields();
            // write id
            for (Pair additionalField : additionalFields) {
                buffer.append(additionalField.getValue().toString()).append(SEPERATOR);
            }
            for (Field field : fields) {
                try {
                	// TODO: ist das gut hier ein Accessible zu setzen? Das würde doch eine private methode zu public machen und 
                	// nie wieder zurück tauschen?
                    field.setAccessible(true);
                    buffer.append(field.get(xInfo)).append(SEPERATOR);
                } catch (IllegalAccessException e) {
                    log.error("Illegal access of xInfo Property: " + field.getName() + " from xinfo " + xInfo.getClass());
                }
            }
            if (xInfo instanceof ISegmentXInfoDTO) {
                buffer.append(((ISegmentXInfoDTO) xInfo).getDirectionTow());
                buffer.append(SEPERATOR);
            }
            buffer.deleteCharAt(buffer.lastIndexOf(SEPERATOR+ ""));
        }
		buffer.append(System.getProperty("line.separator"));
		return buffer.toString();
	}

	@Override
	public String headers(IXInfoDTO xInfo, Pair... additionalFieldNames) {
		StringBuilder buffer = new StringBuilder();
		if (xInfo != null) {
            Field[] fields = xInfo.getClass().getDeclaredFields();
            // write id
            for (Pair additionalFieldName : additionalFieldNames) {
                buffer.append(additionalFieldName.getKey().toString()).append(SEPERATOR);
            }
            for (Field field : fields) {
                   	// TODO: ist das gut hier ein Accessible zu setzen? Das würde doch eine private methode zu public machen und 
                	// nie wieder zurück tauschen?
                 field.setAccessible(true);
                 buffer.append(field.getName()); buffer.append(SEPERATOR);
   
            }
            if (xInfo instanceof ISegmentXInfoDTO) {
                buffer.append("direction_tow");
                buffer.append(SEPERATOR);
            }
            buffer.deleteCharAt(buffer.lastIndexOf(SEPERATOR + ""));
        }
		buffer.append(System.getProperty("line.separator"));
		return buffer.toString();
	}
	
}
