# Stage 1: Cache Gradle dependencies
FROM gradle:9.4.0-jdk21-corretto AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY . /home/gradle/app/
WORKDIR /home/gradle/app
RUN gradle :apps:api:server:dependencies --no-daemon

# Stage 2: Build Application
FROM gradle:9.4.0-jdk21-corretto AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :apps:api:server:installDist --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:21 AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/apps/api/server/build/install/server /app
ENTRYPOINT exec /app/bin/server
