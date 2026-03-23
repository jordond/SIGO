import WidgetKit

struct SIGOWidgetEntry: TimelineEntry {
    let date: Date
    let data: WidgetData?

    static var placeholder: SIGOWidgetEntry {
        SIGOWidgetEntry(date: .now, data: nil)
    }
}
