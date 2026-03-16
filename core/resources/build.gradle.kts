import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose, name = "core.resources")

kotlin {
    android {
        androidResources.enable = true
    }

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
