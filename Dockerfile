# Clone third-party dependencies from GitHub
FROM alpine/git as clone
WORKDIR /app

RUN git clone https://github.com/WaCoDiS/wps-client-lib.git wps-client-lib \
	&& git -C ./wps-client-lib checkout feature/includeJobId

# Build core-engine
FROM maven:3.5-jdk-8-alpine as build 
WORKDIR /app

COPY --from=clone /app/wps-client-lib /app/wps-client-lib
COPY . /app/core-engine/

RUN mvn -f ./wps-client-lib/pom.xml clean install -DskipTests \
	&& mvn -f ./core-engine/pom.xml clean install -DskipTests

# RUn core-engine
FROM openjdk:alpine
WORKDIR /app

COPY --from=build app/core-engine/core-engine-app/target/core-engine-app-0.0.1-SNAPSHOT.jar /app/core-engine-app.jar
CMD ["java", "-jar", "/app/core-engine-app.jar"]