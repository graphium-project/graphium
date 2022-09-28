FROM maven:3.5.3-jdk-8 as builder
# install openjfx
RUN apt-get update \
    #&& apt-get install --no-install-recommends -y openjfx \
    && apt-get clean \
    && rm -f /var/lib/apt/lists/*_dists_*
COPY . /usr/src/graphium/
RUN mvn -f /usr/src/graphium/pom.xml clean package -DskipTests

RUN ls -la /usr/src/graphium/tutorial/central_server/target/

FROM tomcat:9-jdk8

# copy database initialisation scripts
RUN mkdir /db-init/
COPY --from=builder /usr/src/graphium/postgis/db/composite/create_graphium_database.sql /db-init/create_graphium_database.sql
COPY --from=builder /usr/src/graphium/postgis/db/composite/postgis.sh /db-init/postgis.sh

RUN rm -rf $CATALINA_HOME/webapps/ROOT

COPY --from=builder /usr/src/graphium/converters/osm2graphium/target/osm2graphium.one-jar.jar /osm2graphium.one-jar.jar
COPY --from=builder /usr/src/graphium/converters/idf2graphium/target/idf2graphium.one-jar.jar /idf2graphium.one-jar.jar
COPY --from=builder /usr/src/graphium/tutorial/central_server/target/graphium*.war $CATALINA_HOME/webapps/graphium.war

EXPOSE 8080