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
package at.srfg.graphium.gipimport.service.impl;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shennebe on 10.08.2015.
 */
public class TestUTF8Writer {

	@Ignore
    @Test
    public void testWriteURF8() throws IOException {
        Path path = Paths.get("D:/data/vao/Gip_Import/test.sql");
        List<CharSequence> charSequences = new ArrayList<>();
        charSequences.add("Dies ist ein Umlaute-Test");
        charSequences.add("Daß alle Würmer die wärme nicht mögen");
        StringBuilder sb = new StringBuilder();
        sb.append("Daß alle Würmer die wärme nicht mögen").append("--").append(" With StringBuilder");
        charSequences.add(sb);
        Files.write(path, charSequences, StandardOpenOption.CREATE_NEW);
    }

}
