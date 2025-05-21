
import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.config")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)

            implementation(libs.firebase.kmp.config)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.stateHolder)
        }
    }
}
