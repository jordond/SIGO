import WidgetKit
import SwiftUI

struct SIGOWidget: Widget {
    let kind: String = "SIGOWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: SIGOTimelineProvider()) { entry in
            SIGOWidgetEntryView(entry: entry)
        }
        .configurationDisplayName(String(localized: "widget_display_name"))
        .description(String(localized: "widget_description"))
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

struct SIGOWidgetEntryView: View {
    var entry: SIGOWidgetEntry
    @Environment(\.widgetFamily) var family

    var body: some View {
        Group {
            switch family {
            case .systemMedium:
                MediumWidgetView(data: entry.data)
            default:
                SmallWidgetView(data: entry.data)
            }
        }
        .widgetURL(URL(string: "sigo://forecast"))
    }
}

#Preview(as: .systemSmall) {
    SIGOWidget()
} timeline: {
    SIGOWidgetEntry.placeholder
}

#Preview(as: .systemMedium) {
    SIGOWidget()
} timeline: {
    SIGOWidgetEntry.placeholder
}
