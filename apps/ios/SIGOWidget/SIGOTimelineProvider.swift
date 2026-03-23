import WidgetKit

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
        let entry = SIGOWidgetEntry(
            date: .now,
            data: WidgetData.load()
        )

        // Refresh every 30 minutes
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: .now) ?? .now
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}
