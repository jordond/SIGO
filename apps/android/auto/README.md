# SIGO Android Auto

Slimmed-down Android Auto companion for the SIGO weather app. Lives at `:apps:android:auto`, ships
inside the `:apps:android` APK via manifest merging.

## Architecture

Highlights:

- `SigoCarAppService` is the Android Auto entry point (declared in this module's manifest).
- `SigoSession` owns the session lifecycle and a coroutine scope.
- `SigoSessionOrchestrator` (pure) drives `forecastStateHolder.fetch()` + state-change
  `invalidate()`.
- `CarForecastFormatter` (pure) returns `Pane`/`ItemList`/`MessageTemplate` parts.
- `HomeTemplateBuilder`, `HourlyTemplateBuilder`, `AlertsTemplateBuilder` (pure) wrap formatter
  output in templates.
- `HomeScreen`, `HourlyScreen`, `AlertsScreen`, `AlertDetailScreen` are thin `Screen` subclasses.
- `CarLocationProvider` opportunistically primes `SettingsRepo.lastLocation` from car-hardware
  GPS (`AndroidCarHardwareLocationSource`), falling back to the existing phone `LocationRepo`.

## Manual smoke test via DHU (Desktop Head Unit)

1. Install [Desktop Head Unit](https://developer.android.com/training/cars/testing) (bundled with
   the Android SDK).
2. On the phone: enable Developer Options, open Android Auto settings, tap the version 10 times to
   enable Developer Settings, then enable "Unknown sources".
3. Install a debug build: `./gradlew :apps:android:installDebug`.
4. Connect the phone over USB and start DHU.
5. Open Android Auto on the head unit and tap the SIGO icon under the Weather category.

Verify:

- **Cold install, no saved location** → `MessageTemplate` with
  `Open SIGO on your phone to get started`.
- **Saved location, fresh forecast** → `PaneTemplate` with: verdict + temp row, conditions row,
  location row, `Refresh` action, `Hourly forecast` action.
- **Tap `Hourly forecast`** → `ListTemplate` with up to 12 future hours.
- **Forecast has alerts** → `ActionStrip` shows the alerts count badge; tap it → `ListTemplate` of
  alerts; tap an alert → `MessageTemplate` with the alert description.
- **DHU speed slider > 0** → row count clamps to ~6 (host enforced).
- **Airplane mode after a successful fetch** → cached pane is retained, stale row appears once
  threshold passes.
- **`Refresh` action** → re-fetches and updates rows.
- **Change selected activity on phone** → AA pane verdict updates.

## Play Console submission TODOs

- [ ] Enable the Android Auto form factor for the app listing (Setup → Advanced settings → Form
  factors).
- [ ] Upload AA-specific screenshots (capture via DHU at 1080×1920).
- [ ] Declare the app as Weather category in the Auto submission form.
- [ ] Replace the empty `HostValidator.Builder(this).build()` (fail-closed) in
  `SigoCarAppService.createHostValidator` with the official Google AA host allowlist before
  submitting. Debug builds use `ALLOW_ALL_HOSTS_VALIDATOR`; release currently rejects every host.
  Look for the `TODO(release-validator)` comment.
- [ ] Replace the parameterized-string English fallbacks (`Updated %1$d min ago`, etc.) with
  localized lookups in the future — currently uses `String.format` over the resource template, which
  preserves localization automatically.
- [ ] Submit for Android Auto review. The first AA review can take 1–2 weeks and is separate from
  the regular Play review.

## Out of scope (v1)

- Daily forecast (hourly only).
- Severe-weather `CarAppNotification` push (badge only).
- In-car activity switcher.
- `TabTemplate`.
- Voice / Assistant intents.
- Settings UI in the car.
- AAOS (Automotive OS) target — separate work.
- Deep-link from car back into phone activity.
