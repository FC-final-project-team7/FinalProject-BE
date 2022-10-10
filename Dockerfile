FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY /secret/* /var/jenkins_home/workspace/AIJenkins/src/main/resources/
ENTRYPOINT ["java","-jar","/app.jar"]