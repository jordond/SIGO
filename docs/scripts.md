# Scripts Reference

All project tooling is accessed through the `./sigot` CLI wrapper at the project root. Individual
scripts live in the `scripts/` directory but should generally be invoked through `./sigot` rather
than directly.

---

## `./sigot` — Project CLI

The main entry point for all project commands. Must be run from the project root directory.

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
| `cli [args]`                    | Run the SIGOT CLI application               |
| `api`                           | API build commands                          |
| `api:worker <command>`          | Cloudflare Worker commands                  |
| `xcode`                         | Open the Xcode workspace                    |
| `encrypt`                       | Encrypt a file                              |
| `generate-image-sizes`          | Generate image resource sizes               |

---

## Initialization

### `./sigo init`

Runs the full project initialization. Copies `app-env.sample.properties` to `app-env.properties` and
prompts for the `FORECAST_API_KEY` if it is not already set.

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
| `--ci`            | Run in CI mode (uses default version `1.5.0`, skips auto-update) |
| `--idea`          | Apply ktlint styles to Android Studio                            |

The script automatically detects CI environments via the `$CI` variable (set by GitHub Actions and
most CI providers). In CI mode it uses a pinned version and skips interactive features. The
installed version is tracked in `.app/ktlint.version` for upgrade comparison.

**Script:** `scripts/init-ktlint.sh`

### `./sigo init hooks` (aliases: `hook`, `h`)

Copies all hook scripts from `scripts/hooks/` into `.git/hooks/` and makes them executable.

```
./sigo init hooks [--output]
```

| Option     | Description                                        |
|------------|----------------------------------------------------|
| `--output` | Print the contents of each hook as it is installed |

**Script:** `scripts/init-hooks.sh`

### `./sigo init secrets` (aliases: `secret`, `s`)

Decrypts project secret files using `crypt.sh`. The decryption key can be provided via `--key` or
the `SIGOT_DECRYPT_KEY` environment variable. If neither is set, the script prompts interactively.

```
./sigo init secrets [--key <key>]
```

**Decrypted files:**

| Encrypted source                            | Output                                       |
|---------------------------------------------|----------------------------------------------|
| `secrets/secrets.properties.enc`            | `.app/secrets/secrets.gradle`                |
| `secrets/fastlane_service_account.json.enc` | `.app/secrets/fastlane_service_account.json` |
| `secrets/google-services.json.enc`          | `apps/android/google-services.json`          |
| `secrets/GoogleService-Info.plist.enc`      | `apps/ios/iosApp/GoogleService-Info.plist`   |

**Script:** `scripts/init-secrets.sh`

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

Pass `-F` to auto-fix violations (e.g., `./sigo lint --staged -F`).

**Script:** `scripts/ktlint.sh`

### `./sigo fix`

Runs the pre-commit hook which formats staged shell scripts with `shfmt` and lints staged Kotlin
files with ktlint.

```
./sigo fix          # Fix staged files only
./sigo fix --all    # Fix all Kotlin files + format all shell scripts
```

When `--all` is passed, the script runs ktlint with `-F` on every Kotlin file and `shfmt -w -i 4` on
all scripts. Without `--all` it delegates to the pre-commit hook.

---

## Committing

### `./sigo commit` (alias: `c`)

A convenience command that stages all changes, runs the fix/lint hook, re-stages, and commits.

```
./sigo commit                    # Opens editor for commit message
./sigo commit -m "message"       # Inline commit message
./sigo commit [git commit args]  # Passes args to git commit
```

The commit is created with `--no-verify` since the pre-commit hook has already been run manually.

---

## CLI Application

### `./sigo cli [args]`

Builds and runs the SIGOT CLI application.

```
./sigo cli [args]
```

This compiles `:apps:cli:installDist` via Gradle (quietly), then executes the built binary at
`apps/cli/build/install/sigot/bin/sigot` with any provided arguments.

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

Manages the Cloudflare Worker that serves the SIGOT API.

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
| `update-wrangler`  | Update Wrangler to the latest version            |
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

Both are cleaned up on `Ctrl+C`. The script waits up to 60 seconds for the initial build before
starting Wrangler.

**Prerequisites:** The dev and deploy commands verify that `FORECAST_API_KEY` is configured. The key
is sourced from `app-env.properties` (set up by `./sigo init`) and written to `.dev.vars` for
Wrangler.

**Script:** `scripts/api-worker.sh`

---

## Encryption / Decryption

### `./sigo encrypt`

Encrypts a file using AES-256-CBC with PBKDF2 key derivation (500,000 iterations, SHA-512). Output
is written to the `secrets/` directory with a `.enc` extension.

```
./sigo encrypt --key <key> <file>
```

### Decryption (via `init secrets`)

Decryption is handled through `./sigo init secrets` which calls `crypt.sh decrypt` internally. It
can also be called directly:

```
scripts/crypt.sh decrypt --key <key> [file]   # Specific file
scripts/crypt.sh decrypt --key <key>           # All .enc files in secrets/
scripts/crypt.sh decrypt                       # Uses SIGOT_DECRYPT_KEY env var
```

**Key requirements:** Minimum 32 characters. Can be provided via `--key`, the `SIGOT_DECRYPT_KEY`
environment variable, or interactive prompt.

**Encryption details:**

- Algorithm: AES-256-CBC
- Key derivation: PBKDF2 with 500,000 iterations
- Digest: SHA-512

**Script:** `scripts/crypt.sh`

---

## Git Hooks

### `pre-commit`

The pre-commit hook runs automatically on `git commit` (after `./sigo init hooks`).

**Steps:**

1. Finds staged shell scripts in `scripts/` (excluding `.js` and `.json`) and formats them with
   `shfmt -i 4`
2. Runs `ktlint.sh --staged` on staged Kotlin files

Any additional arguments (e.g., `-F` for auto-fix) are forwarded to ktlint.

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

# 4. (Optional) Decrypt secrets if you have the key
./sigo init secrets --key $SIGOT_DECRYPT_KEY

# 5. (Optional) Set up the API worker
./sigo init api:worker
```
