FROM openjdk:12 as builder

RUN mkdir /code
WORKDIR /code

ENV GRADLE_OPTS -Dorg.gradle.daemon=false
COPY gradle/wrapper /code/gradle/wrapper
COPY ./gradlew ./build.gradle.kts ./settings.gradle /code/
RUN ./gradlew --version

RUN ./gradlew downloadDependencies copyDependencies

COPY ./src /code/src

RUN ./gradlew jar

FROM openjdk:12

MAINTAINER @nivemaham

LABEL description="Data exported from Keycloak created by HOMEApp"

COPY --from=builder /code/build/third-party/* /usr/lib/
COPY --from=builder /code/build/libs/* /usr/lib/

CMD ["user-data-exporter"]
