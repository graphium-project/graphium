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
package at.srfg.graphium.gipimport.application;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import at.srfg.graphium.gipimport.model.IImportConfig;
import at.srfg.graphium.gipimport.model.impl.ImportConfig;
import at.srfg.graphium.gipimport.service.impl.GipImporterService;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWaySegment;

/**
 * Main Class which should be used as entry point in case of a standalone tool
 */
public class IDF2JSONConverter {

    private static Logger log = Logger.getLogger(IDF2JSONConverter.class);

    /**
     * Main entry Point for all paramters to trigger the import process
     *
     * @param args all parameters, if something is wrong, the help page is shown.
     */
    public static void main(String... args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Options options = new Options();
        options.addOption("h", "help", false, "display this help page");
        options.addOption(Option.builder("i").longOpt("input").hasArg().argName("input_file").desc("path to IDF or compressed IDF File (ZIP)").build());;
        options.addOption(Option.builder("o").longOpt("output").hasArg().argName("output_file").desc("path to result directory. (default: user.home)").build());
        options.addOption(Option.builder("n").longOpt("name").hasArg().argName("graph_name").desc("Name of the graph to be imported").build());
        options.addOption(Option.builder("v").longOpt("version").hasArg().argName("graph_version").desc("Version of the graph to be imported").build());
        options.addOption(Option.builder("vf").longOpt("valid-from").hasArg().argName("valid_from").desc("validFrom timestamp (format 'yyyy-MM-dd HH:mm')").build());;
        options.addOption(Option.builder("vt").longOpt("valid-to").hasArg().argName("valid_from").desc("validTo timestamp (format 'yyyy-MM-dd HH:mm')").build());;
//        options.addOption(null, "original-name", false, "the original name of the graph");
//        options.addOption(null, "original-version", false, "the original version of the graph");
        options.addOption(null, "skip-gip-import", false, "skip the import process of the GIP, only pixel cuts will be generated. The options -o and --import-frcs are ignored");
        options.addOption(null, "skip-pixel-cut", false, "skip the calculation of the turn offset factors. The options -m and -M are ignored");
        options.addOption(Option.builder("m").longOpt("pixel-cut-min-frc").hasArg().argName("frc_value").desc("minimum frc value to be considered for offset calculations").build());
        options.addOption(Option.builder("M").longOpt("pixel-cut-max-frc").hasArg().argName("frc_value").desc("maximum frc value to be considered for offset calculations. This should be the maximum rendering level. (default: 5)").build());
        options.addOption(Option.builder().longOpt("import-frcs").hasArgs().valueSeparator(',').argName("frc_values")
                .desc("Comma separated List of FRC values to be included for IDF import. If not set (default) all frc values are considered. ").build());
        options.addOption(Option.builder().longOpt("access-types").hasArgs().valueSeparator(',').argName("access_types")
                .desc("Comma separated List of Access Types, to be considered. If not set, all access types will be considered").build());
        options.addOption(Option.builder("e").longOpt("pixel-cut-enable-short-conn").desc("By default short connections below 3.5 meter with frc 0 are ignored. This is to filter the connections between highways and streets. If this option is set all gip links are considered").build());
        options.addOption(Option.builder("bl").longOpt("extract-buslane-info").desc("By default no bus lane info will be extracted.").build());
        options.addOption(Option.builder("fc").longOpt("full-connectivity").hasArg().argName("full_connectivity").desc("Create a full connected network ignoring one ways (default = false)").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            String outputDirectory = null;
            String gipFile = null;
            boolean gipImport = true;
            String name = null;
            String version = null;
            Integer[] frcValues = null;
            Access[] accessTypes = null;

            if (cmd.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("java -jar idf2graphium-0.1.0.one-jar.jar [OPTION]...", options);
                return;
            }
            if (cmd.hasOption('o')) {
                outputDirectory = cmd.getOptionValue('o');
            } else {
                outputDirectory = System.getProperty("user.home");
            }
            if (cmd.hasOption("skip-gip-import")) {
                gipImport = false;
            } else{
                if (cmd.hasOption("import-frcs")) {
                    String[] frcsString = cmd.getOptionValues("import-frcs");
                    frcValues = new Integer[frcsString.length];
                    for (int i = 0; i < frcsString.length; i++) {
                        frcValues[i] = Integer.parseInt(frcsString[i]);
                    }
                } 
                if (cmd.hasOption("access-types")) {
                    String[] accessTypesString = cmd.getOptionValues("access-types");
                    accessTypes = new Access[accessTypesString.length];
                    for (int i = 0; i < accessTypesString.length; i++) {
                        accessTypes[i] = Access.valueOf(accessTypesString[i]);
                    }
                }
            }
            if (cmd.hasOption("n")) {
                name = cmd.getOptionValue('n');
            } else {
                throw new RuntimeException("Missing required option: -n");
            }
            if (cmd.hasOption("v")) {
                version = cmd.getOptionValue('v');
            } else {
                throw new RuntimeException("Missing required option: -v");
            }
            if (cmd.hasOption('i')) {
                gipFile = cmd.getOptionValue('i');
            } else {
                //gip import not skipped but but input file is missing
                throw new RuntimeException("Missing required option: -i");
            }

            IImportConfig config = ImportConfig.getConfig(name,version,gipFile)
                    .outPutDir(outputDirectory).frcList(frcValues).accessTypes(accessTypes);

            Date now = new Date();
            if (cmd.hasOption("vf")) {
            	config.validFrom(df.parse(cmd.getOptionValue("vf")));
            } else {
            	config.validFrom(now);
            }
            
            if (cmd.hasOption("vt")) {
            	config.validTo(df.parse(cmd.getOptionValue("vt")));
            } 
            
            if (!gipImport){
                config.noGipImport();
            }

            if (cmd.hasOption('e')) {
                config.enableSmallConnections();
            }
            if (cmd.hasOption("bl")) {
                config.extractBusLaneInfo(true);
            }
            if (cmd.hasOption("skip-pixel-cut")) {
                config.noPixelCut();
            } else {
                if (cmd.hasOption('m')) {
                    config.minFrc(Integer.parseInt(cmd.getOptionValue('m')));
                }
                if (cmd.hasOption('M')) {
                    config.maxFrc(Integer.parseInt(cmd.getOptionValue('M')));
                }
            }
            if (cmd.hasOption("fc")) {
            	boolean fullConnectivity = Boolean.parseBoolean(cmd.getOptionValue("fc"));
            	if (fullConnectivity) {
            		config.enableFullConnectivity();
            	}
            }

            log.info("Starte IDF to JSON Converter ...");

            System.setProperty("org.geotools.referencing.forceXY", "true");

            log.info(config.toString());
            if(config.isImportGip()) {
                GipImporterService<IWaySegment, IWaySegmentDTO> service = new GipImporterService<>();
                service.importGip(config);    	
            }
            else {
                GipImporterService<IBaseSegment, IBaseSegmentDTO> service = new GipImporterService<>();
                service.importGip(config);
            }
        
            log.info("GIP File succesfully converted");

        } catch (Exception e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            if (e.getMessage() != null) {
                log.error(e.getMessage() + '\n');
            } else {
                log.error("Unknown error",e);
            }
            helpFormatter.printHelp("java -jar idf2graphium-0.1.0.one-jar.jar [OPTION]...", options);
        }
    }
}
