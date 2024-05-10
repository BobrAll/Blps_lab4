FROM openjdk:17

COPY target/*.jar /java/app.jar
WORKDIR /java

ENTRYPOINT ["java", "-jar", "app.jar"]