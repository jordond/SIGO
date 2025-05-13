import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All)

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
        }
    }
}

android {
    namespace = libs.versions.app.name
        .get() + ".forecase.domain"
}
