import Foundation

struct WidgetConfig: Codable {
    let backendUrl: String
    let clientId: String
    let lat: Double
    let lon: Double
    let locationName: String
    let minTemp: Int
    let maxTemp: Int
    let maxWind: Int
    let allowRain: Bool
    let allowSnow: Bool
    let maxAqi: Int
    let includeAirQuality: Bool
    let activityName: String?

    static func load() -> WidgetConfig? {
        guard let defaults = UserDefaults(suiteName: "group.now.shouldigooutside"),
              let jsonString = defaults.string(forKey: "widget_config_json"),
              let data = jsonString.data(using: .utf8) else {
            return nil
        }
        return try? JSONDecoder().decode(WidgetConfig.self, from: data)
    }
}
