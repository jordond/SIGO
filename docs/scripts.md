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

| Option            | Description                                                      |
|-------------------|------------------------------------------------------------------|
| `-u`, `--update`  | Force update even if already installed                           |
| `--version <ver>` | Install a specific version (default: `latest`)                   |
| `--ci`            | CI mode: uses pinned version `1.5.0`, skips auto-update          |
| `--idea`          | Apply ktlint styles to Android Studio                            |

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
