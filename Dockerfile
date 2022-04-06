FROM gradle:7.3-jdk17 as builder

RUN mkdir /code
WORKDIR /code

ENV GRADLE_USER_HOME=/code/.gradlecache

COPY build.gradle.kts settings.gradle.kts gradle.properties /code/

RUN gradle prepareDockerEnvironment --no-watch-fs

COPY ./src /code/src

RUN gradle jar --no-watch-fs

FROM azul/zulu-openjdk-alpine:17.0.2-jre-headless

MAINTAINER @nivemaham

LABEL description="Data exported from Keycloak created by HOMEApp"

RUN apk add --no-cache curl

COPY --from=builder /code/build/third-party/* /usr/lib/
COPY --from=builder /code/build/scripts/* /usr/bin/
COPY --from=builder /code/build/libs/* /usr/lib/

CMD ["user-data-manager"]
