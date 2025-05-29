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

First you need to initialize the worker module:

```shell
./sigot init api:worker
```

This will:

- Check if `npm` is available and use `nvm use latest` if it is not
- Install the node dependencies in `apps/api/worker`

### Developing

Now you are ready to start working on the worker API. To build the project, watch for changes, and
get it running with `wrangler`. You can run the following command:

```shell
./sigot api:worker dev
```

If you make a change to any of the source files, gradle will recompile and eventually wrangler will
pick up on the changes.

**Note:** You might see an error from wrangler about missing files, just wait, it should be fine.

### Deploying

Now that you have a new kick-ass feature, you can deploy the worker to Cloudflare with the following command:

```shell
./sigot api:worker deploy
```

If you want to deploy to staging or the dev environment you can pass `--env` like so:

```shell
# Deploy to staging
./sigot api:worker deploy --env staging

# Deploy to dev
./sigot api:worker deploy --env dev
```

Once that is successful the worker should be available at https://api.sigot.app (or dev/staging.api.sigot.app)