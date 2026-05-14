import Foundation

enum IntentDialogL10nKey {
    static let yes         = "siri_intent_yes_dialog"
    static let maybe       = "siri_intent_maybe_dialog"
    static let no          = "siri_intent_no_dialog"
    static let noLocation  = "siri_intent_no_location_dialog"
    static let staleSuffix = "siri_intent_stale_suffix"
}

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

    var yesTemplate: String       { localized(IntentDialogL10nKey.yes) }
    var maybeTemplate: String     { localized(IntentDialogL10nKey.maybe) }
    var noTemplate: String        { localized(IntentDialogL10nKey.no) }
    var noLocationDialog: String  { localized(IntentDialogL10nKey.noLocation) }
    var staleSuffix: String       { localized(IntentDialogL10nKey.staleSuffix) }

    private func localized(_ key: String) -> String {
        bundle.localizedString(forKey: key, value: key, table: nil)
    }
}
