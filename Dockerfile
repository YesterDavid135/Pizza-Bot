FROM openjdk:18
MAINTAINER ydavid.ch
COPY target/Discord-Bot-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]