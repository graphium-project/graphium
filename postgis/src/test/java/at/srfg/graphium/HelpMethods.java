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
package at.srfg.graphium;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 14.02.2018.
 */
public class HelpMethods {
    public static Polygon getBoundsAustria() {
        WKTReader reader = new WKTReader();
        String poly = "POLYGON((9.5282778 46.3704647,9.5282778 49.023522,17.1625438 49.023522,17.1625438 46.3704647,9.5282778 46.3704647))";
        Polygon bounds = null;
        try {
            bounds = (Polygon) reader.read(poly);
            bounds.setSRID(4326);
        } catch (com.vividsolutions.jts.io.ParseException e) {
            e.printStackTrace();
        }
        return bounds;
    }


    public static Properties getDBProperties() {
        try {
            Properties prop = new Properties();
            InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\db.properties");
            if (inputStream != null) {
                prop.load(inputStream);
                return prop;
            } else {
                throw new FileNotFoundException("property file not found in the classpath");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


