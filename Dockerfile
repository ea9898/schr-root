#
# Build stage
#
FROM maven:3.8.3-openjdk-17-slim AS build
COPY application/ /opt/src
COPY libs/ /opt/src/libs
COPY .git /opt/src
RUN apt-get update && apt-get install -y git
RUN mvn install:install-file -Dfile=/opt/src/libs/client-lib-4.5.1.jar -DgroupId=ru.mos.emias.esu -DartifactId=client-lib -Dversion=4.5.1 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-user-context-1.0.0.jar -DgroupId=ru.mos.emias.uc -DartifactId=emias-user-context -Dversion=1.0.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-common-domain-2.0.4.jar -DgroupId=ru.mos.emias.common -DartifactId=emias-common-domain -Dversion=2.0.4 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/opt/src/libs/emias-errors-1.1.0.jar -DgroupId=ru.mos.emias.errors -DartifactId=emias-errors -Dversion=1.1.0 -Dpackaging=jar
RUN mvn -f /opt/src/pom.xml validate clean install -DskipTests=true -e

#
# I_SCHR_1 - Получение сообщений из топика AttachmentEvent
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-attachmenteventimportservice
ARG JAR_FILE=schr-scheduler/esu-aei-listener-scheduler/aei-listener-application/target/esu-aei-listener-scheduler-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/esu-aei-listener-scheduler.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/esu-aei-listener-scheduler.jar"]

#
# I_SCHR_2 - Обработка сообщений топика AttachmentEvent
#

#
# I_SCHR_3 - Получение сообщений из топика ErpChangePatientPersonalData
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-erpchangepatientpersonaldataimportservice
ARG JAR_FILE=schr-scheduler/esu-ecppd-listener-scheduler/ecppd-listener-application/target/esu-ecppd-listener-scheduler-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/esu-ecppd-listener-scheduler.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/esu-ecppd-listener-scheduler.jar"]

#
# I_SCHR_4 - Обработка сообщений из топика ErpChangePatientPersonalData
#

#
# I_SCHR_5 - Получение сообщений из топика ErpChangePatientPolicies
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-erpchangepatientpoliciesimportservice
ARG JAR_FILE=schr-scheduler/erp-change-patient-policies-import-service/erp-change-patient-application/target/erp-change-patient-policies-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/erp-change-patient-policies.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/erp-change-patient-policies.jar"]

#
# I_SCHR_6 - Обработка сообщений топика ErpChangePatientPolicies
#

#
# I_SCHR_7 - Получение сообщений из топика LastAnthropometry
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-lastanthropometryimportservice
ARG JAR_FILE=schr-scheduler/last-anthropometry-import-service/last-anthropometry-application/target/last-anthropometry-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/last-anthropometry.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/last-anthropometry.jar"]

#
# I_SCHR_8 - Обработка сообщений топика LastAnthropometry
#

#
# I_SCHR_9 - Получение сообщений из топика PatientConsentsTopic
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-patientconsentstopicimportservice
ARG JAR_FILE=schr-scheduler/patient-consents-topic-import-service/patient-consents-topic-application/target/patient-consents-topic-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/patient-consents-topic.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/patient-consents-topic.jar"]

#
# I_SCHR_10 - Обработка сообщений топика PatientConsentsTopic
#

#
# I_SCHR_11 - Получение сообщений из топика SchoolAttachmentEvent
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-schoolattachmenteventimportservice
ARG JAR_FILE=schr-scheduler/esu-sae-listener-scheduler/sae-listener-application/target/esu-sae-listener-scheduler-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/esu-sae-listener-scheduler.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/esu-sae-listener-scheduler.jar"]

#
# I_SCHR_12 - Обработка сообщений топика SchoolAttachmentEvent
#

#
# Веб-сервис "Поиск и возвращение актуальных данных об учащихся и данных реестра по учащимся" (searchService)
#
FROM docker.artifactory.emias.mos.ru/emiasos-openjdk:17.0.4.1 as schregister-searchservice
ARG JAR_FILE=schr-service/schr-service-application/target/schr-service-*.jar
COPY --from=build /opt/src/${JAR_FILE} /opt/schr-service.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/schr-service.jar"]

