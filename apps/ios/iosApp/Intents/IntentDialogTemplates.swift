import Foundation

protocol IntentDialogTemplates {
    var yesTemplate: String { get }
    var maybeTemplate: String { get }
    var noTemplate: String { get }
    var noLocationDialog: String { get }
    var staleSuffix: String { get }
}

struct BundledIntentDialogTemplates: IntentDialogTemplates {
    let bundle: Bundle

    init(bundle: Bundle = .main) {
        self.bundle = bundle
    }

    var yesTemplate: String       { localized("siri_intent_yes_dialog") }
    var maybeTemplate: String     { localized("siri_intent_maybe_dialog") }
    var noTemplate: String        { localized("siri_intent_no_dialog") }
    var noLocationDialog: String  { localized("siri_intent_no_location_dialog") }
    var staleSuffix: String       { localized("siri_intent_stale_suffix") }

    private func localized(_ key: String) -> String {
        bundle.localizedString(forKey: key, value: key, table: nil)
    }
}
