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

package at.srfg.graphium.tutorial;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.model.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 20.02.2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-graphium-tutorial_test.xml"})
@Transactional
public class TestSuite {
    @Resource(name="testclasses")
    List<ITestGraphiumModelExtension> testList;

    @Resource(name = "postgisQueuingGraphVersionImportService")
    private IGraphVersionImportService importService;

    @Autowired
    private IGraphVersionMetadataService metadataService;

    @Value("${db.graphName}")
    String graphName;
    @Value("${db.version}")
    String version;

    @Before
    @Transactional(readOnly=false)
    public void initDB(){
        try {
            File file = new File(System.getProperty("user.dir") + "\\db\\testdata\\osm_dk_180209.zip");
            FileInputStream fis = new FileInputStream(file);
            ZipInputStream zip = new ZipInputStream(fis);
            zip.getNextEntry();
            importService.importGraphVersion(graphName, version, zip, true);
            metadataService.setGraphVersionState(graphName, version, State.ACTIVE);
        } catch (GraphImportException e) {
            e.printStackTrace();
        } catch (GraphAlreadyExistException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional(readOnly = false)
    public void run(){
        for(ITestGraphiumModelExtension testclass : testList){
            testclass.run();
        }
    }
}
