import Foundation

// MARK: - API Response Models

struct ApiResponse<T: Decodable>: Decodable {
    let data: T
}

struct ForecastScoreResponse: Decodable {
    let forecast: ForecastResponse
    let score: ForecastScoreData
}

struct ForecastResponse: Decodable {
    let locationName: String
    let current: ForecastBlock
    let today: ForecastDay
    let alerts: [AlertData]

    enum CodingKeys: String, CodingKey {
        case locationName = "location_name"
        case current
        case today
        case alerts
    }
}

struct ForecastDay: Decodable {
    let block: ForecastBlock
}

struct ForecastBlock: Decodable {
    let temperature: TemperatureData
    let precipitation: PrecipitationData
    let wind: WindData

    enum CodingKeys: String, CodingKey {
        case temperature
        case precipitation
        case wind
    }
}

struct TemperatureData: Decodable {
    let value: Double
    let feelsLike: Double

    enum CodingKeys: String, CodingKey {
        case value
        case feelsLike = "feels_like"
    }
}

struct PrecipitationData: Decodable {
    let amount: Double
    let probability: Int

    enum CodingKeys: String, CodingKey {
        case amount = "value"
        case probability
    }
}

struct WindData: Decodable {
    let speed: Double
}

struct AlertData: Decodable {
    let title: String
}

struct ForecastScoreData: Decodable {
    let current: ScoreData
    let today: ScoreData
}

struct ScoreData: Decodable {
    let result: ScoreResult
}

// MARK: - Service

enum WidgetDataServiceError: Error {
    case noConfig
    case invalidURL
    case networkError(Error)
    case decodingError(Error)
}

struct WidgetDataService {

    static func fetch() async -> WidgetData? {
        guard let config = WidgetConfig.load() else {
            return WidgetData.load()
        }

        do {
            let data = try await fetchFromBackend(config: config)
            data.save()
            return data
        } catch {
            return WidgetData.load()
        }
    }

    private static func fetchFromBackend(config: WidgetConfig) async throws -> WidgetData {
        var components = URLComponents(string: "\(config.backendUrl)/forecast/score")
        components?.queryItems = [
            URLQueryItem(name: "lat", value: String(config.lat)),
            URLQueryItem(name: "lon", value: String(config.lon)),
            URLQueryItem(name: "name", value: config.locationName),
            URLQueryItem(name: "min_temp", value: String(config.minTemp)),
            URLQueryItem(name: "max_temp", value: String(config.maxTemp)),
            URLQueryItem(name: "max_wind", value: String(config.maxWind)),
            URLQueryItem(name: "allow_rain", value: String(config.allowRain)),
            URLQueryItem(name: "allow_snow", value: String(config.allowSnow)),
            URLQueryItem(name: "max_aqi", value: String(config.maxAqi)),
            URLQueryItem(name: "include_air_quality", value: String(config.includeAirQuality)),
        ]

        guard let url = components?.url else {
            throw WidgetDataServiceError.invalidURL
        }

        var request = URLRequest(url: url)
        request.setValue(config.clientId, forHTTPHeaderField: "X-Client-ID")
        request.timeoutInterval = 15

        let (data, _) = try await URLSession.shared.data(for: request)

        let decoder = JSONDecoder()
        let response = try decoder.decode(ApiResponse<ForecastScoreResponse>.self, from: data)
        return mapToWidgetData(response: response.data, config: config)
    }

    private static func mapToWidgetData(
        response: ForecastScoreResponse,
        config: WidgetConfig
    ) -> WidgetData {
        let current = response.forecast.current

        // API returns metric (Celsius, m/s) — map to the display units stored in cached WidgetData
        // For now, use the cached WidgetData's units as reference, or default to Celsius/KilometerPerHour
        let cachedData = WidgetData.load()
        let tempUnit = cachedData?.tempUnit ?? .Celsius
        let windUnit = cachedData?.windSpeedUnit ?? .KilometerPerHour

        let temp = convertTemperature(current.temperature.value, to: tempUnit)
        let feelsLike = convertTemperature(current.temperature.feelsLike, to: tempUnit)
        let wind = convertWindSpeed(current.wind.speed, to: windUnit)

        return WidgetData(
            scoreResult: response.score.current.result,
            locationName: response.forecast.locationName,
            currentTemp: temp,
            tempUnit: tempUnit,
            feelsLikeTemp: feelsLike,
            windSpeed: wind,
            windSpeedUnit: windUnit,
            precipChance: current.precipitation.probability,
            todayScoreResult: response.score.today.result,
            alertCount: response.forecast.alerts.count,
            updatedAtMillis: Int64(Date().timeIntervalSince1970 * 1000),
            activityName: config.activityName
        )
    }

    // API returns Kelvin (unitGroup=base from VisualCrossing)
    private static func convertTemperature(_ kelvin: Double, to unit: TemperatureUnit) -> Double {
        switch unit {
        case .Kelvin: return kelvin
        case .Celsius: return kelvin - 273.15
        case .Fahrenheit: return (kelvin - 273.15) * 9.0 / 5.0 + 32.0
        }
    }

    // API returns m/s
    private static func convertWindSpeed(_ mps: Double, to unit: WindSpeedUnit) -> Double {
        switch unit {
        case .MeterPerSecond: return mps
        case .KilometerPerHour: return mps * 3.6
        case .MilePerHour: return mps * 2.23694
        case .Knot: return mps * 1.94384
        }
    }
}
