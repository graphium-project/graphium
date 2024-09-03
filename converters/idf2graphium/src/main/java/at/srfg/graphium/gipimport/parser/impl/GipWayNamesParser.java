package at.srfg.graphium.gipimport.parser.impl;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.*;
import at.srfg.graphium.gipimport.model.impl.GipWayNamesImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;

import java.io.BufferedReader;
import java.io.IOException;

//public class GipWayNamesParser extends AbstractSectionParser<TLongObjectMap<IGipWayNames>> {
public class GipWayNamesParser extends AbstractSectionParser<THashMap<String, IGipWayNames>> {

    private final IImportConfigIdf config;
    //private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    //private TLongObjectMap<IGipWayNames> map;
    //private TCharObjectMap<GipWayNamesImpl> map;
    private THashMap<String, IGipWayNames> map;

    public GipWayNamesParser(IGipParser parserReference,
                                    IImportConfigIdf config//,
                                    //IGipSectionParser<TLongObjectMap<IGipLink>> linkParser
    ) {
        super(parserReference);
        this.config = config;
        //this.linkParser = linkParser;
        //  map = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
        //map = new TSynchronizedCharObjectMap<>(new TCharObjectHashMap<>());
        map = new THashMap<>();
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.WAY_NAMES;
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {
        String line = null;
        TObjectIntMap<String> atrPos = null;

        try {
            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    break;
                }

                //atr;OBJECT_ID;SHORT_ID;OWNER;NAME_ID;NAME_SHORT_ID;NAME_TEXT;NAME_CATEGORY;NAME_CATEGORY_LONG;REGIONAL_CODE;STREET_CODE;SHORT_NAME
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");

                    IGipWayNames object = new GipWayNamesImpl();

                    object.setNameText(values[atrPos.get("NAME_TEXT")]);
                    object.setNameCategoryLong(values[atrPos.get("NAME_CATEGORY_LONG")]);
                    object.setShortName(values[atrPos.get("SHORT_NAME")]);
                    object.setObjectId(values[atrPos.get("OBJECT_ID")].replaceAll("\"", ""));
                    object.setShortId(Long.parseLong(values[atrPos.get("SHORT_ID")]));

                    map.put(object.getObjectId(), object);
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
    public THashMap<String, IGipWayNames> getResult() {
        return this.map;
    }
}
