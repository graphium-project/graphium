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
package at.srfg.graphium.gipimport.helper;

import java.util.HashSet;
import java.util.Set;

import at.srfg.graphium.gipimport.model.impl.GipAccess;
import at.srfg.graphium.model.Access;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Created by shennebe on 14.12.2015.
 */
public class ParserHelper {

    public static TObjectIntMap<String> splitAtrLine(String line) {
        TObjectIntMap<String> atrPos = new TObjectIntHashMap<>();
        String[] atr = line.split(";");
        int index = 0;
        for (String a : atr) {
            atrPos.put(a, index);
            index++;
        }
        return atrPos;
    }

    /**
     * do some checks and correct line
     */
    public static String[] correctLine(final String line) {

        boolean lineCorrected = false;

        // insert missing double quotes
        // cut 2 starting attributes from line (now the first attribute must be name1)
        StringBuilder buffer = new StringBuilder();
        int index = line.indexOf(';');
        index += line.substring(index + 1).indexOf(';') + 1;
        buffer.append(line.substring(0, index + 1));
        String subLine = line.substring(index + 1);

        // look for string delimiter of name1: is string closed by '"'?
        StringBuilder buffer2 = new StringBuilder();
        index = subLine.indexOf(";\"");
        if (!subLine.substring(0, 1).equals("\"")) {
            // missing opening double quote
            buffer2.append("\"");
            lineCorrected = true;
        }
        buffer2.append(subLine.substring(0, index));
        if (!subLine.substring(index - 1, index).equals("\"")) {
            // closing double quote is missing
            buffer2.append("\"");
            lineCorrected = true;
        }
        buffer2.append(";");

        // look for string delimiter of name2: is string closed by '"'?
        index++; // index of opening double quote (")
        subLine = subLine.substring(index + 1);
        index = subLine.indexOf('"');
        int indexSemicol = subLine.indexOf(';');
        if (index == -1 || index > indexSemicol) {
            // closing double quote is missing
            index = subLine.indexOf(';');
            buffer2.append("\"");
            buffer2.append(subLine.substring(0, index));
            buffer2.append("\"");
            buffer2.append(subLine.substring(index));
            lineCorrected = true;
        } else {
            buffer2.append("\"").append(subLine);
        }

        buffer.append(buffer2);


        if (lineCorrected) {
            return new String[]{buffer.toString(),"corrected"};
        }
        return new String[] {buffer.toString()};
    }

    /**
     * @param access validate the access
     * @return acessFound
     */
    public static boolean validateAccess(int access, Set<Access> accessTypes) {
        boolean accessFound = false;
        for (Access acc : adaptAccess(access)) {
            if (!accessFound && accessTypes.contains(acc)) {
                accessFound = true;
            }
        }
        return accessFound;
    }

    public static Set<Access> adaptAccess(int gipAccesses) {
        Set<Access> accesses = new HashSet<>();
        // Caution: Names must be equal
        for (GipAccess gipAccess : GipAccess.getAccessTypes(gipAccesses)) {
            accesses.add(Access.valueOf(gipAccess.name()));
        }
        return accesses;
    }

    public static String rebuildHeaderLine(String[] values) {
        if (values != null) {
            StringBuilder builder = new StringBuilder();
            for (String value : values) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                builder.append(value);
            }
            return builder.toString();
        } else {
            return null;
        }
    }
}
