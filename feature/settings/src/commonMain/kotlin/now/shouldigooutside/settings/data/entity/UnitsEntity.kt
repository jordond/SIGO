package now.shouldigooutside.settings.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.units.PrecipitationUnit
import now.shouldigooutside.core.model.units.PressureUnit
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit

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
