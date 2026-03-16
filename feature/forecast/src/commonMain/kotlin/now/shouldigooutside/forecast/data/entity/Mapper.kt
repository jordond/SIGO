package now.shouldigooutside.forecast.data.entity

import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastDay
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.location.Location
import kotlin.time.Instant

public fun ForecastEntity.toModel(): Forecast =
    Forecast(
        location = Location(
            latitude = locationLat,
            longitude = locationLong,
            name = locationName,
            administrativeArea = locationAdministrativeArea,
            country = locationCountry,
        ),
        current = current.toModel(),
        today = ForecastDay(
            block = today.block.toModel(),
            hours = today.hours.map { it.toModel() },
        ),
        days = daily.map { day ->
            ForecastDay(
                block = day.block.toModel(),
                hours = day.hours.map { it.toModel() },
            )
        },
        alerts = alerts.map { Alert(it.title, it.description) },
        instant = Instant.fromEpochMilliseconds(updatedAt),
    )

public fun Forecast.toEntity(): ForecastEntity =
    ForecastEntity(
        locationLat = location.latitude,
        locationLong = location.longitude,
        locationName = location.name,
        locationAdministrativeArea = location.administrativeArea,
        locationCountry = location.country,
        current = current.toEntity(),
        today = ForecastDayEntity(
            block = today.block.toEntity(),
            hours = today.hours.map { it.toEntity() },
        ),
        daily = days.map { day ->
            ForecastDayEntity(
                block = day.block.toEntity(),
                hours = day.hours.map { it.toEntity() },
            )
        },
        alerts = alerts.map { AlertEntity(it.title, it.description) },
        updatedAt = instant.toEpochMilliseconds(),
    )

private fun ForecastBlockEntity.toModel(): ForecastBlock =
    ForecastBlock(
        instant = Instant.fromEpochMilliseconds(instant),
        humidity = humidity,
        cloudCoverPercent = cloudCoverPercent,
        temperature = Temperature(
            value = temperature.value,
            feelsLike = temperature.feelsLike,
            max = temperature.max,
            min = temperature.min,
        ),
        precipitation = Precipitation(
            amount = precipitation.amount,
            probability = precipitation.probability,
            types = precipitation.types
                .mapNotNull { type ->
                    runCatching { PrecipitationType.valueOf(type) }.getOrNull()
                }.toSet(),
        ),
        wind = Wind(
            speed = wind.speed,
            gust = wind.gust,
            directionDegree = wind.directionDegree,
            maxSpeed = wind.maxSpeed,
            meanSpeed = wind.meanSpeed,
            minSpeed = wind.minSpeed,
        ),
        pressure = pressure,
        uvIndex = uvIndex,
        visibility = visibility,
        severeWeatherRisk = runCatching { SevereWeatherRisk.valueOf(severeWeatherRisk) }
            .getOrDefault(SevereWeatherRisk.Low),
    )

private fun ForecastBlock.toEntity(): ForecastBlockEntity =
    ForecastBlockEntity(
        instant = instant.toEpochMilliseconds(),
        humidity = humidity,
        cloudCoverPercent = cloudCoverPercent,
        temperature = TemperatureEntity(
            value = temperature.value,
            feelsLike = temperature.feelsLike,
            max = temperature.max,
            min = temperature.min,
        ),
        precipitation = PrecipitationEntity(
            amount = precipitation.amount,
            probability = precipitation.probability,
            types = precipitation.types.map { it.name },
        ),
        wind = WindEntity(
            speed = wind.speed,
            gust = wind.gust,
            directionDegree = wind.directionDegree,
            maxSpeed = wind.maxSpeed,
            meanSpeed = wind.meanSpeed,
            minSpeed = wind.minSpeed,
        ),
        pressure = pressure,
        uvIndex = uvIndex,
        visibility = visibility,
        severeWeatherRisk = severeWeatherRisk.name,
    )
