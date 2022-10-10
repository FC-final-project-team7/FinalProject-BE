FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.config.location=classpath:/application.yml,/secret/application-aws.yml,/secret/application-db.yml,/secret/application-email.yml,/secret/application-jwt.yml,/secret/application-oauth.yml","-jar","/app.jar"]