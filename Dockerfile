FROM openjdk:18
MAINTAINER ydavid.ch
COPY target/Pizzabot-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]