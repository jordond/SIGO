import app.sigot.convention.Platform
import app.sigot.convention.configureMultiplatform
import app.sigot.convention.disableExplicitApi

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Ios, name = "iosApp")

kotlin {
    disableExplicitApi()

    sourceSets {
        iosMain.dependencies {
            implementation(projects.core.app)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.firebase.kmp.config)
        }
    }
}
