FROM openjdk:17
LABEL authors="Bobrus Alexander"

COPY target/*.jar /java/app.jar
WORKDIR /java

ENTRYPOINT ["java", "-jar", "app.jar"]
