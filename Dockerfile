FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY /secret/* ./src/main/resource/
ENTRYPOINT ["java","-jar","/app.jar"]