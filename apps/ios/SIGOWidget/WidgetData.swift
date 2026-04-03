import Foundation

struct WidgetData: Codable {
    static let defaultActivityName = "General"

    let scoreResult: String
    let locationName: String
    let currentTemp: Double
    let tempUnit: String
    let feelsLikeTemp: Double
    let windSpeed: Double
    let windSpeedUnit: String
    let precipChance: Int
    let todayScoreResult: String
    let alertCount: Int
    let updatedAtMillis: Int64
    let activityName: String?

    var isStale: Bool {
        let twoHoursMs: Int64 = 2 * 60 * 60 * 1000
        let nowMs = Int64(Date().timeIntervalSince1970 * 1000)
        return (nowMs - updatedAtMillis) > twoHoursMs
    }

    var updatedAgo: String {
        let nowMs = Int64(Date().timeIntervalSince1970 * 1000)
        let diffMs = nowMs - updatedAtMillis
        let diffMinutes = diffMs / (60 * 1000)

        if diffMinutes < 60 {
            return "\(diffMinutes)m ago"
        }
        let diffHours = diffMinutes / 60
        return "\(diffHours)h ago"
    }

    var formattedTemp: String {
        let unit = String(tempUnit.prefix(1))
        return "\(Int(currentTemp.rounded()))°\(unit)"
    }

    var formattedFeelsLike: String {
        let unit = String(tempUnit.prefix(1))
        return "\(Int(feelsLikeTemp.rounded()))°\(unit)"
    }

    var formattedWind: String {
        let unitLabel: String
        switch windSpeedUnit {
        case "MilePerHour": unitLabel = "mph"
        case "KilometerPerHour": unitLabel = "km/h"
        case "MeterPerSecond": unitLabel = "m/s"
        default: unitLabel = windSpeedUnit
        }
        return "\(Int(windSpeed.rounded())) \(unitLabel)"
    }

    static func load() -> WidgetData? {
        guard let defaults = UserDefaults(suiteName: "group.now.shouldigooutside"),
              let jsonString = defaults.string(forKey: "widget_data_json"),
              let data = jsonString.data(using: .utf8) else {
            return nil
        }

        return try? JSONDecoder().decode(WidgetData.self, from: data)
    }
}
