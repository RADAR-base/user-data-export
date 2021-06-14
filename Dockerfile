FROM gradle:7.0-jdk11 as builder

RUN mkdir /code
WORKDIR /code

ENV GRADLE_USER_HOME=/code/.gradlecache

COPY gradlew ./build.gradle.kts ./settings.gradle.kts /code/

RUN gradle downloadDependencies copyDependencies --no-watch-fs

COPY ./src /code/src

RUN gradle distTar --no-watch-fs \
    && cd build/distributions \
    && tar xf *.tar \
    && rm *.tar user-data-manager-*/lib/user-data-manager-*.jar

FROM openjdk:11-jre-slim

MAINTAINER @nivemaham

LABEL description="Data exported from Keycloak created by HOMEApp"

RUN apt-get update && apt-get install -y \
  curl \
  && rm -rf /var/lib/apt/lists/*

COPY --from=builder /code/build/distributions/user-data-manager-*/bin/* /usr/bin/
COPY --from=builder /code/build/distributions/user-data-manager-*/lib/* /usr/lib/
COPY --from=builder /code/build/libs/user-data-manager-*.jar /usr/lib/

CMD ["user-data-manager"]
