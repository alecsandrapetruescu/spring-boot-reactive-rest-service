FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY application/dependencies/ ./
COPY application/spring-boot-loader/ ./
COPY application/snapshot-dependencies/ ./
#COPY application/resources/ ./
COPY application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]