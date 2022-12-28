#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY application/ /opt/src
COPY libs/ /opt/src/libs
COPY .git /opt/src
RUN apt-get update && apt-get install -y git
RUN mvn install:install-file -Dfile=/opt/src/libs/client-lib-4.5.1.jar -DgroupId=ru.mos.emias.esu -DartifactId=client-lib -Dversion=4.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-user-context-1.0.0.jar -DgroupId=ru.mos.emias.uc -DartifactId=emias-user-context -Dversion=1.0.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-common-domain-2.0.4.jar -DgroupId=ru.mos.emias.common -DartifactId=emias-common-domain -Dversion=2.0.4 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-errors-1.1.0.jar -DgroupId=ru.mos.emias.errors -DartifactId=emias-errors -Dversion=1.1.0 -Dpackaging=jar
RUN mvn -f /opt/src/pom.xml validate clean install -DskipTests=true -e -T 2

#
# Package stage НСИ
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:11.0.7 as schregister-service
ARG JAR_FILE=schr-service/schr-service-application/target/schr-service-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/schr-service.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/schr-service.jar"]
