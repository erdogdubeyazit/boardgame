FROM openjdk:8-jdk-alpine
WORKDIR /usr/src/app
EXPOSE 8080
COPY target/game-0.0.1-SNAPSHOT.jar ./app.jar
CMD ["java","-jar", "./app.jar"]
