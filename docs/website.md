# Website

The marketing website for SIGO lives in `website/` and is deployed to Cloudflare Workers. It serves
as the public landing page at [shouldigooutside.now](https://shouldigooutside.now).

## Stack

- **Astro** — Static site generator with scoped CSS and zero-JS-by-default pages
- **Cloudflare Workers** — Hosting via `@astrojs/cloudflare` adapter
- **Wrangler** — Deployment tooling

## Pages

| Route      | File                    | Purpose                          |
|------------|-------------------------|----------------------------------|
| `/`        | `src/pages/index.astro` | Landing page with app pitch      |
| `/help`    | `src/pages/help.astro`  | Help / FAQ                       |
| `/privacy` | `src/pages/privacy.astro` | Privacy policy                 |
| `/terms`   | `src/pages/terms.astro` | Terms of service                 |
| `404`      | `src/pages/404.astro`   | Not found page                   |

## Components

| Component             | Purpose                                        |
|-----------------------|------------------------------------------------|
| `Hero`                | Above-the-fold headline and call to action     |
| `FeatureShowcase`     | App feature highlights                         |
| `HowItWorks`          | Step-by-step explanation of the app            |
| `TrackedConditions`   | Weather conditions the app monitors            |
| `DownloadButtons`     | App Store and Play Store download links        |
| `Footer`              | Site footer                                    |

## Design

The site uses a **neobrutalism** aesthetic — chunky borders, hard drop shadows, bright colors on a
warm cream background. Typography uses Lexend Mega for display headings and Public Sans for body
text, matching the native app.

All styling is pure scoped Astro CSS with global CSS variables defined in
`src/layouts/Layout.astro`. No CSS frameworks.

See `website/CLAUDE.md` for the full design context, palette, and principles.

## Local Development

```shell
./sigo website dev   # Start Astro dev server (from project root)

# Or directly from the website directory:
cd website
pnpm install
pnpm run dev         # Astro dev server
pnpm run preview     # Build + run locally via Wrangler dev
```

## Environments

Configured in `website/wrangler.jsonc`:

| Environment | Worker Name              | Domain                              |
|-------------|--------------------------|-------------------------------------|
| `prod`      | `sigo-marketing`         | `shouldigooutside.now`              |
| `staging`   | `sigo-marketing-staging` | `staging.shouldigooutside.now`      |

## Deploying

Use the `./sigo` CLI from the project root:

```shell
# Deploy to production
./sigo release:website

# Deploy to staging
./sigo release:website --env staging

# Dry run (build + preview deploy without publishing)
./sigo release:website --dry-run
```

See [scripts.md](scripts.md#sigo-releasewebsite) for the full options reference.
