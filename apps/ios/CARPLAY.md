# SIGO CarPlay

Apple-side counterpart to the Android Auto module (`apps/android/auto`). Reuses the existing
`SIGOWidget` extension on the CarPlay dashboard and adds a Siri AppIntent for hands-free queries.

## What ships

- **Dashboard widget**: `systemSmall` family of `SIGOWidget`, visible on the CarPlay dashboard
  customization panel. Apple gates the appearance — iOS 26+ on stock CarPlay, earlier on
  CarPlay Ultra. Backed by `NSSupportsCarPlay = true` in `apps/ios/SIGOWidget/Info.plist`.
- **Siri AppIntent**: "Hey Siri, should I go outside with SIGO?" returns a spoken verdict and a
  small SwiftUI snippet (`SigoIntentSnippetView`). Implemented in
  `apps/ios/iosApp/Intents/SigoStatusIntent.swift` with `SigoAppShortcuts` registering the trigger
  phrases.

## Architecture

- `IntentDialogBuilder` (pure) assembles the spoken string from a `WidgetData?` plus an
  `IntentDialogTemplates`. Tested in `iosAppTests/Intents/IntentDialogBuilderTests.swift`.
- `BundledIntentDialogTemplates` reads localized templates from `apps/ios/iosApp/Localizable.strings`
  via `Bundle.localizedString(forKey:value:table:)`.
- `SigoStatusIntent.perform()` races `WidgetRefresher.shared.refresh()` against a 5s timer; on
  timeout, thrown error, or nil-return falls back to `WidgetRefresher.shared.loadCached()`. The
  intent never throws — Siri's generic "Sorry, I couldn't do that" is unacceptable UX.
- Bridge types (`WidgetData`, `WidgetColors`, `ScoreResult`, the `iosApp.WidgetData ->
  WidgetData` extension) live in `apps/ios/iosApp/Shared/`, shared between the main app target
  and the widget extension.

## Manual smoke test via CarPlay Simulator

1. Open `apps/ios/iosApp.xcodeproj` in Xcode.
2. Boot the iPhone simulator chosen for development (e.g. `iPhone 16 Pro`).
3. **Window → Devices and Simulators → Simulators → iPhone 16 Pro → I/O → External Displays → CarPlay**.
4. Install a debug build:
   `xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,id=$SIM_ID' build install`.

Verify:

- **Cold install, no saved location** → widget placeholder; Siri replies "Open SIGO on your phone
  first to set a location".
- **Saved location, fresh forecast** → widget shows score badge, location, temp, optional alert
  count; Siri replies with the verdict template and shows the snippet.
- **Add SIGO widget from CarPlay dashboard customization** → renders `SmallWidgetView`, no
  clipping.
- **Airplane mode after a successful fetch** → cached widget retained; Siri verdict appended with
  "(cached)".
- **Airplane mode with no cache** → Siri replies "Open SIGO on your phone first to set a location." (Both never-set-up and cache-cleared-then-network-failed produce the same prompt; distinguishing them requires a KMP probe not in scope for v1.)
- **iPhone Home Screen widget gallery** → existing `systemSmall` and `systemMedium` unaffected.

## App Store submission TODOs

- [ ] Upload `screenshots/carplay-dashboard.png` to App Store Connect under the iPhone listing
  (CarPlay screenshots share the iPhone tab today).
- [ ] Confirm AppShortcuts phrases pass review (all three include `\(.applicationName)`).
- [ ] No new privacy permissions required.

## Out of scope (v1)

- Native CarPlay scene with `CPListTemplate`/`CPGridTemplate`/`CPInformationTemplate`. Apple gates
  these behind category entitlements (Audio/Comm/Driving Task/EV/Fueling/Nav/Parking/Quick Food
  Ordering); weather is not eligible (RadarScope precedent).
- `accessoryRectangular`, `accessoryCircular`, `accessoryInline` widget families.
- CarPlay-specific render variant via `@Environment(\.widgetRenderingMode)`.
- Live Activity / Dynamic Island.
- Apple Watch complication.
- AppIntent parameters beyond the saved location.
- Filing a CarPlay app entitlement application with Apple.
