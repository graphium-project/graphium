package at.srfg.graphium.core.helper;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 01.02.2018.
 *
 * This class tests the graphVersionValidityPeriodValidator
 */

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.springconfig.MockBeansConfig;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context-graphium-core-test.xml"})
public class TestGraphiumGraphVersionValidityPeriodValidator {

    @Autowired
    private GraphVersionValidityPeriodValidator validator;

    //define mock-object
    @Autowired
    public IWayGraphVersionMetadataDao metadataDao;

    @Test
    public void testValidateValidityPeriod(){
        test();
    }

    private void test(){
        //get initialization-data from MockBeansConfig
        ISource source = MockBeansConfig.getSource();
        String graphName = MockBeansConfig.getGraphName();
        Date now = MockBeansConfig.getNow();
        Map<String, String> tags = MockBeansConfig.getTags();
        Set<Access> accessTypes = MockBeansConfig.getAccessTypes();
        SimpleDateFormat df = MockBeansConfig.getDf();
        int graphId = MockBeansConfig.getGraphId();

        //test wayGraph
        IWayGraph graph = metadataDao.getGraph(graphName);
        assertEquals(new WayGraph(1,"TEST"), metadataDao.getGraph(graphName));
        assertEquals(graph.getId(), 1);

        try {
            //generate metadataToUpdate-object
            IWayGraphVersionMetadata metadataToUpdate = metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), MockBeansConfig.getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");

            //test validator (the data is created from the mock-object: metadataDao)
            List<String> messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNull(messages);

            metadataToUpdate.setValidFrom(df.parse("2017-03-25"));
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            //assertEquals("overlaps with existing version 1.0", messages.get(0).substring(messages.get(0).indexOf(")")+1,messages.get(0).lastIndexOf("(")).trim());
            assertEquals("new version (2017-03-25 00:00:00 - 2017-05-01 00:00:00) overlaps with existing version 1.0 (2017-03-01 00:00:00 - 2017-03-31 00:00:00)", messages.get(0).trim());

            metadataToUpdate.setValidTo(df.parse("2017-05-03"));
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            assertEquals("new version (2017-03-25 00:00:00 - 2017-05-03 00:00:00) overlaps with existing version 1.0 (2017-03-01 00:00:00 - 2017-03-31 00:00:00)", messages.get(0).trim());
            assertEquals("new version (2017-03-25 00:00:00 - 2017-05-03 00:00:00) overlaps with existing version 3.0 (2017-05-01 00:00:00 - 2017-05-31 00:00:00)", messages.get(1).trim());

            metadataToUpdate.setValidFrom(df.parse("2017-04-01"));
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            assertEquals("new version (2017-04-01 00:00:00 - 2017-05-03 00:00:00) overlaps with existing version 3.0 (2017-05-01 00:00:00 - 2017-05-31 00:00:00)", messages.get(0).trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
