FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
EXPOSE 8675
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.egd=gile:/dev/urandom", "-jar", "/app.jar"]