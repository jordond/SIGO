# API Worker Release Guide

The API worker runs on [Cloudflare Workers](https://developers.cloudflare.com/workers/). Releases
are managed through the `./sigo release:api` command, which bumps the version, builds, tags, and
deploys in a single workflow.

## Prerequisites

Before your first release:

1. **Initialize the worker module:** `./sigo init api:worker`
2. **Log in to Wrangler:** `./sigo api:worker wrangler login`
3. **Set the Cloudflare secret:** `./sigo api:worker secret set`

See [Cloudflare Worker setup](cloudflare.md) for details.

## Quick Start

```shell
# Patch release (1.0.0 → 1.0.1) — builds, tags, pushes, deploys to prod
./sigo release:api --semver patch

# Exact version — skip deploy
./sigo release:api --version 2.0.0 --no-deploy

# Minor release — deploy to all environments
./sigo release:api --semver minor --deploy-all
```

## How It Works

The release command runs through these steps:

1. **Version bump** — Updates `api-server-version` in `gradle/libs.versions.toml`. This value is
   read by the Gradle build to generate `Version.kt`, which the worker serves at `GET /`.
2. **Build** — Compiles the Kotlin/JS worker via Gradle. If the build fails, the version bump is
   automatically reverted.
3. **Commit** — Commits the version change: `bump for api release <version> [skip-ci]`
4. **Tag** — Creates `release/api/<version>`
5. **Push** — Pushes the commit and tag to the remote.
6. **Deploy** — Prompts for confirmation, then deploys to Cloudflare via
   `./sigo api:worker deploy`. Skipped if `--no-deploy` is passed.

## Environments

The worker is deployed to three Cloudflare environments configured in
[`wrangler.json`](../../apps/api/worker/wrangler.json):

| Environment | URL                                     | Flag              |
|-------------|-----------------------------------------|-------------------|
| `prod`      | `https://api.shouldigooutside.now`      | *(default)*       |
| `staging`   | `https://staging.api.shouldigooutside.now` | `--deploy-env staging` |
| `dev`       | `https://dev.api.shouldigooutside.now`  | `--deploy-env dev` |

Deploy to all environments at once with `--deploy-all`.

## Version Endpoint

After deploying, verify the release by hitting the root endpoint:

```shell
curl -H "Origin: https://shouldigooutside.now" https://api.shouldigooutside.now/
```

The response includes the version name, build code, and git SHA:

```json
{
  "data": {
    "version": {
      "name": "1.0.1",
      "code": 1,
      "sha": "abc1234"
    }
  }
}
```

## Options Reference

| Option                    | Description                                          |
|---------------------------|------------------------------------------------------|
| `-v`, `--version <ver>`   | Set exact version (e.g. `1.0.1`)                     |
| `-s`, `--semver <level>`  | Bump version: `major`, `minor`, `patch`, `none`      |
| `-y`, `--yes`             | Skip confirmation prompts                            |
| `-n`, `--dry-run`         | Show what would happen without making changes        |
| `--no-tag`                | Skip creating git tag                                |
| `--no-commit`             | Skip creating git commit                             |
| `--no-push`               | Skip pushing to remote                               |
| `--no-git`                | Skip both tagging and committing                     |
| `--no-deploy`             | Skip deploying to Cloudflare                         |
| `--no-clean`              | Skip clean before building                           |
| `--no-branch-check`       | Skip branch verification                             |
| `--deploy-env <env>`      | Deploy environment (default: `prod`)                 |
| `--deploy-all`            | Deploy to all environments                           |

## Common Workflows

### Production release

```shell
./sigo release:api --semver patch
```

### Test on staging first, then promote

```shell
# Deploy to staging without git operations
./sigo release:api --semver patch --no-git --deploy-env staging

# When ready, do the full release to prod
./sigo release:api --semver patch
```

### Version bump only (CI handles deploy)

```shell
./sigo release:api --semver patch --no-deploy
```

### Dry run to preview

```shell
./sigo release:api --semver minor --dry-run
```
