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

To deploy, push the image to a container registry:

```shell
./sigo api:server docker build --tag sigo-api
docker tag sigo-api registry.example.com/sigo-api:latest
docker push registry.example.com/sigo-api:latest
```

The image exposes port 8080. For hosts that accept a Dockerfile directly (Fly.io, Railway, Cloud
Run, ECS, etc.), point them at `apps/api/server/Dockerfile`. The build context must be the project
root since Gradle needs access to the full source tree.

Minimal `docker-compose.yml`:

```yaml
services:
  sigo-api:
    image: sigo-api
    ports:
      - "8080:8080"
    environment:
      FORECAST_API_KEY: ${FORECAST_API_KEY}
      LOG_LEVEL: WARN
```

## Standalone

For environments without Docker, the Gradle `installDist` task produces a self-contained
distribution with a startup script. Requires JVM 21+ on the host.

Build the distribution:

```shell
./sigo api:server build
```

This outputs to `apps/api/server/build/install/server/`. The directory contains a `bin/server`
script and a `lib/` folder with all required JARs.

Run the built distribution:

```shell
./sigo api:server run
```

Override the port:

```shell
./sigo api:server run --port 9090
```

If the distribution has not been built yet, `run` will build it automatically.

Or run it directly:

```shell
FORECAST_API_KEY=<your-key> apps/api/server/build/install/server/bin/server
```

To deploy the standalone distribution to a remote host, copy the entire `build/install/server/`
directory and run `bin/server`. Set environment variables (`FORECAST_API_KEY`, `PORT`, etc.) as
needed on the host. A clean build with `--clean` ensures no stale artifacts:

```shell
./sigo api:server build --clean
```

## Logging

Configured via logback (`apps/api/server/src/main/resources/logback.xml`). Uses an async console
appender. Control log levels at runtime with the `LOG_LEVEL` and `APP_LOG_LEVEL` environment
variables.

## Endpoints

The server mounts the shared API router from `core:api:server`:

```
GET /forecast?lat=<lat>&lon=<lon>
```

### Required Headers

Every request must include an `X-Client-ID` header containing a valid UUID. The server returns
`400 Bad Request` if the header is missing or not a valid UUID.

```
X-Client-ID: 550e8400-e29b-41d4-a716-446655440000
```

The mobile and desktop apps generate and persist a client ID automatically via `ClientIdProvider`.
If you are calling the API directly (e.g., with `curl`), you need to supply one yourself.

The client ID is used for per-client rate limiting. The rate limiter tracks request counts by both
client ID and IP address.
