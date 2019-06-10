# Initial Setup
FROM maven:3.6-jdk-8-alpine as setup

## Setup Maven Authentication
RUN mkdir -p /root/.m2

## Pre-fetch Dependencies
WORKDIR /prefetch
COPY ./pom.xml /prefetch/pom.xml
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
RUN mvn -B dependency:resolve -q

# Build Project
FROM maven:3.6-jdk-8-alpine as build

RUN apk add --update git perl

WORKDIR /build
COPY . /build
## Grab Pre-fetched Dependencies
COPY --from=setup /root/.m2/repository /root/.m2/repository
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
RUN mvn -B clean fmt:check -q
RUN mvn -B clean verify
RUN mv ./target/*.jar ./target/eva-client-java.jar
ARG BUILD_ARTIFACTS_JAVA=/build/target/eva-client-java.jar

# Dependency Artifacts
RUN ./scripts/ci/dependencies.sh
RUN mkdir -p /audit
RUN cp ./MANIFEST.yml /audit/MANIFEST.yml
RUN cp ./pom.xml /audit/pom.xml
ARG BUILD_ARTIFACTS_AUDIT=/audit/*

## Upload Code-Coverage Report
ARG GIT_COMMIT
ARG GIT_BRANCH
RUN ./scripts/ci/codecov.sh

# Prepare Final Image
FROM scratch
