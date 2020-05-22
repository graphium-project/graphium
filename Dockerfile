FROM maven:3.5.3-jdk-8 as builder
# install openjfx
RUN apt-get update \
    && apt-get install --no-install-recommends -y openjfx \
    && apt-get clean \
    && rm -f /var/lib/apt/lists/*_dists_*
COPY . /usr/src/graphium/
RUN mvn -f /usr/src/graphium/pom.xml clean package -DskipTests

FROM tomcat:9-jdk8
RUN rm -rf $CATALINA_HOME/webapps/ROOT
COPY --from=builder /usr/src/graphium/converters/osm2graphium/target/osm2graphium_1.1.0-SNAPSHOT.one-jar.jar /osm2graphium.jar
COPY --from=builder /usr/src/graphium/tutorial/central_server/target/graphium-tutorial-central-server-1.1.0-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war
EXPOSE 8080