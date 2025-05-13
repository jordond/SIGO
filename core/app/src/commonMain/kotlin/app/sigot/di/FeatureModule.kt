package app.sigot.di

import app.sigot.forecast.data.forecastDataModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun featureModule(): Module =
    module {
        includes(
            forecastDataModule(),
        )
    }
