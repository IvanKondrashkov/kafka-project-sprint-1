FROM amazoncorretto:21-alpine-jdk
COPY build/libs/*-all.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 9090