package app.sigot.forecast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.back
import app.sigot.core.resources.forecast_details_cloud_cover
import app.sigot.core.resources.forecast_details_feels_like
import app.sigot.core.resources.forecast_details_high
import app.sigot.core.resources.forecast_details_humidity
import app.sigot.core.resources.forecast_details_low
import app.sigot.core.resources.forecast_details_precipitation
import app.sigot.core.resources.forecast_details_precipitation_chance
import app.sigot.core.resources.forecast_details_pressure
import app.sigot.core.resources.forecast_details_title
import app.sigot.core.resources.forecast_details_uv_index
import app.sigot.core.resources.forecast_details_visibility
import app.sigot.core.resources.forecast_details_wind
import app.sigot.core.resources.forecast_details_wind_gust
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowBigDown
import app.sigot.core.ui.icons.lucide.CloudRain
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.icons.lucide.Sun
import app.sigot.core.ui.icons.lucide.Waves
import app.sigot.core.ui.icons.lucide.Wind
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.ktx.text
import app.sigot.core.ui.mappers.units.rememberUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import dev.stateholder.extensions.collectAsState
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

// --- Connected Composable ---

@Composable
internal fun ForecastDetailsScreen(
    onBack: () -> Unit,
    model: ForecastDetailsModel = koinViewModel(),
) {
    val state by model.collectAsState()
    ForecastDetailsScreen(
        locationName = state.locationName,
        currentBlock = state.currentBlock,
        todayBlock = state.todayBlock,
        hours = state.hours,
        selectedHourIndex = state.selectedHourIndex,
        selectedBlock = state.selectedBlock,
        units = state.units,
        onHourSelected = model::selectHour,
        onBack = onBack,
    )
}

// --- Stateless Composable ---

@Composable
internal fun ForecastDetailsScreen(
    locationName: String,
    currentBlock: ForecastBlock?,
    todayBlock: ForecastBlock?,
    hours: List<ForecastBlock>,
    selectedHourIndex: Int?,
    selectedBlock: ForecastBlock?,
    units: Units,
    onHourSelected: (Int?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            DetailsTopBar(
                locationName = locationName,
                onBack = onBack,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        if (currentBlock == null || todayBlock == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = AppTheme.spacing.standard),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            CurrentConditionsHero(
                block = currentBlock,
                todayBlock = todayBlock,
                units = units,
            )

            Spacer(modifier = Modifier.height(20.dp))

            HourlyForecastStrip(
                hours = hours,
                selectedIndex = selectedHourIndex,
                units = units,
                onHourSelected = onHourSelected,
            )

            Spacer(modifier = Modifier.height(20.dp))

            selectedBlock?.let { block ->
                WeatherDetailsGrid(
                    block = block,
                    units = units,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Top Bar ---

@Composable
private fun DetailsTopBar(
    locationName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = Res.string.forecast_details_title.get(),
                    style = AppTheme.typography.h1,
                )
                if (locationName.isNotEmpty()) {
                    Text(
                        text = locationName,
                        style = AppTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                variant = IconButtonVariant.Outlined,
            ) {
                Icon(
                    icon = AppIcons.Lucide.ArrowBigDown,
                    contentDescription = Res.string.back.get(),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalContainerColor.current,
            navigationIconContentColor = LocalContentColor.current,
            titleContentColor = LocalContentColor.current,
        ),
    )
}

// --- Current Conditions Hero ---

@Composable
private fun CurrentConditionsHero(
    block: ForecastBlock,
    todayBlock: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Current temperature - large
            Text(
                text = "${block.temperature.value.roundToInt()}$tempUnit",
                style = AppTheme.typography.h1.copy(fontSize = 64.sp),
            )

            // Feels like
            Text(
                text = "${Res.string.forecast_details_feels_like.get()} ${block.temperature.feelsLike.roundToInt()}$tempUnit",
                style = AppTheme.typography.body1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // High / Low
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "${Res.string.forecast_details_high.get()} ${todayBlock.temperature.max.roundToInt()}$tempUnit",
                    style = AppTheme.typography.h3,
                )
                Text(
                    text = "${Res.string.forecast_details_low.get()} ${todayBlock.temperature.min.roundToInt()}$tempUnit",
                    style = AppTheme.typography.h3,
                )
            }
        }
    }
}

// --- Hourly Forecast Strip ---

@Composable
private fun HourlyForecastStrip(
    hours: List<ForecastBlock>,
    selectedIndex: Int?,
    units: Units,
    onHourSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(hours) { index, hour ->
            val isSelected = selectedIndex == index
            val colors = if (isSelected) {
                AppTheme.colors.brutal.yellow
            } else {
                AppTheme.colors.brutal.blue
            }

            HourCard(
                block = hour,
                tempUnit = tempUnit,
                colors = colors,
                isSelected = isSelected,
                onClick = {
                    onHourSelected(if (isSelected) null else index)
                },
            )
        }
    }
}

@Composable
private fun HourCard(
    block: ForecastBlock,
    tempUnit: String,
    colors: BrutalColors,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val localTime = remember(block.instant) {
        LocalTime(hour = block.instant.toLocalDateTime(TimeZone.currentSystemDefault()).hour, minute = 0)
    }
    val hour = localTime.text()

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.width(80.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(if (isSelected) colors.bright else Color.Transparent)
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
        ) {
            Text(
                text = hour,
                style = AppTheme.typography.h4,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${block.temperature.value.roundToInt()}$tempUnit",
                style = AppTheme.typography.h3,
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (block.precipitation.probability > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        icon = AppIcons.Lucide.Droplet,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = "${block.precipitation.probability}%",
                        style = AppTheme.typography.body1.copy(fontSize = 11.sp),
                    )
                }
            }
        }
    }
}

// --- Weather Details Grid ---

@Composable
private fun WeatherDetailsGrid(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()
    val windUnit = units.windSpeed.rememberUnit()
    val pressureUnit = units.pressure.rememberUnit()
    val precipUnit = units.precipitation.rememberUnit()

    // Wind direction
    val windDirection = remember(block.wind.directionDegree) {
        degreesToCardinal(block.wind.directionDegree)
    }

    // Visibility formatting - raw value is in km
    val visibilityText = remember(block.visibility, units.pressure) {
        // Use pressure unit to infer metric vs imperial for distance
        if (units.pressure == PressureUnit.InchMercury) {
            // Imperial - convert km to miles
            val miles = block.visibility * 0.621371
            "${miles.roundToInt()} mi"
        } else {
            "${block.visibility.roundToInt()} km"
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        // Row 1: Humidity + Wind
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_humidity.get(),
                value = "${block.humidity.roundToInt()}%",
                icon = AppIcons.Lucide.Droplet,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )
            WeatherDetailTile(
                title = Res.string.forecast_details_wind.get(),
                value = "${block.wind.speed.roundToInt()} $windUnit",
                subtitle = "${Res.string.forecast_details_wind_gust.get()} ${block.wind.gust.roundToInt()} $windUnit $windDirection",
                icon = AppIcons.Lucide.Wind,
                colors = AppTheme.colors.brutal.pink,
                modifier = Modifier.weight(1f),
            )
        }

        // Row 2: UV Index + Pressure
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_uv_index.get(),
                value = "${block.uvIndex}",
                subtitle = uvIndexLabel(block.uvIndex),
                icon = AppIcons.Lucide.Sun,
                colors = AppTheme.colors.brutal.yellow,
                modifier = Modifier.weight(1f),
            )
            WeatherDetailTile(
                title = Res.string.forecast_details_pressure.get(),
                value = "${block.pressure.roundToInt()} $pressureUnit",
                icon = AppIcons.Lucide.Waves,
                colors = AppTheme.colors.brutal.green,
                modifier = Modifier.weight(1f),
            )
        }

        // Row 3: Visibility + Cloud Cover
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_visibility.get(),
                value = visibilityText,
                icon = AppIcons.Lucide.Sun,
                colors = AppTheme.colors.brutal.purple,
                modifier = Modifier.weight(1f),
            )
            WeatherDetailTile(
                title = Res.string.forecast_details_cloud_cover.get(),
                value = "${block.cloudCoverPercent}%",
                icon = AppIcons.Lucide.CloudRain,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )
        }

        // Row 4: Precipitation
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            val precipType = remember(block.precipitation.types) {
                block.precipitation.types.firstOrNull()?.name ?: "Rain"
            }
            WeatherDetailTile(
                title = Res.string.forecast_details_precipitation.get(),
                value = "${block.precipitation.amount} $precipUnit",
                subtitle = "${block.precipitation.probability}% ${Res.string.forecast_details_precipitation_chance.get()} - $precipType",
                icon = AppIcons.Lucide.CloudRain,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )
            // Spacer for symmetry
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WeatherDetailTile(
    title: String,
    value: String,
    icon: ImageVector,
    colors: BrutalColors,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Title bar with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(colors.bright)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Icon(
                    icon = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = AppTheme.typography.h4,
                    maxLines = 1,
                )
            }

            HorizontalDivider()

            // Value
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
                Text(
                    text = value,
                    style = AppTheme.typography.h2,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.body1.copy(fontSize = 12.sp),
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

// --- Utility Functions ---

private fun degreesToCardinal(degrees: Double): String {
    val normalized = ((degrees % 360) + 360) % 360
    return when {
        normalized < 22.5 || normalized >= 337.5 -> "N"
        normalized < 67.5 -> "NE"
        normalized < 112.5 -> "E"
        normalized < 157.5 -> "SE"
        normalized < 202.5 -> "S"
        normalized < 247.5 -> "SW"
        normalized < 292.5 -> "W"
        else -> "NW"
    }
}

private fun uvIndexLabel(uvIndex: Int): String =
    when {
        uvIndex <= 2 -> "Low"
        uvIndex <= 5 -> "Moderate"
        uvIndex <= 7 -> "High"
        uvIndex <= 10 -> "Very High"
        else -> "Extreme"
    }
