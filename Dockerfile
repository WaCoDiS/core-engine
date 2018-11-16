#BUILD
FROM maven:3.5.4-jdk-8-alpine as BUILD

COPY . /

#RUN mvn -f /core-models/pom.xml -DskipTests -Pdownload-generate-models clean install
RUN mvn -DskipTests -Pdownload-generate-models clean install 


#RUN
FROM openjdk:alpine

RUN mkdir /app
COPY --from=BUILD /core-engine-app/target/core-engine-app-0.0.1-SNAPSHOT.jar /app/core-engine-app.jar
EXPOSE 5672

CMD ["java", "-jar", "/app/core-engine-app.jar"]