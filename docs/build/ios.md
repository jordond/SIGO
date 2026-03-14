# Build iOS App

## Prerequisites

- Mac with Xcode installed
- Apple Developer account (free or paid)
- Java 17+ (for Gradle / KMP compilation)
- `app-env.properties` configured at the project root (see [Configuration](#configuration))

A **paid** Apple Developer account ($99/year) is required to install on a physical device. A free
account works for simulator builds only.

## Configuration

The iOS app doesn't read `app-env.properties` directly. Gradle compiles the shared KMP code with
those values as compile-time constants. Same setup as the
[Android build doc](./android.md#configuration).

At minimum, set one of:

```properties
# Direct API access
USE_DIRECT_API=true
FORECAST_API_KEY=<your Visual Crossing API key>
```

```properties
# Custom backend
USE_DIRECT_API=false
APP_BACKEND_URL=https://api.your-domain.com
```

If neither is configured, Gradle will fail during the Xcode pre-build step.

## Opening the project

```shell
./sigo xcode
```

Or open `apps/ios/iosApp.xcodeproj` directly. There is no separate `.xcworkspace`.

The first time you open the project, Xcode will download SPM dependencies (Firebase SDK and its
transitive packages). This can take a few minutes.

## How the build works

Xcode has a pre-build script phase that runs:

```
./gradlew :apps:ios:embedAndSignAppleFrameworkForXcode
```

This compiles all the shared Kotlin code into a static framework (`iosApp.framework`) and embeds it
into the app bundle. The Swift side of the project is just `iosApp.swift`, which calls into the KMP
framework.

The first build is slow because Gradle compiles the full KMP project. Subsequent builds are
incremental.

## Simulator

1. Open the project in Xcode
2. Select the `iosApp` scheme and a simulator target
3. Build and run (`Cmd+R`)

No developer account is needed for simulator builds.

## Physical device

To run on a real iPhone or iPad:

1. Open the project in Xcode
2. Go to **Signing & Capabilities** for the `iosApp` target
3. Change the **Team** to your Apple Developer account
4. Update the **Bundle Identifier** to something unique (e.g., `com.yourname.sigo`). Apple
   requires each app to have a globally unique bundle ID
5. Connect your device via USB or select it from the device dropdown
6. Build and run (`Cmd+R`)

Xcode handles provisioning and code signing automatically. On first deploy, you may need to trust
the developer certificate on the device:

**Settings > General > VPN & Device Management > [your developer account] > Trust**

The app stays installed after you disconnect. With a free account it expires after 7 days. Paid
accounts have no expiry.

## Troubleshooting

**Gradle fails during pre-build:** Make sure `app-env.properties` exists at the project root with
valid API configuration.

**Scheme not visible:** The `iosApp` scheme may be in user-specific Xcode data. If you don't see
it, go to **Product > Scheme > New Scheme** and select the `iosApp` target.

**Slow first build:** Normal. Gradle compiles the full KMP project on first run. Incremental after
that.
