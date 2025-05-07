import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose)

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = libs.versions.app.name
        .get() + ".core.resources"
}

android {
    namespace = libs.versions.app.name
        .get() + ".core.resources"
}
