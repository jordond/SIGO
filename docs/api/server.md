# JVM Server API

Located in the [`./apps/api/server`](../../apps/api/server) directory.

Standalone Ktor/Netty server that proxies forecast requests to the Visual Crossing API. Runs on
JVM 21 and is containerized with Docker.

## API Key

The server resolves `FORECAST_API_KEY` in order:

1. `FORECAST_API_KEY` environment variable at runtime (recommended for Docker/production)
2. Baked in at compile time from `app-env.properties` in the project root

If you've already run `./sigo init`, the key in `app-env.properties` is picked up automatically at
build time and no environment variable is needed for local development.

## Running Locally

If your key is in `app-env.properties`:

```shell
./sigo api:server dev
```

Override the port:

```shell
./sigo api:server dev --port 9090
```

Or run directly via Gradle:

```shell
FORECAST_API_KEY=<your-key> ./gradlew :apps:api:server:run
```

## Environment Variables

| Variable           | Required | Default    | Description                    |
|--------------------|----------|------------|--------------------------------|
| `FORECAST_API_KEY` | No\*     | from build | Visual Crossing API key        |
| `PORT`             | No       | `8080`     | Server listen port             |
| `LOG_LEVEL`        | No       | `INFO`     | Root log level (logback)       |
| `APP_LOG_LEVEL`    | No       | `INFO`     | Application-specific log level |

\* Required at runtime if not set in `app-env.properties` at build time.

## Docker

Build and run via the wrapper:

```shell
./sigo api:server docker build
./sigo api:server docker run
```

Or directly:

```shell
docker build -f apps/api/server/Dockerfile -t sigo-api .
docker run -p 8080:8080 -e FORECAST_API_KEY=<your-key> sigo-api
```

If the key was in `app-env.properties` when the image was built, the `-e` flag can be omitted.

The Dockerfile uses a multi-stage build:

1. **Cache**: downloads Gradle dependencies
2. **Build**: compiles and produces the `installDist` binary distribution
3. **Runtime**: Amazon Corretto 21 minimal image

## Logging

Configured via logback (`apps/api/server/src/main/resources/logback.xml`). Uses an async console
appender. Control log levels at runtime with the `LOG_LEVEL` and `APP_LOG_LEVEL` environment
variables.

## Endpoints

The server mounts the shared API router from `core:api:server`:

```
GET /forecast?lat=<lat>&lon=<lon>
```
