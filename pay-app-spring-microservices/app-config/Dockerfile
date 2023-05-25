FROM openjdk:13-alpine as stage1
COPY . /app
WORKDIR /app
RUN chmod 777 gradlew
RUN ./gradlew clean
RUN ./gradlew bootJar


FROM openjdk:13-alpine
EXPOSE 8010
RUN mkdir /home/logs
#COPY logging.properties /logs/logging.properties
#RUN touch /logs/app-config.log
#RUN chmod 777 /logs/app-config.log
COPY --from=stage1 /app/build/libs/app-config-1.0.0.jar app-config.jar
#ADD ./build/libs/app-config-1.0.0.jar app-config.jar
#ENTRYPOINT ["java", "-jar", "/app-config.jar", "--logging.file=/logs/app-config.log"]
ENTRYPOINT ["java", "-jar", "/app-config.jar"]
