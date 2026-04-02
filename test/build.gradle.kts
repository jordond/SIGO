import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "test")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(projects.core.domain)
            api(projects.core.config)
            api(projects.core.foundation)
            api(libs.stateHolder)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.kotest.assertions)
            api(libs.kotlinx.coroutines.test)
            api(libs.turbine)
        }
    }
}
