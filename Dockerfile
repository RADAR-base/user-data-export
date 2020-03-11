FROM openjdk:12 as builder

RUN mkdir /code
WORKDIR /code

ENV GRADLE_OPTS -Dorg.gradle.daemon=false
COPY gradle/wrapper /code/gradle/wrapper
COPY ./gradlew ./build.gradle.kts ./settings.gradle /code/
RUN ./gradlew --version

RUN ./gradlew downloadDependencies copyDependencies

