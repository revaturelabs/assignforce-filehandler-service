FROM maven:3.6.1-jdk-8
VOLUME /tmp
ARG JAR_FILE
ARG CONFIG_URL
ARG PROFILE
ARG LABEL

ENV CONFIG_URL=$CONFIG_URL
ENV spring_profiles_active=$PROFILE
ENV PROFILE=$PROFILE
ENV LABEL=$LABEL
COPY src/main/resources/ojdbc7.jar .
RUN mvn install:install-file -Dfile=ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0 -Dpackaging=jar
EXPOSE 8080
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-jar", "/app.jar"]
