FROM eclipse-temurin:17-jre-alpine
ARG JAVA_OPTS
ADD target/account-0.0.1-SNAPSHOT.jar /var/acc/acc.jar
WORKDIR /var/acc
ENTRYPOINT exec java $JAVA_OPTS -jar acc.jar
