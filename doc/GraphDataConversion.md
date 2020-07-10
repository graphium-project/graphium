# Graph Data Conversion

To import transport graph data into Graphium, data has to be converted into Graphium's input format (JSON). Data converters already exist for OSM or GIP, custom converters have to be implemented for other data sources.

## OSM

Example API call to generate a JSON file from OSM data:

`java osm2graphium.one-jar.jar -i /path/to/osm-andorra-latest.osm.pbf -o /path/to/output -n osm_at -v 200603 -q 20000 -t 5 –highwayTypes "motorway, motorway_link, primary, primary_link"`

Example API call to generate a JSON file from OSM data and import into a Graphium server:

`java osm2graphium.one-jar.jar -i /path/to/osm-andorra-latest.osm.pbf -o /path/to/output -n osm_at -v 200603 -q 20000 -t 5 –highwayTypes "motorway, motorway_link, primary, primary_link" -u "http://localhost:8080/graphium/api/segments/graphs/osm_andorra/versions/200603?overrideIfExists=true"`

Example API call to download a OSM file, generate a JSON file and import into a Graphium server:

`java osm2graphium.one-jar.jar -i http://download.geofabrik.de/europe/andorra-latest.osm.pbf -o /path/to/output -n osm_at -v 200603 -q 20000 -t 5 –highwayTypes "motorway, motorway_link, primary, primary_link" -u "http://localhost:8080/graphium/api/segments/graphs/osm_andorra/versions/200603?overrideIfExists=true"`

| short option | long option    | description                              |
| :----------- | :------------- | ---------------------------------------- |
| -h           | --help         | display this help page                   |
| -i           | --input        | path or URL to PBF File                 |
| -o           | --output       | path to result directory. (default: user.home) |
| -n           | --name         | Name of the graph to be imported         |
| -v           | --version      | Version of the graph to be imported      |
| -vf          | --valid-from   | start timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm'); optional |
| -vt          | --valid-to     | end timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm'); optional |
| -b           | --bounds       | Name of bounds file for geographical filtering (format alá Osmosis); optional |
| -q           | --queueSize    | Size of import queue; optional           |
| -t           | --threads      | Number of worker threads; optional       |
|              | --highwayTypes | Comma separated List of highway types, to be considered. If not set, all highway types will be considered; optional |
| -T		   | --tags			| mode how osm tags of ways are stored on created segments, allowed modes 'none','all', defaults to 'none'; optional |
| -d	| --downloadDir	| Directory to store download files; optional. If this parameter is not present download files will be stored in output directory |
| -df	| --keepDownloadFile	| 'false': downloaded file from URL will be deleted afterwards / 'true' will not be deleted; default = true |
| -fd	| --forceDownload	| 'false': if file exists in download directory this one will be used / 'true': download file from URL even if it has been already downloaded; default = false |
| -cf	| --keepConvertedFile	| 'false': converted file will be deleted afterwards / 'true': converted file will not be deleted; default = true |
| -u	| --importUrl	| Server URL to import the converted graph file; optional. |

## GIP

Example API call to generate a JSON file from GIP data:

`java idf2graphium.one-jar.jar -i /path/to/gip-at.txt -o /path/to/output -n gip_at_frc_0_8 -v 20_04_200603 --skip-pixel-cut -import-frcs "0,1,2,3,4,5,6,7,8"`

Example API call to generate a JSON file from GIP data and import into a Graphium server:

`java idf2graphium.one-jar.jar -i /path/to/gip-at.txt -o /path/to/output -n gip_at_frc_0_8 -v 20_04_200603 --skip-pixel-cut -import-frcs "0,1,2,3,4,5,6,7,8" -u "http://localhost:8080/graphium/api/segments/graphs/gip_at/versions/200603?overrideIfExists=true"`

Example API call to download a GIP file, generate a JSON file and import into a Graphium server:

`java idf2graphium.one-jar.jar -i http://open.gip.gv.at/ogd/A_routingexport_ogd.zip -o /path/to/output -n gip_at_frc_0_8 -v 20_04_200603 --skip-pixel-cut -import-frcs "0,1,2,3,4,5,6,7,8" -u "http://localhost:8080/graphium/api/segments/graphs/osm_andorra/versions/200603?overrideIfExists=true"`

| short option | long option                   | Beschreibung                                                 |
| ------------ | ----------------------------- | ------------------------------------------------------------ |
| -h           | --help                        | display this help page                                       |
| -i           | --input                       | path to IDF or compressed IDF File (ZIP)                     |
| -o           | --output                      | path to result directory. (default: user.home)               |
| -n           | --name                        | Name of the graph to be imported                             |
| -v           | --version                     | Version of the graph to be imported                          |
| -vf          | --valid-from                  | start timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm'); optional |
| -vt          | --valid-to                    | end timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm'); optional |
|              | --skip-gip-import             | skip the import process of the GIP, only pixel cuts will be generated. The options -o and --import-frcs are ignored; optional |
|              | --skip-pixel-cut            | skip the calculation of the turn offset factors. The options -m and -M are ignored; optional |
| -m           | --pixel-cut-min-frc           | minimum frc value to be considered for offset calculations; optional |
| -M           | --pixel-cut-max-frc           | maximum frc value to be considered for offset calculations; optional |
|              | --import-frcs                 | Comma separated List of FRC values to be included for IDF import. If not set (default) all frc values are considered; optional |
|              | --access-types                | Comma separated List of Access Types, to be considered. If not set, all access types will be considered; optional |
| -e           | --pixel-cut-enable-short-conn | By default short connections below 3.5 meter with frc 0 are ignored. This is to filter the connections between highways and streets. If this option is set all gip links are considered; optional |
| -fc            | --full-connectivity | Creates a full connected network ignoring one ways (default = false) |
|  | --xinfo-csv | Defines optional CSV file to convert into XInfo object; pattern: <FILENAME>=<XInfoFactoryClass><br />Therefore a jar is needed containing all necessary classes for creating and processing the custom XInfo objects (see graphium-pixelcuts). This jar has to linked via `java -cp executable.jar;libs/*;. at.srfg....IDFConverter`. The <XInfoFactoryClass> has to be a class implementing the interface ICsvXInfoFactory. All XInfo objects will be written to the output JSON file as xInfo array entries. Optional |
| -d	| --downloadDir	| Directory to store download files; optional. If this parameter is not present download files will be stored in output directory. |
| -df	| --keepDownloadFile	| 'false': downloaded file from URL will be deleted afterwards / 'true' will not be deleted; default = true |
| -fd	| --forceDownload	| 'false': if file exists in download directory this one will be used / 'true': download file from URL even if it has been already downloaded; default = false |
| -cf	| --keepConvertedFile	| 'false': converted file will be deleted afterwards / 'true': converted file will not be deleted; default = true |
| -u	| --importUrl	| Server URL to import the converted graph file; optional |