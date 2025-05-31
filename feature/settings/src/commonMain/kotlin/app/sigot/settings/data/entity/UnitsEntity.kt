package app.sigot.settings.data.entity

import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.WindSpeedUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UnitsEntity(
    @SerialName("temperature")
    val temperature: String,
    @SerialName("precipitation")
    val precipitation: String,
    @SerialName("wind_speed")
    val windSpeed: String,
    @SerialName("pressure")
    val pressure: String,
)

internal fun Units.toEntity() =
    UnitsEntity(
        temperature = temperature.name,
        precipitation = precipitation.name,
        windSpeed = windSpeed.name,
        pressure = pressure.name,
    )

internal fun UnitsEntity.toModel() =
    Units(
        temperature = TemperatureUnit.valueOf(temperature),
        precipitation = PrecipitationUnit.valueOf(precipitation),
        windSpeed = WindSpeedUnit.valueOf(windSpeed),
        pressure = PressureUnit.valueOf(pressure),
    )
