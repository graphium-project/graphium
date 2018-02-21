package at.srfg.graphium.core.helper;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 * <p>
 * Project: graphium
 * Created by sschwarz on 01.02.2018.
 */

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.impl.GraphVersionMetadataServiceImpl;
import at.srfg.graphium.model.*;

import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.management.impl.Source;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {//"classpath:/application-context-graphium-core.xml",
        "classpath:application-context-graphium-core-test.xml"})
public class TestGraphiumGraphVersionValidityPeriodValidator {

    @Autowired
    private GraphVersionValidityPeriodValidator validator;

    //define mock-object
    public IWayGraphVersionMetadataDao metadataDao = mock(IWayGraphVersionMetadataDao.class);



    @Test
    public void testValidateValidityPeriod(){
        test();
    }

    IWayGraph theGraph;

    private void test(){
        int sourceId = 815;
        ISource source = new Source(sourceId, "neue Testsource");
        String graphName = "testgraph";
        Date now = new Date();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("einSinnlosesTag", "holladrio");

        //TODO Ersetze metadataDao mit MOCK (Graphen im try-catch Block können verwendet werden!)
        //TODO Mock liefert Daten zurück und anschließend wird mit den unten stehenden Asserts getested

        //https://www.planetgeek.ch/2010/07/20/mockito-answer-vs-return/ //TODO delete
        //https://codingcraftsman.wordpress.com/2015/02/20/java-mockito-and-mocking-a-callback/ //TODO delete
        //https://stackoverflow.com/questions/16890133/cant-return-class-object-with-mockito //TODO delete

        //TEST 1
        /*
        when(metadataDao.getGraph(graphName)).thenAnswer(new Answer<IWayGraph>() {
            @Override
            public IWayGraph answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new WayGraph(1,"TEST");
            }
        });
        */


        //Test 2
        when(metadataDao.getGraph(graphName)).thenReturn(new WayGraph(1, "TEST"));

        IWayGraph graph = metadataDao.getGraph(graphName);
        assertEquals(new WayGraph(1,"TEST"), metadataDao.getGraph(graphName));

        long graphId = graph.getId();
        assertEquals(graphId, 1);

        IWayGraphVersionMetadata metadata;
        Set<Access> accessTypes = new HashSet<Access>();
        accessTypes.add(Access.PRIVATE_CAR);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            List<IWayGraphVersionMetadata> metaDataList = new ArrayList<IWayGraphVersionMetadata>();
            metaDataList.add(new WayGraphVersionMetadata(0,graphId, graphName, "1.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"),getBoundsAustria(),1000,2000,accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(1, graphId, graphName, "2.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(2, graphId, graphName, "3.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));

            when(metadataDao.getWayGraphVersionMetadataList(anyString())).thenReturn(metaDataList);

            //funktioniert!!!!
            //List<IWayGraphVersionMetadata> testtest = metadataDao.getWayGraphVersionMetadataList("bla");


            /*
            // march version
            metadata = metadataDao.newWayGraphVersionMetadata(0, graphId, graphName, "1.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            metadataDao.saveGraphVersion(metadata);

            // april version
            IWayGraphVersionMetadata metadataToUpdate = metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            metadataDao.saveGraphVersion(metadataToUpdate);

            // may version
            metadata = metadataDao.newWayGraphVersionMetadata(2, graphId, graphName, "3.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            metadataDao.saveGraphVersion(metadata);


            List<IWayGraphVersionMetadata> versions = dao.getWayGraphVersionMetadataList(graphName);
            for (IWayGraphVersionMetadata version : versions) {
                System.out.println("Version '" + version.getVersion() + "'");
            }
            */

            when(metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org")).thenReturn(new WayGraphVersionMetadata(1, graphId, graphName, "2.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));

            IWayGraphVersionMetadata metadataToUpdate = metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");


            /*
            //TODO JUST FOR TEST (BUT NOT USEFULL (not a real test))!!!!!
            //GraphVersionValidityPeriodValidator spyValidator = Mockito.spy(validator);
            GraphVersionValidityPeriodValidator validator2 = mock(GraphVersionValidityPeriodValidator.class);
            List<String> testReturnMessage = new ArrayList<String>();
            testReturnMessage.add("validFrom overlaps with existing version 1.0");
            List<String> testReturnMessage2 = new ArrayList<String>();
            testReturnMessage2.add("validFrom overlaps with existing version 1.0");
            testReturnMessage2.add("validTo overlaps with existing version 3.0");
            List<String> testReturnMessage3 = new ArrayList<String>();
            testReturnMessage3.add("validTo overlaps with existing version 3.0");
            when(validator2.validateValidityPeriod(metadataToUpdate)).thenReturn(null).thenReturn(testReturnMessage).thenReturn(testReturnMessage2).thenReturn(testReturnMessage3); //3 different return-messages in 3 different calls
            */



            // Comparable<String> c= mock(Comparable.class);
            // when(c.compareTo("Mockito")).thenReturn(1);
            // when(c.compareTo("Eclipse")).thenReturn(2);

            // Comparable<Todo> c= mock(Comparable.class);
            // when(c.compareTo(isA(Todo.class))).thenReturn(0);
            // //assert
            // assertEquals(0, c.compareTo(new Todo(1)));

            //TODO validator.validateValidityPeriod(metadataToUpdate) liefert immer Null zurück (validator verwendet auch ein metadataDao, dieses wird jedoch nicht richtig gemockt)
            //@PrepareForTest({Class1.class,Class2.class})
            List<String> messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNull(messages);

            metadataToUpdate.setValidFrom(df.parse("2017-03-25"));  //set field in metadataToUpdate-object
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            assertEquals("validFrom overlaps with existing version 1.0", messages.get(0));

            metadataToUpdate.setValidTo(df.parse("2017-05-03"));
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            assertEquals("validFrom overlaps with existing version 1.0", messages.get(0));
            assertEquals("validTo overlaps with existing version 3.0", messages.get(1));

            metadataToUpdate.setValidFrom(df.parse("2017-04-01"));
//			metadataToUpdate.setValidTo(df.parse("2017-05-03"));
            messages = validator.validateValidityPeriod(metadataToUpdate);
            Assert.assertNotNull(messages);
            assertEquals("validTo overlaps with existing version 3.0", messages.get(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Polygon getBoundsAustria() {
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
}
