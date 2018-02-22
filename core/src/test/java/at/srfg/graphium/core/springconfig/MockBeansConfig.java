package at.srfg.graphium.core.springconfig;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 05.02.2018.
 *
 * This class configures the mock-dao used for the GraphiumGraphVersionValidityPeriodValidator-test
 */

//@Configuration
public class MockBeansConfig {
    private static Source source = new Source(815,"neue Testsource");
    private static String sourceName = "neue Testsource";
    private static String graphName = "testgraph";
    private static Date now = new Date();
    private static Map<String, String> tags = new HashMap<String, String>(){{
        put("einSinnloserTag","holladrio");
    }};
    private static int graphId = 1;

    private static Set<Access> accessTypes = new HashSet<Access>(Arrays.asList(Access.PRIVATE_CAR));
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    //Getter
    public static Source getSource() {
        return source;
    }

    public static String getSourceName() {
        return sourceName;
    }

    public static String getGraphName() {
        return graphName;
    }

    public static Date getNow() {
        return now;
    }

    public static Map<String, String> getTags() {
        return tags;
    }

    public static int getGraphId() {
        return graphId;
    }

    public static Set<Access> getAccessTypes() {
        return accessTypes;
    }

    public static SimpleDateFormat getDf() {
        return df;
    }

    //Bean-configuration
    //@Bean(name = "wayGraphVersionMetadataDao")
    public static IWayGraphVersionMetadataDao getWayGraphVersionMetadataDaoMock() {
        //define the mock-object using mockito when then
        try{
            IWayGraphVersionMetadataDao metadataDao = mock(IWayGraphVersionMetadataDao.class);
            //when(metadataDao.getGraph(anyString())).thenReturn(new WayGraph(1, "TEST"));
            when(metadataDao.getGraph(graphName)).thenReturn(new WayGraph(graphId, "TEST"));

            List<IWayGraphVersionMetadata> metaDataList = new ArrayList<IWayGraphVersionMetadata>();
            metaDataList.add(new WayGraphVersionMetadata(0,graphId, graphName, "1.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"),getBoundsAustria(),1000,2000,accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(1, graphId, graphName, "2.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(2, graphId, graphName, "3.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));

            when(metadataDao.getWayGraphVersionMetadataList(anyString())).thenReturn(metaDataList);

            IWayGraphVersionMetadata metadataToUpdate = metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");

            when(metadataDao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0",
                    graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org")).thenReturn(new WayGraphVersionMetadata(1, graphId, graphName, "2.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));

            // return configured mock to container as bean --> should be same instance in all injecting services
            return metadataDao;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

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
}
