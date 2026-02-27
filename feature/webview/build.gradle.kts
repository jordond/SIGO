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

configureMultiplatform(Platforms.Compose, name = "webview")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.foundation)
            implementation(projects.core.platform)
            implementation(projects.core.model)
            implementation(projects.core.resources)
            implementation(projects.core.ui)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.navigation)
            implementation(libs.composeWebviewMultiplatform)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
        }
    }
}
