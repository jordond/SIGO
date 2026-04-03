import Foundation

enum ScoreResult: String, Codable {
    case Yes, Maybe, No
}

enum TemperatureUnit: String, Codable {
    case Kelvin, Celsius, Fahrenheit
}

enum WindSpeedUnit: String, Codable {
    case MeterPerSecond, KilometerPerHour, MilePerHour, Knot

    var label: String {
        switch self {
        case .MilePerHour: return "mph"
        case .KilometerPerHour: return "km/h"
        case .MeterPerSecond: return "m/s"
        case .Knot: return "kn"
        }
    }
}

struct WidgetData: Codable {
    static let defaultActivityName = "General"

    let scoreResult: ScoreResult
    let locationName: String
    let currentTemp: Double
    let tempUnit: TemperatureUnit
    let feelsLikeTemp: Double
    let windSpeed: Double
    let windSpeedUnit: WindSpeedUnit
    let precipChance: Int
    let todayScoreResult: ScoreResult
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
        let unit = String(tempUnit.rawValue.prefix(1))
        return "\(Int(currentTemp.rounded()))°\(unit)"
    }

    var formattedFeelsLike: String {
        let unit = String(tempUnit.rawValue.prefix(1))
        return "\(Int(feelsLikeTemp.rounded()))°\(unit)"
    }

    var formattedWind: String {
        return "\(Int(windSpeed.rounded())) \(windSpeedUnit.label)"
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
