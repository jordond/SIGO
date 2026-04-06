import Foundation

enum ScoreResult: String, Codable {
    case Yes, Maybe, No
}

struct WidgetData: Codable {
    let scoreResult: ScoreResult
    let locationName: String
    let formattedTemp: String
    let formattedFeelsLike: String
    let formattedWind: String
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
            return String(localized: "\(diffMinutes)m ago")
        }
        let diffHours = diffMinutes / 60
        return String(localized: "\(diffHours)h ago")
    }

    func save() {
        guard let defaults = UserDefaults(suiteName: "group.now.shouldigooutside"),
              let data = try? JSONEncoder().encode(self),
              let jsonString = String(data: data, encoding: .utf8) else {
            return
        }
        defaults.set(jsonString, forKey: "widget_data_json")
    }

    static func load() -> WidgetData? {
        guard let defaults = UserDefaults(suiteName: "group.now.shouldigooutside"),
              let jsonString = defaults.string(forKey: "widget_data_json"),
              let data = jsonString.data(using: .utf8) else {
            return nil
        }

        return try? JSONDecoder().decode(WidgetData.self, from: data)
    }

    static let sample = WidgetData(
        scoreResult: .Yes,
        locationName: "Toronto",
        formattedTemp: "22°C",
        formattedFeelsLike: "20°C",
        formattedWind: "12 km/h",
        precipChance: 10,
        todayScoreResult: .Yes,
        alertCount: 0,
        updatedAtMillis: Int64(Date().timeIntervalSince1970 * 1000),
        activityName: nil
    )
}
