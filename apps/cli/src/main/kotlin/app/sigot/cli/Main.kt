package app.sigot.cli

import app.sigot.cli.config.ApiTokenProvider
import app.sigot.cli.config.CliConfigRepo
import app.sigot.cli.di.initKoin
import app.sigot.cli.util.BaseCommand
import app.sigot.cli.util.prompt
import app.sigot.core.domain.forecast.ForecastRepo
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.option
import io.github.vinceglb.filekit.FileKit
import kotlin.system.exitProcess

class Cli(
    private val configRepo: CliConfigRepo,
    private val apiTokenProvider: ApiTokenProvider,
    private val forecastRepo: ForecastRepo,
) : BaseCommand() {
    private val token: String? by option(
        "--token",
        "-t",
        help = "The token to use for authentication",
    )

    private val location: String? by argument(
        name = "location",
        help = "The location to get the forecast for, if empty previous location will be used",
    ).optional()

    override suspend fun execute() {
        debug("Starting CLI...")
        debug("Getting token...")
        val token = getToken()
        apiTokenProvider.token = token
        debug("Token: $token")

        val targetLocation = location ?: configRepo.get()?.lastLocation
        val location = targetLocation ?: prompt("Enter your location", required = true)

        info("Getting forecast for $location")
        val result = forecastRepo
            .forecastFor(location)
            .onFailure { cause ->
                error("Failed to get forecast: ${cause.message}")
                throw cause
            }

        val forecast = result.getOrNull()
        if (forecast == null) {
            error("No forecast data available")
            throw RuntimeException("No forecast data available")
        }

        success("Forecast for ${forecast.location.name}:")
        success("\tTemperature: ${forecast.current.temperature.value} K")
        success("\tHumidity:    ${forecast.current.humidity} %")
        success("\tWind Speed:  ${forecast.current.wind.speed} m/s")
        success("\tSevere Risk: ${forecast.current.severeWeatherRisk}")
        exitProcess(0)
    }

    private suspend fun getToken(): String {
        val token = this.token
        if (token != null) {
            configRepo.update { it.copy(token = token) }
            return token
        }

        val saved = configRepo.get()?.token
        if (saved == null) {
            val input = prompt("Enter your VisualCrossing API key", required = true)
            configRepo.update { it.copy(token = input) }
            return input
        }

        return saved
    }
}

suspend fun main(args: Array<String>) {
    FileKit.init("app.sigot.cli")
    val di = initKoin()

    // TODO: The saving of the ConfigRepo to the Store isn't working
    Cli(
        configRepo = di.get(),
        apiTokenProvider = di.get(),
        forecastRepo = di.get(),
    ).main(args)
}
