# Cloudflare Worker API

Located in the [`./apps/api/worker`](../../apps/api/worker) directory.

First you need to initialize the worker module:

```shell
./sigot init api:worker
```

This will:

- Check if `npm` is available
- Install the node dependencies in `apps/api/worker`

### Deploying

You are now ready to deploy the backend API to your own Cloudflare account, but there are a few
things you need to do first:

1. Update [`./wrangler.json`](./wrangler.json) with the appropriate values
    - Change the URL for the routes to match your own domain
    - **Note:** If your domain's DNS nameservers are **not** Cloudflare, then you will need to
      follow
      separate steps to setup your worker with your domain
2. If you already have `wrangler` installed run: `wrangler login`
    - **Note:** If you don't have `wrangler` installed you can run:
      `./sigot api:worker wrangler login`
3. Now you need to set the `FORECAST_API_KEY` env secret in cloudflare. You can do so by running:
    - `./sigot api:worker secret set`

Now you are ready to deploy!

That can be done by running the following command:

```shell
./sigot api:worker deploy
```

This will preform a clean-build of the project, then attempt to deploy the `apps/api/worker` module
to Cloudflare.

If all goes well you will be able to hit the api at:

`https://your-domain.com/forecast?lat=123&lon=321`

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

Now that you have a new kick-ass feature, you can deploy the worker to Cloudflare with the following
command:

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

Once that is successful the worker should be available at https://api.shouldigooutside.now (or
dev/staging.api.shouldigooutside.now)

### Misc

Wrangler is installed to the `apps/api/worker/node_modules` folder. To run it easily for any other
command you can use the wrapper command:

```shell
./sigot api:worker wrangler <command>
```