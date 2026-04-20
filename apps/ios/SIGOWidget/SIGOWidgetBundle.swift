import WidgetKit
import SwiftUI
import FirebaseCore

@main
struct SIGOWidgetBundle: WidgetBundle {
    init() {
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
    }

    var body: some Widget {
        SIGOWidget()
    }
}
