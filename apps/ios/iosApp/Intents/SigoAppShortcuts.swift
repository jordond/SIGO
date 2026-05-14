import AppIntents

struct SigoAppShortcuts: AppShortcutsProvider {
    static var appShortcuts: [AppShortcut] {
        AppShortcut(
            intent: SigoStatusIntent(),
            phrases: [
                "Should I go outside with \(.applicationName)?",
                "What's my \(.applicationName) score?",
                "\(.applicationName) score",
            ],
            shortTitle: "Should I go outside?",
            systemImageName: "sun.max.fill"
        )
    }
}
