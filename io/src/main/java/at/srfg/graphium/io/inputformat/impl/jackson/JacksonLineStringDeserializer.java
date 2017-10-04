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
package at.srfg.graphium.io.inputformat.impl.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class JacksonLineStringDeserializer extends JsonDeserializer<LineString> {	
	
	@Override
	public LineString deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Geometry geom = parse(jp);
		if(geom instanceof LineString) {
			return (LineString)geom;		
		}
		throw new JsonParseException(jp, "parsed geometry was not a LineString!", jp.getCurrentLocation());
	}

	private Geometry parse(JsonParser jp) throws IOException, JsonProcessingException {
		WKTReader reader = new WKTReader();
		Geometry geom;		
		try {
			geom = reader.read(jp.getText());
		} catch (ParseException e) {
			throw new JsonParseException(jp, "wkt not parsable", jp.getCurrentLocation(), e);
		}
		return geom;
	}

}
