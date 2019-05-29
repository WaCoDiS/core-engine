# Build dependencies
FROM alpine/git as clone
WORKDIR /app

RUN git clone https://github.com/WaCoDiS/wps-client-lib.git wps-client-lib \
	&& git -C ./wps-client-lib checkout feature/includeJobId

#BUILD
FROM maven:3.5-jdk-8-alpine as build 
WORKDIR /app

COPY --from=clone /app/wps-client-lib /app/wps-client-lib
COPY . /app/core-engine/

#RUN mvn -f /core-models/pom.xml -DskipTests -Pdownload-generate-models clean install
RUN mvn -f ./wps-client-lib/pom.xml clean install -DskipTests \
	&& mvn -f ./core-engine/pom.xml clean install -DskipTests
#RUN mvn -DskipTests -Pdownload-generate-models clean install 

#RUN
FROM openjdk:alpine
WORKDIR /app

COPY --from=build app/core-engine/core-engine-app/target/core-engine-app-0.0.1-SNAPSHOT.jar /app/core-engine-app.jar
EXPOSE 8000

CMD ["java", "-jar", "/app/core-engine-app.jar"]