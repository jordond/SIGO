import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.lumo)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose, name = "core.ui.components")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.ui)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.coil)
            implementation(libs.materialKolor)
        }

        androidMain.dependencies {
            implementation(compose.preview)
        }
    }
}
