FROM frolvlad/alpine-java:jdk8-slim AS builder

COPY build/libs/users-0.0.1-SNAPSHOT.jar /opt/app/
RUN echo $(ls -1 /opt/app/)
CMD ["/usr/bin/java", "-jar", "/opt/app/users-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080