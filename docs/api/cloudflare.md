# Cloudflare Worker API

Located in the [`./apps/api/worker`](../../apps/api/worker) directory.

First you need to initialize the worker module:

```shell
./sigo init api:worker
```

This will:

- Check if `npm` is available
- Install the node dependencies in `apps/api/worker`

### Deploying

Before deploying to your Cloudflare account:

1. Update [`wrangler.json`](../../wrangler.json) with your domain routes
    - **Note:** If your domain's DNS nameservers are **not** Cloudflare, you'll need to set up the
      worker with your domain separately
2. Log in to Wrangler: `wrangler login` (or `./sigo api:worker wrangler login` if you don't have it
   installed globally)
3. Set the `FORECAST_API_KEY` secret: `./sigo api:worker secret set`

Deploy:

```shell
./sigo api:worker deploy
```

This clean-builds the project and deploys the `apps/api/worker` module to Cloudflare.

The API will be available at:

`https://your-domain.com/forecast?lat=123&lon=321`

### Developing

Start the dev server with file watching:

```shell
./sigo api:worker dev
```

Gradle re-compiles on source changes and Wrangler picks them up automatically.

**Note:** You might see a Wrangler error about missing files on first start. Wait for the initial
build to finish.

### Deploying

```shell
./sigo api:worker deploy
```

Deploy to staging or dev with `--env`:

```shell
# Deploy to staging
./sigo api:worker deploy --env staging

# Deploy to dev
./sigo api:worker deploy --env dev
```

The worker will be available at `https://api.shouldigooutside.now` (or `dev`/
`staging.api.shouldigooutside.now`).

### Misc

Wrangler is installed to `apps/api/worker/node_modules`. Run it via the wrapper:

```shell
./sigo api:worker wrangler <command>
```