ARG JAVA_VERSION=11
FROM openjdk:$JAVA_VERSION
EXPOSE 8085
ADD target/parking-spot-system-kotlin-0.0.1-SNAPSHOT.jar parking-spot-system-kotlin.jar

LABEL maintainer="GB_kotlin"
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar", "parking-spot-system-kotlin.jar"]
