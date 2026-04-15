import Foundation

enum ScoreResult: String {
    case Yes, Maybe, No
}

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
