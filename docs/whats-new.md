# What's New System

Shows a bottom sheet to returning users after app updates. Fresh installs skip it.
Pages are shown in a swipeable pager with a dot indicator. Each page supports an optional image.

To test, use the "Show What's New" button in Internal Settings.

## Adding Content

1. Add string resources to `core/resources/.../strings.xml`:

```xml
<string name="whats_new_dark_mode">Dark Mode</string>
<string name="whats_new_dark_mode_desc">The app now supports dark mode. Toggle it in settings.</string>
```

2. Add pages to `WhatsNewRegistry.pages` (newest first). The `version` must match the build number
   in `gradle/libs.versions.toml`:

```kotlin
WhatsNewPage(
    version = 15,
    title = Res.string.whats_new_dark_mode,
    description = Res.string.whats_new_dark_mode_desc,
    image = Res.drawable.whats_new_dark_mode, // optional
),
```

Multiple pages with the same `version` are shown together in the pager. Single-page versions
skip the pager and show the content directly.

Users who have not seen pages for that build number will get the sheet on their next home screen
visit. If a user skips several versions, all unseen pages are shown together.
