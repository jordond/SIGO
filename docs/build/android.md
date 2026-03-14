# Build Android App

## Prerequisites

- Java 17+
- Android SDK (via Android Studio or command line tools)

## Firebase setup

The app uses Firebase for Remote Config and Crashlytics. You need to create your own Firebase
project and download the config file:

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project
2. Add an Android app with package name `now.shouldigooutside` (or your own package name if you've
   changed it)
3. Download `google-services.json` and place it at `apps/android/google-services.json`
4. Enable any Firebase services you need (Remote Config, Crashlytics, etc.)

> **Note:** `google-services.json` is gitignored. Each contributor must create their own Firebase
> project or obtain the file from a project maintainer.

## Configuration

Copy the sample env file and fill in your values:

```shell
cp app-env.sample.properties app-env.properties
```

Or run the init script which does this for you:

```shell
./sigo init
```

### API access

The app needs a way to get forecast data. Pick one of these two options:

**Option A: Direct API access** (no backend needed):

```properties
USE_DIRECT_API=true
FORECAST_API_KEY=<your Visual Crossing API key>
```

Get a key from [Visual Crossing](https://www.visualcrossing.com/).

**Option B: Custom backend:**

```properties
USE_DIRECT_API=false
APP_BACKEND_URL=https://api.your-domain.com
```

See [Cloudflare Worker](../api/cloudflare.md) for deploying your own backend.

> **Note:** If `USE_DIRECT_API` is `false` and `APP_BACKEND_URL` is blank and no `FORECAST_API_KEY`
> is set, the Gradle build will fail at configuration time.

---

## Debug APK

No signing config needed. Build with:

```shell
./sigo gradle :apps:android:assembleDebug
```

The APK is written to:

```
apps/android/build/outputs/apk/debug/android-debug.apk
```

---

## Release APK

Release builds require a signing keystore. If no keystore is configured, the build falls back to
the debug signing key.

### 1. Create a keystore

```shell
keytool -genkeypair \
  -keystore release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias my-key-alias \
  -storepass <password> \
  -keypass <password>
```

Replace `my-key-alias` and `<password>` with your own values. The store password and key password
**must be the same**. The build uses a single password for both.

You'll be prompted for name, organization, etc.

### 2. Base64 encode the keystore

The build expects the keystore as a base64 string, not a file path.

```shell
base64 -i release.jks | tr -d '\n'
```

Copy the output.

### 3. Add signing properties to `app-env.properties`

```properties
APP_KEYSTORE_BASE64=<base64 output from step 2>
APP_KEYSTORE_PASSWORD=<password>
APP_KEYSTORE_KEY_ALIAS=my-key-alias
```

Gradle decodes the base64 keystore at build time and writes it to
`apps/android/build/signing/release.jks`.

### 4. Build

```shell
./sigo gradle :apps:android:assembleRelease
```

Output locations:

```
apps/android/build/outputs/apk/release/android-release.apk
```

### Verify the APK is signed

```shell
apksigner verify --print-certs apps/android/build/outputs/apk/release/android-release.apk
```

If you see your certificate details instead of the Android debug cert, signing is configured
correctly.

---

## CI / Environment variables

All properties can also be provided as environment variables. The build checks `app-env.properties`
first, then falls back to `System.getenv()`:

```shell
export APP_KEYSTORE_BASE64="..."
export APP_KEYSTORE_PASSWORD="..."
export APP_KEYSTORE_KEY_ALIAS="..."
export APP_BACKEND_URL="https://api.your-domain.com"
```

---

## Full `app-env.properties` example

```properties
# API access (pick one)
USE_DIRECT_API=true
FORECAST_API_KEY=abc123
# Or use a backend instead
# USE_DIRECT_API=false
# APP_BACKEND_URL=https://api.your-domain.com
# Release signing
APP_KEYSTORE_BASE64=MIIKUQIBAzCCCh...
APP_KEYSTORE_PASSWORD=my-secure-password
APP_KEYSTORE_KEY_ALIAS=my-key-alias
# Optional
ENABLE_INTERNAL_SETTINGS=false
```
