import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose, name = "location")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.config)
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.ui)
            implementation(projects.core.uiIcons)

            implementation(libs.compass.autocomplete)
            implementation(libs.compass.geolocation)
            implementation(libs.compass.geocoder)
            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.koin.compose)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.stateHolder)
        }
    }
}
