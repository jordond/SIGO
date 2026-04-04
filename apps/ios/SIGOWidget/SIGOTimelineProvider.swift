import WidgetKit

struct SIGOTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> SIGOWidgetEntry {
        .placeholder
    }

    func getSnapshot(in context: Context, completion: @escaping (SIGOWidgetEntry) -> Void) {
        // Snapshots should be fast — use cached data
        let entry = SIGOWidgetEntry(
            date: .now,
            data: WidgetData.load()
        )
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SIGOWidgetEntry>) -> Void) {
        Task {
            // Try to fetch fresh data from the backend
            let data = await WidgetDataService.fetch() ?? WidgetData.load()

            let entry = SIGOWidgetEntry(
                date: .now,
                data: data
            )

            // If data is stale, refresh sooner (15 min instead of 30)
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
}
