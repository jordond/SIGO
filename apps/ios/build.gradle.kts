import app.sigot.convention.Platform
import app.sigot.convention.configureMultiplatform
import app.sigot.convention.disableExplicitApi

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Ios, name = "app")

kotlin {
    disableExplicitApi()
    sourceSets {
        iosMain.dependencies {
            implementation(projects.core.app)
        }
    }
}
