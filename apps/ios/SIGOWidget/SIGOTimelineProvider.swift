import WidgetKit
import iosApp

struct SIGOTimelineProvider: TimelineProvider {
    private static let freshRefreshMinutes = 30
    private static let staleRefreshMinutes = 15

    func placeholder(in context: Context) -> SIGOWidgetEntry {
        .placeholder
    }

    func getSnapshot(in context: Context, completion: @escaping (SIGOWidgetEntry) -> Void) {
        completion(SIGOWidgetEntry(date: .now, data: loadCached()))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SIGOWidgetEntry>) -> Void) {
        Task {
            let kotlinData = try? await WidgetRefresher.shared.refresh()
            let data = kotlinData?.toSwiftWidgetData() ?? loadCached()
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

    private func loadCached() -> WidgetData? {
        WidgetRefresher.shared.loadCached()?.toSwiftWidgetData()
    }
}
