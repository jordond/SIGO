import Foundation
import iosApp

struct WidgetData {
    let scoreResult: ScoreResult
    let scoreLabel: String
    let locationName: String
    let formattedTemp: String
    let formattedFeelsLike: String
    let formattedWind: String
    let precipChance: Int
    let todayScoreResult: ScoreResult
    let todayScoreLabel: String
    let alertCount: Int
    let updatedAtMillis: Int64
    let isStale: Bool
    let updatedAgoLabel: String
    let activityName: String?

    static let sample = WidgetData(
        scoreResult: .Yes,
        scoreLabel: "YES",
        locationName: "Toronto",
        formattedTemp: "22°C",
        formattedFeelsLike: "20°C",
        formattedWind: "12 km/h",
        precipChance: 10,
        todayScoreResult: .Yes,
        todayScoreLabel: "Yes",
        alertCount: 0,
        updatedAtMillis: Int64(Date().timeIntervalSince1970 * 1000),
        isStale: false,
        updatedAgoLabel: "",
        activityName: nil
    )
}

extension iosApp.WidgetData {
    func toSwiftWidgetData() -> WidgetData {
        WidgetData(
            scoreResult: ScoreResult.from(kmpName: scoreResult.name),
            scoreLabel: scoreLabel,
            locationName: locationName,
            formattedTemp: formattedTemp,
            formattedFeelsLike: formattedFeelsLike,
            formattedWind: formattedWind,
            precipChance: Int(precipChance),
            todayScoreResult: ScoreResult.from(kmpName: todayScoreResult.name),
            todayScoreLabel: todayScoreLabel,
            alertCount: Int(alertCount),
            updatedAtMillis: updatedAtMillis,
            isStale: isStale,
            updatedAgoLabel: updatedAgoLabel,
            activityName: activityName
        )
    }
}
