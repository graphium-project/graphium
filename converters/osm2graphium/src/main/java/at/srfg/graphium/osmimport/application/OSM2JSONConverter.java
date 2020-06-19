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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.osmimport.application;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import at.srfg.graphium.osmimport.model.IImportConfigOsm;
import at.srfg.graphium.osmimport.model.impl.ImportConfigOsm;
import at.srfg.graphium.osmimport.model.impl.OsmTagAdaptionMode;
import at.srfg.graphium.osmimport.service.impl.OsmImporterServiceImpl;

/**
 * @author mwimmer
 */
public class OSM2JSONConverter {
	
	private static Logger log = Logger.getLogger(OSM2JSONConverter.class);

    /**
     * Main entry Point for all paramters to trigger the import process
     *
     * @param args all parameters, if somethin is wrong, the help page is shown.
     */
    public static void main(String... args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Options options = new Options();
        options.addOption("h", "help", false, "display this help page");
        options.addOption(Option.builder("i").longOpt("input").hasArg().argName("input_file").desc("path or URL to OSM File (PBF)").build());;
        options.addOption(Option.builder("o").longOpt("output").hasArg().argName("output_dir").desc("path to result directory. (default: user.home)").build());
        options.addOption(Option.builder("n").longOpt("name").hasArg().argName("graph_name").desc("Name of the graph to be imported").build());
        options.addOption(Option.builder("v").longOpt("version").hasArg().argName("graph_version").desc("Version of the graph to be imported").build());
        options.addOption(Option.builder("vf").longOpt("valid-from").hasArg().argName("valid_from").desc("validFrom timestamp (format 'yyyy-MM-dd HH:mm')").build());;
        options.addOption(Option.builder("vt").longOpt("valid-to").hasArg().argName("valid_from").desc("validTo timestamp (format 'yyyy-MM-dd HH:mm')").build());;
        options.addOption(Option.builder("b").longOpt("bounds").hasArg().argName("bounds_file").desc("Name of bounds file for geographical filtering (format alá Osmosis)").build());
        options.addOption(Option.builder("q").longOpt("queueSize").hasArg().argName("queue_size").desc("Size of import queue").build());
        options.addOption(Option.builder("t").longOpt("threads").hasArg().argName("worker_threads").desc("Number of worker threads").build());
        options.addOption(Option.builder("d").longOpt("downloadDir").hasArg().argName("download_dir").desc("directory to store download files").build());
        options.addOption(Option.builder("df").longOpt("keepDownloadFile").hasArg().argName("keep_download_file").desc("false: downloaded file from URL will be deleted afterwards; default = true").build());
        options.addOption(Option.builder("fd").longOpt("forceDownload").hasArg().argName("force_download").desc("true: downloaded file from URL even if it has been already downloaded / false: if file exists in download directory this one will be used; default = false").build());
        options.addOption(Option.builder("cf").longOpt("keepConvertedFile").hasArg().argName("keep_converted_file").desc("false: converted file will be deleted afterwards; default = true").build());
        options.addOption(Option.builder("u").longOpt("importUrl").hasArg().argName("import_url").desc("server URL to import the converted graph file").build());
        
        options.addOption(Option.builder("T").longOpt("tags").hasArg().argName("tag_mode").desc("mode how osm tags of ways are stored on created segments, "
        		+ "allowed modes 'none'|'all', defaults to 'none'").build());
        
        options.addOption(Option.builder().longOpt("highwayTypes").hasArgs().valueSeparator(',').argName("highway_types")
                .desc("Comma separated List of highway types, to be considered. If not set, all highway types will be considered").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            String outputDirectory = null;
            String osmFile = null;
            String boundsFile = null;
            String name = null;
            String version = null;
            String[] highwayTypesString = null;
            int threads = 1;
            int queueSize = 20000;
            OsmTagAdaptionMode tagAdaptionMode = OsmTagAdaptionMode.NONE;

            if (cmd.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("java -jar osm2graphium.one-jar.jar [OPTION]...", options);
                return;
            }
            if (cmd.hasOption('o')) {
                outputDirectory = cmd.getOptionValue('o');
            } else {
                outputDirectory = System.getProperty("user.home");
            }
            if (cmd.hasOption("highwayTypes")) {
                highwayTypesString = cmd.getOptionValues("highwayTypes");
                for (int i = 0; i < highwayTypesString.length; i++) {
                	highwayTypesString[i] = highwayTypesString[i].trim();
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
            	osmFile = cmd.getOptionValue('i');
            } else {
                throw new RuntimeException("Missing required option: -i");
            }
            if (cmd.hasOption("b")) {
                boundsFile = cmd.getOptionValue('b');
            }
            if (cmd.hasOption("t")) {
            	try {
            		threads = Integer.parseInt(cmd.getOptionValue('t'));
            	} catch (NumberFormatException e) {
            	}
            }
            if (cmd.hasOption("q")) {
            	try {
            		queueSize = Integer.parseInt(cmd.getOptionValue('q'));
            	} catch (NumberFormatException e) {
            	}
            }
            if (cmd.hasOption("T")) {
            	try {
            		tagAdaptionMode = OsmTagAdaptionMode.fromValue(cmd.getOptionValue("T"));
            	} catch(IllegalArgumentException e) {
            		throw new RuntimeException("wrong value for option T " + e.getMessage());
            	}
            }
            
            IImportConfigOsm config = ImportConfigOsm.getConfig(name, version, osmFile);
            config.setOutPutDir(outputDirectory);
            config.setBoundsFile(boundsFile);
            config.setQueueSize(queueSize);
            config.setWorkerThreads(threads);
            config.setHighwayList(highwayTypesString);
            config.setTagAdaptionMode(tagAdaptionMode);


            Date now = new Date();
            if (cmd.hasOption("vf")) {
            	config.setValidFrom(df.parse(cmd.getOptionValue("vf")));
            } else {
            	config.setValidFrom(now);
            }
            
            if (cmd.hasOption("vt")) {
            	config.setValidTo(df.parse(cmd.getOptionValue("vt")));
            } 

            if (cmd.hasOption("d")) {
            	config.setDownloadDir(cmd.getOptionValue("d"));
            }
            
            if (cmd.hasOption("df")) {
            	config.setKeepDownloadFile(Boolean.parseBoolean(cmd.getOptionValue("df")));
            }

            if (cmd.hasOption("fd")) {
            	config.setForceDownload(Boolean.parseBoolean(cmd.getOptionValue("fd")));
            }

            if (cmd.hasOption("cf")) {
            	config.setKeepConvertedFile(Boolean.parseBoolean(cmd.getOptionValue("cf")));
            }
            
            if (cmd.hasOption("u")) {
            	config.setImportUrl(cmd.getOptionValue("u"));
            }
            
            log.info("Starte OSM to JSON Converter ...");

            log.info(config.toString());
            OsmImporterServiceImpl service = new OsmImporterServiceImpl();
            service.importOsm(config);
            log.info("OSM File succesfully converted");

        } catch (Exception e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            if (e.getMessage() != null) {
                log.error(e.getMessage() + '\n');
            } else {
                log.error("Unknown error",e);
            }
            helpFormatter.printHelp("java -jar osm2graphium-0.1.0.one-jar.jar [OPTION]...", options);
        }
    }

}