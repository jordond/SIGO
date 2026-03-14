# Should I Go Outside? — Website

Landing page for [Should I Go Outside?](https://shouldigooutside.now) — a weather app that tells you
whether you should go outside based on your preferences (temperature, wind, rain/snow). The app
gives you a **Yes**, **No**, or **Maybe**.

Visit [shouldigooutside.now](https://shouldigooutside.now)

## Tech Stack

- [Astro](https://astro.build) — static site framework
- Deployed on [Cloudflare Workers](https://workers.cloudflare.com/)

## Pages

| Route      | Description      |
|:-----------|:-----------------|
| `/`        | Home             |
| `/help`    | Help             |
| `/privacy` | Privacy policy   |
| `/terms`   | Terms of service |

## Development

All commands are run from the `website/` directory:

| Command        | Action                                     |
|:---------------|:-------------------------------------------|
| `pnpm install` | Install dependencies                       |
| `pnpm dev`     | Start local dev server at `localhost:4321` |
| `pnpm build`   | Build production site to `./dist/`         |
| `pnpm preview` | Preview the build locally before deploying |
