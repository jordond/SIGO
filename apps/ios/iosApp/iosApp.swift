import SwiftUI
import Firebase
import WidgetKit
import iosApp

@main
struct ComposeApp: App {
    init() {
        FirebaseApp.configure()

        // Register widget update callback so KMP can trigger timeline reloads
        IosWidgetNotifier.companion.onUpdate = {
            WidgetCenter.shared.reloadAllTimelines()
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
                .onOpenURL { url in
                    // Handle widget deep link (sigo://forecast)
                    // The app opens to its default screen which is the forecast
                }
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Updates will be handled by Compose
    }
}
