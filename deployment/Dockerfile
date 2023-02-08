FROM registry.access.redhat.com/ubi8/openjdk-17:1.14

ENV LANGUAGE='en_US:en'

COPY --chown=185 build/libs/ /deployments/

EXPOSE 8080
USER 185

ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dspring.profiles.active=default"
ENV JAVA_APP_JAR="/deployments/spring-boot-reactive-rest-service-0.0.1-SNAPSHOT.jar"
