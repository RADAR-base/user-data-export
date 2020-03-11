FROM openjdk:12 as builder

RUN mkdir /code
WORKDIR /code

ENV GRADLE_OPTS -Dorg.gradle.daemon=false
COPY gradle/wrapper /code/gradle/wrapper
COPY ./gradlew ./build.gradle.kts ./settings.gradle /code/
RUN ./gradlew --version

RUN ./gradlew downloadDependencies copyDependencies

COPY ./src /code/src

RUN ./gradlew -Dkotlin.compiler.execution.strategy="in-process" -Dorg.gradle.parallel=false -Pkotlin.incremental=false distTar \
    && cd build/distributions \
    && tar xf *.tar \
    && rm *.tar user-data-exporter-*/lib/user-data-exporter-*.jar

FROM openjdk:12

MAINTAINER @nivemaham

LABEL description="Data exported from Keycloak created by HOMEApp"

COPY --from=builder /code/build/distributions/user-data-exporter-*/bin/* /usr/bin/
COPY --from=builder /code/build/distributions/user-data-exporter-*/lib/* /usr/lib/
COPY --from=builder /code/build/libs/user-data-exporter-*.jar /usr/lib/

CMD ["user-data-exporter"]
