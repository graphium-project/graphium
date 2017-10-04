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

import at.srfg.graphium.model.IXInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IAdapter;

public class ReflectionXInfoToCSVConverterImpl implements IAdapter<String, IXInfo> {


    private static final char SEPERATOR = ';';
    
	private static Logger log = LoggerFactory.getLogger(ReflectionXInfoToCSVConverterImpl.class);
	
	@Override
	public String adapt(IXInfo xInfo) {
		StringBuilder buffer = new StringBuilder();
		if (xInfo != null) {
            Field[] fields = xInfo.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                	// TODO: ist das gut hier ein Accessible zu setzen? Das würde doch eine private methode zu public machen und 
                	// nie wieder zurück tauschen?
                    field.setAccessible(true);
                    buffer.append(field.get(xInfo)); buffer.append(SEPERATOR);
                } catch (IllegalAccessException e) {
                    log.error("Illegal access of xInfo Property: " + field.getName() + " from xinfo " + xInfo.getXInfoType());
                }
            }
        }
		buffer.append(System.getProperty("line.separator"));
		return buffer.toString();
	}


}
