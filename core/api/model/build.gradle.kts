@file:Suppress("unused")

import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.api.model")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.kotlinx.serialization.json)
        }
    }
}
