# Clone third-party dependencies from GitHub
FROM alpine/git as clone
WORKDIR /app

RUN echo invalidate the cache2

RUN git clone https://github.com/WaCoDiS/wps-client-lib.git wps-client-lib \
	&& git -C ./wps-client-lib checkout 1.0.8-wacodis && git -C ./wps-client-lib log -1
RUN ls -la ./wps-client-lib/src/main/resources
RUN cat ./wps-client-lib/src/main/resources/wps-client-default.properties
RUN cat ./wps-client-lib/src/main/java/org/n52/geoprocessing/wps/client/WPSClientSession.java

# Build core-engine
FROM maven:3.5-jdk-8-alpine as build 
WORKDIR /app

COPY --from=clone /app/wps-client-lib /app/wps-client-lib

# only retrieve deps
COPY ./pom.xml /app/core-engine/pom.xml
COPY ./job-scheduling/pom.xml /app/core-engine/job-scheduling/
COPY ./job-definition-evaluator/pom.xml /app/core-engine/job-definition-evaluator/
COPY ./job-definition-executor/pom.xml /app/core-engine/job-definition-executor/
COPY ./core-models/pom.xml /app/core-engine/core-models/
COPY ./core-engine-app/pom.xml /app/core-engine/core-engine-app/
COPY ./core-engine-utils/pom.xml /app/core-engine/core-engine-utils/
RUN mvn -f /app/core-engine/pom.xml dependency:go-offline --non-recursive
RUN mvn -f /app/wps-client-lib/pom.xml dependency:go-offline

COPY . /app/core-engine/

RUN mvn -f ./wps-client-lib/pom.xml clean install -DskipTests \
	&& mvn -f ./core-engine/pom.xml clean install -DskipTests -Dapp.finalName=core-engine-app

# RUn core-engine
FROM adoptopenjdk/openjdk8:alpine
WORKDIR /app

COPY --from=build app/core-engine/core-engine-app/target/core-engine-app.jar /app/core-engine-app.jar
CMD ["java", "-jar", "/app/core-engine-app.jar"]
