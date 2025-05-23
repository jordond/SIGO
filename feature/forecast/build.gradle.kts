import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "forecast")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.config)
            implementation(projects.core.model)
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.platform)

            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.koin.compose)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.stateHolder)
        }
    }
}
