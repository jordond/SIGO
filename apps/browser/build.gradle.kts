import app.sigot.convention.Platform
import app.sigot.convention.configureMultiplatform
import app.sigot.convention.disableExplicitApi

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Js)

kotlin {
    disableExplicitApi()

    sourceSets {
        jsMain.dependencies {
            implementation(projects.core.app)

            implementation(libs.koin.core)
        }
    }
}
