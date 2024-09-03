package at.srfg.graphium.gipimport.parser.impl;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipLinkToTruck;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.model.impl.GipLinkToTruckImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.io.BufferedReader;
import java.io.IOException;


public class GipLinkToTruckParser extends AbstractSectionParser<TLongObjectMap<IGipLinkToTruck>> {

    private final IImportConfigIdf config;
    //private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    private TLongObjectMap<IGipLinkToTruck> map;

    public GipLinkToTruckParser(IGipParser parserReference,
                                          IImportConfigIdf config//,
                                          //IGipSectionParser<TLongObjectMap<IGipLink>> linkParser
    ) {
        super(parserReference);
        this.config = config;
        //this.linkParser = linkParser;
        map = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.LINK_TO_TRUCK;
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {
        //TODO: implement me correct
        String line = null;
        TObjectIntMap<String> atrPos = null;

        try {
            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    break;
                }

                //atr;OBJECT_ID;SHORT_ID;NODE_FROM_ID;NODE_TO_ID;NODE_FROM_SHORT_ID;NODE_TO_SHORT_ID;ACCESS_TRUCK3500_TOW;ACCESS_TRUCK3500_BKW;ACCESS_TRUCK7500_TOW;ACCESS_TRUCK7500_BKW;ACCESS_TRUCK16000_TOW;ACCESS_TRUCK16000_BKW;MAX_WIDTH;MAX_HEIGHT;MAX_PRESSURE;MAX_LENGTH;MAXSPEED_TOW_TRUCK;MAXSPEED_BKW_TRUCK;ABUTTER_TRUCK3500;ABUTTER_TRUCK7500;ABUTTER_TRUCK16000;ACCESS_SEMITRUCK_TOW;ACCESS_SEMITRUCK_BKW;ACCESS_TRAILERTRUCK_TOW;ACCESS_TRAILERTRUCK_BKW
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");
                    IGipLinkToTruck object = new GipLinkToTruckImpl();

                    object.setShortId(Long.parseLong(values[atrPos.get("SHORT_ID")])); //TODO: not necessary
                    object.setNodeFromShortId(Long.parseLong(values[atrPos.get("NODE_FROM_SHORT_ID")]));
                    object.setNodeToShortId(Long.parseLong(values[atrPos.get("NODE_TO_SHORT_ID")]));
                    object.setMaxWidth(Float.parseFloat(values[atrPos.get("MAX_WIDTH")]));

                    //uuids
                    object.setObjectId(values[atrPos.get("OBJECT_ID")].replaceAll("\"", ""));
                    object.setNodeFromId(values[atrPos.get("NODE_FROM_ID")].replaceAll("\"", ""));
                    object.setNodeToId(values[atrPos.get("NODE_TO_ID")].replaceAll("\"", ""));

                    map.put(object.getShortId(), object);
                }

                line = file.readLine();
            }

        } catch (IOException e) {
            log.error(e.toString());
        }

        postImport();

        return line;
    }

    @Override
    public void postImport() {

    }

    @Override
    public TLongObjectMap<IGipLinkToTruck> getResult() {
        return this.map;
    }
}
