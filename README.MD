# Should I go outside today?

"Should I go outside today?" or **SIGOT** is a Kotlin Multiplatform weather app. The inspiration
behind this app was a dead simple weather app that answer that simple question.

When opened the app will fetch the forecast for your current location then display a simple answer
to whether or not you should go outside today. Of course like any good weather app, it will also
give you the detailed forecast if you wish.

The forecast is currently retrieved from the [Visual Crossing API](https://www.visualcrossing.com/).
There is planned support for other API's down the line.

### Supported platforms

The initial release is targeted at:

- Android
- iOS

With a planned future web, desktop, and CLI app.

## How do I get it?

You can download the app from the Play Store or the App Store.

## What if I want to roll my own?

Since this repository is open source, that means you can roll your own version of the backend and
build the app yourself. That way you have total control over your data.

There are two ways of manually setting up the app:

- Hosted backend API
    - Currently only Cloudflare Workers are supported
- Direct access to the Weather API via the app
    - This requires you to add your API Token to the `./app-env.properties` file
    - This will add the API token to a Build Config value, which the app will see and enable direct
      communication to the weather API

## Setup

Go to [VisualCrossing](https://www.visualcrossing.com/) and grab your API key.

Clone the repository:

```shell
git clone git@github.com:jordond/sigot
cd sigot
```

Now run the init script:

```shell
# Initialize the app properties and secrets
# You will be prompted for an API key for the Forecast aPI
./sigot init

# Optional, for development of sigot
./sigot init ktlint
./sigot init hooks
```

The `init` script will prompt you for your API token and store it in the `app-env.properties` file.
You are now ready to build the app, and deploy to Cloudflare.

If you are using a custom deploy to Cloudflare make sure you set the `APP_BACKEND_URL` property in
`app-env.properties`.

## Building front-end app

First decide if you plan on using a [Custom Backend](#custom-backend-api) or will be hitting
the [VisualCrossing API](https://www.visualcrossing.com/) directly.

Now you will need to edit `app-env.properties` with the required values:

### Custom Backend

```properties
# app-env.properties
APP_BACKEND_URL=https://api.my-domain.net
APP_USE_DIRECT_API=false
FORECAST_API_KEY=<My VisualCrossing API key>
```

### Direct to Visual Crossing

```properties
# app-env.properties
APP_USE_DIRECT_API=true
FORECAST_API_KEY=<My VisualCrossing API key>
```

**Note:** Optionally you can set `APP_ENABLE_INTERNAL_SETTINGS` to `true` to enable the internal
settings so you can change these values at runtime in the app.

Now you're ready to build the client app:

- [Android](./docs/build/android)
- [iOS](./docs/build/ios)
- More planned soon

If you are using the Direct API, then you are done!

However if you are deploying a custom backend, follow the steps [below](#custom-backend-api).

## Custom Backend API

The API handles all communication to the Weather API, there are three planned implementations:

- [x] Cloudflare Worker
- [ ] Firebase Function
- [ ] Standalone Ktor server

## Cloudflare Worker

Located in the [`./apps/api/worker`](./apps/api/worker) directory.

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

### Misc

Wrangler is installed to the `apps/api/worker/node_modules` folder. To run it easily for any other command you can use the wrapper command:

```shell
./sigot api:worker wrangler <command>
```