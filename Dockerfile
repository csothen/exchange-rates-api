FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /opt/app

ARG JAR_FILE=target/rates_api-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

CMD ["java", "-jar", "app.jar"]