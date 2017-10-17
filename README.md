<p align="right">
<img src="doc/img/Graphium_225x130px.svg">
</p>

# Graphium

Graphium is an Open Source project to store, manage and version transport graphs. Graph data can be stored from several data providers. With the REST-API imports and management is easy. Graphium is designed for deployment in distributed systems for centralized management and publishing to several servers.

## Features

### Import transport graphs from different sources

There are several providers of transport graph data. Each of them uses its own data format. Using those transport graph data is easy with Graphium. Either use an existing converter or implement an custom one to convert transport graph data into a JSON file. Currently there are converters for OpenStreetMap and GIP (Graph Integration Platform - a joint, nationwide transport graph of Austria) available. The transport graph data is imported into the Graphium server using the JSON file. The Extended Information data model allows to import additional information to the basic transport graph.

<p align="center">
<img src="doc/img/import_from_different_sources.png">
</p>

### Uniform and expandable data model to fulfill specific use cases

After you have imported the transport graphs, you can access and process the data through its uniform data model. You can also enhance transport graphs and handle additional information.

<p align="center">
<img src="doc/img/uniform_model.png">
</p>

### Manage time restricted versions

Transport networks and their abstract mapping into digital transport graphs evolve over time. Graphium can import and manage several time restricted versions of one graph. This feature makes it possible to process data for real-time tasks as well as historic data processing.

<p align="center">
<img src="doc/img/versions.png">
</p>

### Define Views

One major design decision was to keep one (or more) graph version(s) including all kind of data needed and set up various views for data only needed for special use cases. This approach is similar to database views. Hence it is possible to import the whole data of a graph and extract only way segment used by car or by bike using views.

<p align="center">
<img src="doc/img/views.png">
</p>

### Publish transport graphs to other servers

Graphium is designed for use in distributed systems. If data processing based on graph data has to be parallalized using a number of machines, graph data has to be distributed and persisted on them. With Graphium it is possible to define one central Graphium server for administration of used graph versions. On worker machines clients - so called satellite Graphium servers - can deployed and subscribe on the central Graphium server for one or more specific views on graph versions. The publishing of graph versions will be done on central Graphium server's side and guarantees a consistent state over all subscribers of one view.

<p align="center">
<img src="doc/img/publishing.png">
</p>


## Concepts

### Data Management & Expandability

For data management transport graph data has to be persisted. By default, Graphium uses PostgreSQL, but because of the interchangable database tier other DBMS could be used, too.

The base network model represents a directed graph consisting of waysegments and their connections. In PostgreSQL the entity *waysegments* stores data of graph segments (the linestring between two crossings) in a directed way, the segment entries itself are not directed. The entity *waysegment_connections* stores directed data for each connection between two joined segments. Both entities store an array of access types (in case of *waysegments* for each direction) representing the allowed usage for different vehicle types.

The base model is easy to expand if storage of additional data is needed. The entity *waysegments* is able to store tags as key-value-pairs. Alternatively, the entity *xinfo* (short for extended information) can be used. This entity defines the basic structure to reference waysegments and can be inherited to model your specific needs.

*PostgreSQL DB Scheme:*

<p align="center">
<img src="doc/img/waysegments_model.png">
</p>

### Views

The concept of views in a DBMS means to filter only relevant data needed for further processing. Graphium uses the same concept to allow different views on transport graphs. For example a fine granulated tranport graph is stored in database but for one specific analysis only cycle ways are necessary. Then you have to define a custom view returning only waysegments having the access type "BIKE".

To access those views over the API the graph name can be replaced with each view name. So graph name and view name will be treated similarly at the API.

Find out how to define [custom views](doc/CustomViews.md).

### Publish / Subscribe

Graphium is designed for use as a server in standalone mode or for deployment in distributed systems. The main use case in distributed systems is to deploy one Graphium central server storing all transport graph data needed within the whole system. One or more worker servers, so called Graphium satellite server, could be deployed to process data based on specific transport graph data. This architecture supports horizontal scaling of Graphium satellite servers. All Graphium satellite servers will be automatically informed by the Graphium central server as soon as a new graph version is available to always process consistently on the newest transport graph version.

Detailed information about the publish / subscribe process can be found [here](doc/PublishSubscribe.md).

## API

[API Reference Documentation](doc/apiReferenceDocumentation.md)

## Graph Data Conversion

To import transport graph data into Graphium data has to be converted into Graphium's input format (JSON). Data converters already exist for OSM or GIP, custom converters have to be implemented for other data sources.

### OSM

Example API call to generate a JSON file from OSM data:

`java osm2graphium_1.0.0.one-jar -i /path/to/osm-at-latest.osm.pbf -o /path/to/output -n osm_at -v 170701 -q 20000 -t 5 –highwayTypes "motorway, motorway_link, primary, primary_link"`

| short option | long option    | description                              |
| :----------- | :------------- | ---------------------------------------- |
| -h           | --help         | display this help page                   |
| -i           | --input        | path to PBF File                         |
| -o           | --output       | path to result directory. (default: user.home) |
| -n           | --name         | Name of the graph to be imported         |
| -v           | --version      | Version of the graph to be imported      |
| -vf          | --valid-from   | start timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm') |
| -vt          | --valid-to     | end timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm') |
| -b           | --bounds       | Name of bounds file for geographical filtering (format alá Osmosis) |
| -q           | --queueSize    | Size of import queue                     |
| -t           | --threads      | Number of worker threads                 |
|              | --highwayTypes | Comma separated List of highway types, to be considered. If not set, all highway types will be considered |

### GIP

Example API call to generate a JSON file from GIP data:

`java idf2graphium_1.0.0.one-jar -i /path/to/gip-at.txt -o /path/to/output -n gip_at_frc_0_8 -v 16_02_161111 --skip-pixel-cut -import-frcs "0,1,2,3,4,5,6,7,8"`

| short option | long option                   | Beschreibung                             |
| ------------ | ----------------------------- | ---------------------------------------- |
| -h           | --help                        | display this help page                   |
| -i           | --input                       | path to IDF or compressed IDF File (ZIP) |
| -o           | --output                      | path to result directory. (default: user.home) |
| -n           | --name                        | Name of the graph to be imported         |
| -v           | --version                     | Version of the graph to be imported      |
| -vf          | --valid-from                  | start timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm') |
| -vt          | --valid-to                    | end timestamp of graph version's validity (format 'yyyy-MM-dd HH:mm') |
|              | --skip-gip-import             | skip the import process of the GIP, only pixel cuts will be generated. The options -o and --import-frcs are ignored |
|              | –skip-pixel-cut               | skip the calculation of the turn offset factors. The options -m and -M are ignored |
| -m           | --pixel-cut-min-frc           | minimum frc value to be considered for offset calculations |
| -M           | --pixel-cut-max-frc           | maximum frc value to be considered for offset calculations |
|              | -import-frcs                  | Comma separated List of FRC values to be included for IDF import. If not set (default) all frc values are considered |
|              | -access-types                 | Comma separated List of Access Types, to be considered. If not set, all access types will be considered |
| -e           | --pixel-cut-enable-short-conn | By default short connections below 3.5 meter with frc 0 are ignored. This is to filter the connections between highways and streets. If this option is set all gip links are considered |

## Quickstart

1. Building

   `mvn clean install`

2. Install PostgreSQL: https://www.postgresql.org/download/

3. Create Database (using *psql*)

   ```sql
   CREATE ROLE graphium LOGIN ENCRYPTED PASSWORD 'md5e85d3e0c9e3a933a0c9103b21ed017df'
      VALID UNTIL 'infinity';

   CREATE DATABASE graphium_central WITH OWNER = graphium;

   \i /path/to/graphium-postgis/db/composite/create_graphium_database.sql
   ```

4. Install Apache Tomcat: http://tomcat.apache.org/

5. Deploy tutorial's Graphium central server (*graphium-tutorial-central-server-1.0.0.war*) on Apache Tomcat and start

6. Download OSM File:

   ```
   curl http://download.geofabrik.de/europe/austria-latest.osm.pbf -o /data/osm/austria-latest.osm.pbf
   ```

7. Convert OSM File into Graphium's JSON format:

   ```
   java -jar osm2graphium_1.0.0.one-jar -i /data/osm/austria-latest.osm.pbf -o /path/to/output -n osm_at -v 170929 -q 20000 -t 5 -highwayTypes "motorway, motorway_link, primary, primary_link"
   ```

8. Import OSM into Graphium central server

   ```
   curl -X POST "http://localhost:8080/graphium-tutorial-central-server-1.0.0/api/segments/graphs/osm_at/versions/170929?overrideIfExists=true" -F "file=@/path/to/output/osm_at.json"
   ```

## Tutorials

### How to define custom views

Look at [custom views](doc/CustomViews.md).

### How to extend Graphium

Look at the tutorial package.

## Dependencies

- Jackson-annotations, Apache License, Version 2.0 (http://github.com/FasterXML/jackson)
- Jackson-core, Apache License, Version 2.0 (https://github.com/FasterXML/jackson-core)
- jackson-databind, Apache License, Version 2.0 (http://github.com/FasterXML/jackson)
- JTS Topology Suite, LGPL (http://sourceforge.net/projects/jts-topo-suite)
- Apache Commons Codec, Apache License, Version 2.0 (http://commons.apache.org/proper/commons-codec/)
- Commons Logging, Apache License, Version 2.0 (http://commons.apache.org/logging)
- JUnit, Eclipse Public License 1.0 (http://junit.org)
- Apache Extras™ for Apache log4j™, Apache License, Version 2.0 (http://logging.apache.org/log4j/extras)
- Apache Log4j, Apache License, Version 2.0 (http://logging.apache.org/log4j/1.2/)
- Apache Commons Lang, Apache License, Version 2.0 (http://commons.apache.org/proper/commons-lang/)
- Apache HttpClient, Apache License, Version 2.0 (http://hc.apache.org/httpcomponents-client)
- Apache HttpCore, Apache License, Version 2.0 (http://hc.apache.org/httpcomponents-core-ga)
- Jackson, Apache License, Version 2.0(http://jackson.codehaus.org)
- Data Mapper for Jackson, Apache License, Version 2.0 (http://jackson.codehaus.org)
- Hamcrest Core, New BSD License (https://github.com/hamcrest/JavaHamcrest/hamcrest-core)
- JCL 1.1.1 implemented over SLF4J, MIT License (http://www.slf4j.org)
- SLF4J API Module, MIT License (http://www.slf4j.org)
- SLF4J LOG4J-12 Binding, MIT License (http://www.slf4j.org)
- Spring AOP, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Beans, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Context, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Core, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Expression Language (SpEL), Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Transaction, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Web, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
- Spring Web MVC, Apache License, Version 2.0 (https://github.com/spring-projects/spring-framework)
