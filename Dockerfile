FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.config.location=classpath:/application.yml,/secret/","-jar","/app.jar"]