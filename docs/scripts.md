# Scripts Reference

All project tooling goes through the `./sigo` CLI wrapper at the project root. Individual scripts
live in `scripts/` but should be invoked through `./sigo`.

---

## `./sigo`

Must be run from the project root.

```
./sigo <command> [args]
```

### Commands

| Command                         | Description                                 |
|---------------------------------|---------------------------------------------|
| `help`                          | Show usage information                      |
| `init [subcommand]`             | Initialize project components               |
| `gradle <command>`              | Run any Gradle command (alias: `g`)         |
| `add <component>`               | Add a Lumo UI component                     |
| `clean`                         | Clean all build files                       |
| `lint [--all\|--staged] [args]` | Lint the codebase (alias: `l`)              |
| `fix [--all]`                   | Auto-fix staged files (or all with `--all`) |
| `commit [options]`              | Stage, fix, and commit (alias: `c`)         |
| `cli [args]`                    | Run the SIGO CLI application                |
| `api`                           | API build commands                          |
| `api:worker <command>`          | Cloudflare Worker commands                  |
| `release:app [options]`         | Release both Android and iOS                |
| `release:app:android [options]` | Build and release Android AAB               |
| `release:app:ios [options]`     | Bump iOS version and prepare Xcode release  |
| `release:api [options]`         | Release and deploy the API worker           |
| `website dev`                   | Start the website dev server                |
| `release:website [options]`     | Deploy the website to Cloudflare            |
| `xcode`                         | Open the Xcode workspace                    |
| `generate-image-sizes`          | Generate image resource sizes               |

---

## Initialization

### `./sigo init`

Copies `app-env.sample.properties` to `app-env.properties` and prompts for `FORECAST_API_KEY`.

**Script:** `scripts/init.sh`

### `./sigo init ktlint` (aliases: `lint`, `k`)

Installs the [ktlint](https://github.com/pinterest/ktlint) binary to `.app/ktlint`.

```
./sigo init ktlint [options]
```

| Option            | Description                                             |
|-------------------|---------------------------------------------------------|
| `-u`, `--update`  | Force update even if already installed                  |
| `--version <ver>` | Install a specific version (default: `latest`)          |
| `--ci`            | CI mode: uses pinned version `1.5.0`, skips auto-update |
| `--idea`          | Apply ktlint styles to Android Studio                   |

CI environments are detected via `$CI`. The installed version is tracked in `.app/ktlint.version`.

**Script:** `scripts/init-ktlint.sh`

### `./sigo init hooks` (aliases: `hook`, `h`)

Copies hook scripts from `scripts/hooks/` into `.git/hooks/`.

```
./sigo init hooks [--output]
```

| Option     | Description                                        |
|------------|----------------------------------------------------|
| `--output` | Print the contents of each hook as it is installed |

**Script:** `scripts/init-hooks.sh`

### `./sigo init api:worker`

Installs npm dependencies for the Cloudflare Worker (`apps/api/worker/`) and verifies Wrangler is
available.

---

## Linting & Formatting

### `./sigo lint` (alias: `l`)

Runs ktlint on Kotlin files.

```
./sigo lint [--all | --staged] [ktlint args]
```

| Mode              | Description                                                            |
|-------------------|------------------------------------------------------------------------|
| `--all` (default) | Lint all `*.kt` and `*.kts` files, excluding `build/` and `generated/` |
| `--staged`        | Lint only git-staged Kotlin files                                      |

Pass `-F` to auto-fix (e.g., `./sigo lint --staged -F`).

**Script:** `scripts/ktlint.sh`

### `./sigo fix`

Runs the pre-commit hook: formats staged shell scripts with `shfmt`, then lints staged Kotlin files
with ktlint.

```
./sigo fix          # staged files only
./sigo fix --all    # all Kotlin files + all shell scripts
```

With `--all`, runs ktlint `-F` on every Kotlin file and `shfmt -w -i 4` on all scripts. Without it,
delegates to the pre-commit hook.

---

## Committing

### `./sigo commit` (alias: `c`)

Stages all changes, runs fix/lint, re-stages, and commits.

```
./sigo commit                    # opens editor for commit message
./sigo commit -m "message"       # inline commit message
./sigo commit [git commit args]  # passes args to git commit
```

Uses `--no-verify` since the pre-commit hook is already run inline.

---

## CLI Application

### `./sigo cli [args]`

Builds and runs the SIGO CLI application. Compiles `:apps:cli:installDist` via Gradle, then runs
the binary at `apps/cli/build/install/sigo/bin/sigo`.

**Script:** `scripts/cli.sh`

---

## API

### `./sigo api`

Build commands for the Kotlin/JS API module.

```
./sigo api build [--clean]
```

| Option    | Description                                                                                                       |
|-----------|-------------------------------------------------------------------------------------------------------------------|
| `build`   | Compile the Kotlin/JS worker to `apps/api/worker/build/compileSync/js/main/productionExecutable/kotlin/index.mjs` |
| `--clean` | Run `gradlew clean` before building                                                                               |

**Script:** `scripts/api.sh`

### `./sigo api:server <command>`

JVM API server (Ktor/Netty).

```
./sigo api:server <command> [options]
```

| Command               | Description                                   |
|-----------------------|-----------------------------------------------|
| `dev [--port <port>]` | Run the server locally via Gradle             |
| `build [--clean]`     | Build the server distribution via installDist |
| `docker build`        | Build the Docker image                        |
| `docker run`          | Run the Docker container                      |

#### Dev mode

Starts the JVM server via Gradle. The API key is read from `app-env.properties` at build time,
so no extra setup is needed after `./sigo init`.

```shell
./sigo api:server dev
./sigo api:server dev --port 9090
```

#### Docker

```shell
./sigo api:server docker build
./sigo api:server docker build --tag my-api
./sigo api:server docker run
./sigo api:server docker run --port 9090 --tag my-api
```

**Script:** `scripts/api-server.sh`

---

### `./sigo api:worker <command>`

Manages the Cloudflare Worker that serves the SIGO API.

```
./sigo api:worker <command> [options]
```

| Command            | Description                                      |
|--------------------|--------------------------------------------------|
| `init`             | Install npm dependencies and verify Wrangler     |
| `secret check`     | Check if `FORECAST_API_KEY` exists in Cloudflare |
| `secret set`       | Set the `FORECAST_API_KEY` secret in Cloudflare  |
| `build [--clean]`  | Compile the worker (delegates to `api.sh build`) |
| `dev`              | Start Kotlin/JS watcher + Wrangler dev server    |
| `deploy [options]` | Deploy to Cloudflare                             |
| `update-wrangler`  | Update Wrangler to latest                        |
| `wrangler [args]`  | Pass-through to the Wrangler CLI                 |

#### Deploy options

```
./sigo api:worker deploy [--env <env>] [--no-clean] [--all]
```

| Option        | Description                                            |
|---------------|--------------------------------------------------------|
| `--env <env>` | Target environment: `prod` (default), `staging`, `dev` |
| `--no-clean`  | Skip clean before building                             |
| `--all`       | Deploy to all environments sequentially                |

#### Dev mode

`./sigo api:worker dev` starts two processes:

1. Gradle continuous build watching for Kotlin/JS changes
2. Wrangler dev server serving the compiled worker

Both are cleaned up on `Ctrl+C`. Waits up to 60s for the initial build before starting Wrangler.

The dev and deploy commands require `FORECAST_API_KEY` in `app-env.properties`. The key is written
to `.dev.vars` for Wrangler.

**Script:** `scripts/api-worker.sh`

---

## Website

### `./sigo website dev`

Starts the Astro dev server for the marketing website.

```shell
./sigo website dev
```

---

## Releasing

### `./sigo release:app:android`

Builds an Android release AAB, bumps version/build-code, and optionally tags + commits + pushes.

Must be on `main` or a `release/*` branch (unless `--no-branch-check` is passed).

```
./sigo release:app:android [options]
```

| Option                   | Description                                     |
|--------------------------|-------------------------------------------------|
| `-v`, `--version <ver>`  | Set exact version (e.g. `1.0.1`)                |
| `-s`, `--semver <level>` | Bump version: `major`, `minor`, `patch`, `none` |
| `-o`, `--output <dir>`   | Output directory for AAB (default: `./release`) |
| `--no-tag`               | Skip creating git tags                          |
| `--no-commit`            | Skip creating git commit                        |
| `--no-push`              | Skip pushing to remote                          |
| `--no-git`               | Skip both tagging and committing                |
| `--no-clean`             | Skip clean before building                      |
| `--no-branch-check`      | Skip branch verification                        |

If neither `--version` nor `--semver` is provided, defaults to a **patch** bump.

The script:

1. Increments `app-android-version` and `app-android-code` in `gradle/libs.versions.toml`
2. Runs `:apps:android:bundleRelease`
3. Copies the AAB to `<output>/android-<version>-<code>.aab`
4. Creates tags `android/build/<code>` and `release/android/<version>`
5. Commits: `bump for android release <version> (<code>) [skip-ci]`
6. Pushes commit and tags

```shell
./sigo release:app:android --semver patch
./sigo release:app:android --version 2.0.0 --no-push
./sigo release:app:android --semver minor --no-git
```

**Script:** `scripts/release-android.sh`

### `./sigo release:app:ios`

Bumps the iOS version in `gradle/libs.versions.toml` and updates `MARKETING_VERSION` /
`CURRENT_PROJECT_VERSION` in the Xcode project. Then tags, commits, and instructs you to archive
via Xcode.

```
./sigo release:app:ios [options]
```

| Option                   | Description                                     |
|--------------------------|-------------------------------------------------|
| `-v`, `--version <ver>`  | Set exact version (e.g. `1.0.1`)                |
| `-s`, `--semver <level>` | Bump version: `major`, `minor`, `patch`, `none` |
| `--no-tag`               | Skip creating git tags                          |
| `--no-commit`            | Skip creating git commit                        |
| `--no-push`              | Skip pushing to remote                          |
| `--no-git`               | Skip both tagging and committing                |
| `--no-branch-check`      | Skip branch verification                        |

After running, follow the printed steps to archive and upload via Xcode.

```shell
./sigo release:app:ios --semver minor
./sigo release:app:ios --version 1.2.0
```

**Script:** `scripts/release-ios.sh`

### `./sigo release:app`

Runs both Android and iOS releases with a **single combined commit**. All options from the
individual commands are supported **except `--version`** (use `--semver` instead, or run each
platform separately if you need exact version control).

```shell
./sigo release:app --semver minor
./sigo release:app --semver patch --no-push
```

**Script:** `scripts/release-app.sh`

### `./sigo release:api`

Bumps the API worker version, builds, commits, tags, pushes, and optionally deploys to Cloudflare.

Must be on `main` or a `release/*` branch (unless `--no-branch-check` is passed).

```
./sigo release:api [options]
```

| Option                   | Description                                                    |
|--------------------------|----------------------------------------------------------------|
| `-v`, `--version <ver>`  | Set exact version (e.g. `1.0.1`)                               |
| `-s`, `--semver <level>` | Bump version: `major`, `minor`, `patch`, `none`                |
| `--no-tag`               | Skip creating git tag                                          |
| `--no-commit`            | Skip creating git commit                                       |
| `--no-push`              | Skip pushing to remote                                         |
| `--no-git`               | Skip both tagging and committing                               |
| `--no-deploy`            | Skip deploying to Cloudflare                                   |
| `--no-clean`             | Skip clean before building                                     |
| `--no-branch-check`      | Skip branch verification                                       |
| `--deploy-env <env>`     | Deploy environment: `prod`, `staging`, `dev` (default: `prod`) |
| `--deploy-all`           | Deploy to all environments (prod, staging, dev)                |

Either `--version` or `--semver` is required.

The script:

1. Increments `api-server-version` in `gradle/libs.versions.toml`
2. Builds the Kotlin/JS worker via `./sigo api build`
3. Commits: `bump for api release <version> [skip-ci]`
4. Creates tag `release/api/<version>`
5. Pushes commit and tag
6. Prompts for deploy confirmation, then deploys via `./sigo api:worker deploy`

If the build fails, the version bump in `libs.versions.toml` is automatically reverted.

```shell
./sigo release:api --semver patch
./sigo release:api --version 2.0.0 --no-deploy
./sigo release:api --semver minor --deploy-all
./sigo release:api --semver patch --deploy-env staging --no-push
```

See [API Release Guide](api/release.md) for the full workflow.

**Script:** `scripts/release-api.sh`

### `./sigo release:website`

Installs dependencies, builds, and deploys the marketing website to Cloudflare.

```
./sigo release:website [options]
```

| Option         | Description                                          |
|----------------|------------------------------------------------------|
| `--env <env>`  | Target environment: `prod` (default) or `staging`    |
| `--dry-run`    | Run wrangler deploy in dry-run mode (no actual deploy) |

```shell
# Deploy to production
./sigo release:website

# Deploy to staging
./sigo release:website --env staging

# Preview what would be deployed
./sigo release:website --dry-run
```

See [Website](website.md) for more about the marketing site.

**Script:** `scripts/release-website.sh`

---

## Git Hooks

### `pre-commit`

Runs automatically on `git commit` (after `./sigo init hooks`).

1. Formats staged shell scripts in `scripts/` (excluding `.js`/`.json`) with `shfmt -i 4`
2. Runs `ktlint.sh --staged` on staged Kotlin files

Additional arguments (e.g., `-F`) are forwarded to ktlint.

**Script:** `scripts/hooks/pre-commit`

---

## Quick Start

```bash
# 1. Initialize the project (env file + API key)
./sigo init

# 2. Install ktlint
./sigo init ktlint

# 3. Install git hooks
./sigo init hooks

# 4. (Optional) Set up the API worker
./sigo init api:worker
```
