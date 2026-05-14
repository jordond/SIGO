@testable import SIGOT

enum WidgetDataFixtures {
    static func make(
        scoreResult: ScoreResult = .Yes,
        locationName: String = "Toronto",
        formattedTemp: String = "22°C",
        isStale: Bool = false
    ) -> WidgetData {
        WidgetData(
            scoreResult: scoreResult,
            scoreLabel: "ignored",
            locationName: locationName,
            formattedTemp: formattedTemp,
            formattedFeelsLike: "ignored",
            formattedWind: "ignored",
            precipChance: 0,
            todayScoreResult: scoreResult,
            todayScoreLabel: "ignored",
            alertCount: 0,
            updatedAtMillis: 0,
            isStale: isStale,
            updatedAgoLabel: "ignored",
            activityName: nil
        )
    }
}
