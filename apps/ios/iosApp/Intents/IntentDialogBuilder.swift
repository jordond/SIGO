import AppIntents
import Foundation

enum IntentDialogBuilder {
    /// Pure assembly used by tests and by the AppIntent. Returns the verbatim
    /// string Siri will speak.
    static func spokenString(
        data: WidgetData?,
        templates: IntentDialogTemplates,
        locale: Locale = .current
    ) -> String {
        guard let data else {
            return templates.noLocationDialog
        }
        let template: String
        switch data.scoreResult {
        case .Yes:   template = templates.yesTemplate
        case .Maybe: template = templates.maybeTemplate
        case .No:    template = templates.noTemplate
        }
        let body = String(format: template, locale: locale, data.locationName, data.formattedTemp)
        return data.isStale ? body + " " + templates.staleSuffix : body
    }

    /// Convenience used by `SigoStatusIntent.perform()` only.
    static func dialog(data: WidgetData?, bundle: Bundle = .main) -> IntentDialog {
        let text = spokenString(
            data: data,
            templates: BundledIntentDialogTemplates(bundle: bundle)
        )
        return IntentDialog(stringLiteral: text)
    }
}
