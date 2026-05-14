@testable import SIGOT

struct StubIntentDialogTemplates: IntentDialogTemplates {
    var yesTemplate: String   = "Y %1$@/%2$@"
    var maybeTemplate: String = "M %1$@/%2$@"
    var noTemplate: String    = "N %1$@/%2$@"
    var noLocationDialog: String  = "no-loc"
    var staleSuffix: String       = "STALE"
}
