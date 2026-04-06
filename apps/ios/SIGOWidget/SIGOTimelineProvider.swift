import WidgetKit
import iosApp

struct SIGOTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> SIGOWidgetEntry {
        .placeholder
    }

    func getSnapshot(in context: Context, completion: @escaping (SIGOWidgetEntry) -> Void) {
        let entry = SIGOWidgetEntry(
            date: .now,
            data: WidgetData.load()
        )
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SIGOWidgetEntry>) -> Void) {
        // WidgetRefresher handles everything in KMP: Koin init, settings, forecast fetch, scoring
        let kotlinData = WidgetRefresher.shared.refresh()

        // Convert Kotlin WidgetData to Swift WidgetData, or fall back to cached
        let data = kotlinData?.toSwiftWidgetData() ?? WidgetData.load()

        let entry = SIGOWidgetEntry(
            date: .now,
            data: data
        )

        let refreshMinutes = data?.isStale == true ? 15 : 30
        let nextUpdate = Calendar.current.date(
            byAdding: .minute,
            value: refreshMinutes,
            to: .now
        ) ?? .now

        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}

// Bridge from Kotlin WidgetData to Swift WidgetData
// The Kotlin side handles all business logic; this just maps types for SwiftUI views.
private extension iosApp.WidgetData {
    func toSwiftWidgetData() -> WidgetData {
        WidgetData(
            scoreResult: ScoreResult(rawValue: scoreResult.name) ?? .Maybe,
            locationName: locationName,
            formattedTemp: formattedTemp,
            formattedFeelsLike: formattedFeelsLike,
            formattedWind: formattedWind,
            precipChance: Int(precipChance),
            todayScoreResult: ScoreResult(rawValue: todayScoreResult.name) ?? .Maybe,
            alertCount: Int(alertCount),
            updatedAtMillis: updatedAtMillis,
            activityName: activityName
        )
    }
}
