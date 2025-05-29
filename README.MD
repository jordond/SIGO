# Compose Multiplatform Application

## Setup

Clone the repository:

```shell
git clone git@github.com:jordond/sigot
cd sigot
```

Now run the init scripts:

```shell
./sigot init ktlint
./sigot init hooks

# The secrets require the env variable SIGOT_DECRYPT_KEY, or the key to be passed in
./sigot init secrets
```

## Backend API

The API handles all communication to the Weather API, there are three planned implementations:

- Cloudflare Worker
- Firebase Function
- Standalone Ktor server

## Cloudflare Worker

Located in the `apps/api/worker` directory.

### Deploy

```shell
# Install wrangler for Cloudflare
npm install -g wrangler

# Install dependencies for the worker
cd apps/api/worker
npm install
cd -

# Build the worker
./gradlew :apps:api:worker:compileProductionExecutableKotlinJs

# Deploy
wrangler deploy

# Optionally deploy to dev/staging
wrangler deploy --env dev
wtangler deploy --env staging
```