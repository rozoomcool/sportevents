FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts /app/
COPY gradle /app/gradle

RUN gradle dependencies --no-daemon

COPY src /app/src

RUN gradle clean build -x test --no-daemon

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8000
EXPOSE 4444
ENTRYPOINT ["java", "-jar", "/app/app.jar"]