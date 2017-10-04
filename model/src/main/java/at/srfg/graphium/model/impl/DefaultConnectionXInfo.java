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
package at.srfg.graphium.model.impl;

import java.util.HashMap;
import java.util.Map;

import at.srfg.graphium.model.IDefaultConnectionXInfo;

/**
 * Created by shennebe on 23.09.2016.
 */
public class DefaultConnectionXInfo extends AbstractConnectionXInfo implements IDefaultConnectionXInfo {

	protected Map<String, Object> values;

    public DefaultConnectionXInfo() {
        super("default");
    }

	@Override
	public Map<String, Object> getValues() {
		return values;
	}
	
	@Override
	public Object getValue(String key) {
		if (values != null) {
			return values.get(key);
		} else {
			return null;
		}
	}
	
	@Override
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	
	@Override
	public void setValue(String key, Object value) {
		if (values == null) {
			values = new HashMap<>();
		}
		values.put(key, value);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		IDefaultConnectionXInfo returnObject = (IDefaultConnectionXInfo) super.clone();
		returnObject.setValues(this.values);
		return returnObject;
	}
	
}