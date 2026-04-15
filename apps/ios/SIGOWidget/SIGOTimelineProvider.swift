import WidgetKit
import iosApp

struct SIGOTimelineProvider: TimelineProvider {
    private static let freshRefreshMinutes = 30
    private static let staleRefreshMinutes = 15

    func placeholder(in context: Context) -> SIGOWidgetEntry {
        .placeholder
    }

    func getSnapshot(in context: Context, completion: @escaping (SIGOWidgetEntry) -> Void) {
        Task {
            let data = await loadCached()
            completion(SIGOWidgetEntry(date: .now, data: data))
        }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SIGOWidgetEntry>) -> Void) {
        Task {
            let kotlinData = try? await WidgetRefresher.shared.refresh()
            let data = kotlinData?.toSwiftWidgetData() ?? (await loadCached())
            let entry = SIGOWidgetEntry(date: .now, data: data)

            let refreshMinutes = (data?.isStale == true)
                ? Self.staleRefreshMinutes
                : Self.freshRefreshMinutes
            let nextUpdate = Calendar.current.date(
                byAdding: .minute,
                value: refreshMinutes,
                to: .now
            ) ?? .now

            completion(Timeline(entries: [entry], policy: .after(nextUpdate)))
        }
    }

    private func loadCached() async -> WidgetData? {
        IosWidgetDataStore().load()?.toSwiftWidgetData()
    }
}

private extension iosApp.WidgetData {
    func toSwiftWidgetData() -> WidgetData {
        WidgetData(
            scoreResult: ScoreResult(rawValue: scoreResult.name) ?? .Maybe,
            scoreLabel: scoreLabel,
            locationName: locationName,
            formattedTemp: formattedTemp,
            formattedFeelsLike: formattedFeelsLike,
            formattedWind: formattedWind,
            precipChance: Int(precipChance),
            todayScoreResult: ScoreResult(rawValue: todayScoreResult.name) ?? .Maybe,
            todayScoreLabel: todayScoreLabel,
            alertCount: Int(alertCount),
            updatedAtMillis: updatedAtMillis,
            isStale: isStale,
            updatedAgoLabel: updatedAgoLabel,
            activityName: activityName
        )
    }
}
