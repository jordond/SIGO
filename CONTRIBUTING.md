# Contributing to SIGO

SIGO is a Kotlin Multiplatform weather app that tells you whether you should go outside. It runs on
Android, iOS, Desktop (JVM), and CLI, with two API backends (JVM server and Cloudflare Worker).

## Prerequisites

| Tool        | Version | Notes                                      |
|-------------|---------|--------------------------------------------|
| **Java**    | 21+     | 21 for project, 25 for server; CI uses 25  |
| **Gradle**  | 9.4     | Bundled via wrapper (`./gradlew`)          |
| **Xcode**   | Latest  | iOS only; requires Apple Developer account |
| **Node.js** | 22+     | Cloudflare Worker only                     |
| **pnpm**    | 10+     | Website only                               |

## Getting Started

### 1. Clone and initialize

```bash
git clone <repo-url>
cd SIGO
./sigo init
```

`./sigo init` copies `app-env.sample.properties` to `app-env.properties` and prompts you for a
`FORECAST_API_KEY`.

### 2. Get a weather API key

Sign up at [Visual Crossing](https://www.visualcrossing.com/) (free tier available) and add your
key to `app-env.properties`:

```properties
USE_DIRECT_API=true
FORECAST_API_KEY=<your-key>
```

Alternatively, if you're running the SIGO backend locally, set `USE_DIRECT_API=false` and provide
`APP_BACKEND_URL` instead.

### 3. Firebase configuration

The Android and iOS apps require Firebase config files. These are **not** checked into the repo.

1. Create a [Firebase project](https://console.firebase.google.com/) (or ask a maintainer for
   access to the existing one).
2. Register an Android app with package name `now.shouldigooutside` and download
   `google-services.json`.
3. Register an iOS app and download `GoogleService-Info.plist`.
4. Place the files at:

```
apps/android/google-services.json
apps/ios/iosApp/GoogleService-Info.plist
```

> **CI note:** CI uses placeholder files from `.github/ci-google-services.json` and
> `.github/ci-app-env.properties` so builds pass without real credentials.

### 4. Install tooling

```bash
./sigo init ktlint    # install the Kotlin linter
./sigo init hooks     # install pre-commit hooks (ktlint + shfmt)
```

### 5. Build and run

| Target             | Command                                           |
|--------------------|---------------------------------------------------|
| Android (debug)    | `./sigo gradle :apps:android:assembleDebug`       |
| iOS                | `./sigo xcode` (opens the Xcode workspace)        |
| Desktop            | `./sigo gradle :apps:desktop:run`                 |
| CLI                | `./sigo cli`                                      |
| API server (local) | `./sigo api:server dev`                           |
| Cloudflare Worker  | `./sigo init api:worker && ./sigo api:worker dev` |
| Website            | `cd website && pnpm install && pnpm dev`          |

## Project Structure

```
apps/           Runnable targets (android, ios, desktop, cli, api/server, api/worker)
core/           Shared libraries (model, domain, platform, foundation, config, ui, api/*)
feature/        Feature modules (forecast, location, settings, onboarding, webview)
buildLogic/     Gradle convention plugins
website/        Astro landing page (separate pnpm project)
scripts/        Shell scripts invoked by ./sigo
docs/           Architecture and build documentation
```

All project commands go through the `./sigo` CLI wrapper. Run `./sigo help` for the full list, or
see [`docs/scripts.md`](docs/scripts.md) for detailed reference.

## Code Style

Kotlin code is formatted with [ktlint](https://github.com/pinterest/ktlint) using the
`ktlint_official` code style. Key rules (configured in `.editorconfig`):

- 4-space indentation
- 110-character line limit
- Trailing commas allowed
- No star imports

The pre-commit hook automatically formats staged Kotlin files and shell scripts. You can also run
manually:

```bash
./sigo lint --all        # check all files
./sigo lint --staged     # check staged files only
./sigo fix               # auto-fix staged files
./sigo fix --all         # auto-fix everything
```

## Making Changes

1. **Create a branch** off `main` with a descriptive name.
2. **Make your changes.** Keep commits focused and well-described.
3. **Lint before pushing** — the pre-commit hook handles this if installed, but you can also run
   `./sigo lint --all` manually.
4. **Open a pull request** against `main`.

### Commit workflow

The project provides a convenience command that stages, fixes, and commits in one step:

```bash
./sigo commit -m "your commit message"
```

This runs the linter/formatter before committing so CI stays green.

## CI Pipeline

Pull requests and pushes to `main` trigger the CI workflow (`.github/workflows/ci.yml`):

1. **Lint** — ktlint on all Kotlin files
2. **Build** — compile the Android debug variant
3. **Build Docker** — build the API server Docker image
4. **Build Worker** — compile and validate the Cloudflare Worker bundle

CI uses Java 25 and placeholder config files. Commits containing `[skip-ci]` in the message bypass
the pipeline (used for version-bump commits during releases).

## Configuration Reference

All app configuration lives in `app-env.properties` (gitignored). The file is read at **compile
time** via the BuildKonfig Gradle plugin — values are baked into the binary, not read at runtime.

| Property                   | Required | Description                                |
|----------------------------|----------|--------------------------------------------|
| `USE_DIRECT_API`           | Yes      | `true` to call the weather API directly    |
| `FORECAST_API_KEY`         | Yes*     | Visual Crossing API key (* if direct mode) |
| `APP_BACKEND_URL`          | Yes*     | Backend URL (* if not using direct API)    |
| `ENABLE_INTERNAL_SETTINGS` | No       | Show internal debug settings               |
| `APP_KEYSTORE_BASE64`      | No       | Base64-encoded release keystore            |
| `APP_KEYSTORE_PASSWORD`    | No       | Keystore password                          |
| `APP_KEYSTORE_KEY_ALIAS`   | No       | Keystore key alias                         |

## Further Reading

- [`docs/architecture.md`](docs/architecture.md) — module dependency graph and design overview
- [`docs/scripts.md`](docs/scripts.md) — full `./sigo` CLI reference
- [`docs/build/android.md`](docs/build/android.md) — Android build and signing
- [`docs/build/ios.md`](docs/build/ios.md) — iOS build and Xcode setup
- [`docs/api/server.md`](docs/api/server.md) — JVM API server operations
- [`docs/api/cloudflare.md`](docs/api/cloudflare.md) — Cloudflare Worker deployment
- [`docs/api/release.md`](docs/api/release.md) — API Worker release guide
