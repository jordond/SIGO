package app.sigot.forecast.data.source.cache.entity

import app.sigot.core.model.forecast.Alert
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.ForecastDay
import app.sigot.core.model.forecast.Precipitation
import app.sigot.core.model.forecast.PrecipitationType
import app.sigot.core.model.forecast.SevereWeatherRisk
import app.sigot.core.model.forecast.Temperature
import app.sigot.core.model.forecast.Wind
import app.sigot.core.model.location.Location
import kotlinx.datetime.Instant

internal fun ForecastEntity.toModel(): Forecast =
    Forecast(
        location = Location(locationLat, locationLong, locationName),
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

internal fun Forecast.toEntity(): ForecastEntity =
    ForecastEntity(
        locationLat = location.latitude,
        locationLong = location.longitude,
        locationName = location.name,
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
