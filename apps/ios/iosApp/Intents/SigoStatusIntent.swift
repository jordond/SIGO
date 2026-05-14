import AppIntents
import Foundation
import iosApp

struct SigoStatusIntent: AppIntent {
    static var title: LocalizedStringResource = "Should I go outside?"
    static var description = IntentDescription(
        "Reads your current SIGO score for your saved location."
    )
    static var openAppWhenRun: Bool = false

    func perform() async throws -> some ProvidesDialog & ShowsSnippetView {
        let data = await Self.refreshWithBudget()
        let dialog = IntentDialogBuilder.dialog(data: data)
        return .result(dialog: dialog) {
            SigoIntentSnippetView(data: data)
        }
    }

    /// Races KMP `WidgetRefresher.refresh()` against a 5s timer. If refresh
    /// succeeds within budget, use the fresh value. Otherwise (timeout, thrown
    /// error, or refresh-returns-nil) fall back to `loadCached()` which itself
    /// may be nil. `perform()` MUST NOT throw — Siri's generic "Sorry, I
    /// couldn't do that" is unacceptable UX.
    private static func refreshWithBudget() async -> WidgetData? {
        let raced: iosApp.WidgetData? = await withTaskGroup(
            of: iosApp.WidgetData?.self
        ) { group in
            group.addTask {
                try? await WidgetRefresher.shared.refresh()
            }
            group.addTask {
                try? await Task.sleep(nanoseconds: 5_000_000_000)
                return nil
            }
            defer { group.cancelAll() }
            if let inner = await group.next() { return inner } else { return nil }
        }
        let kotlinData = raced ?? WidgetRefresher.shared.loadCached()
        return kotlinData?.toSwiftWidgetData()
    }
}
