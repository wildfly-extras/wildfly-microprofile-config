FROM jboss/base-jdk:8

ADD target/microprofile-config-openshift-example-1.0-SNAPSHOT-swarm.jar /opt/wildfly-swarm.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/wildfly-swarm.jar"]
