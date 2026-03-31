import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform
import now.shouldigooutside.convention.testDependencies

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.domain")

kotlin {
    sourceSets {
        testDependencies()

        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.koin.core)
            implementation(libs.compose.runtime.annotation)
            implementation(libs.compass.geolocation)
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
            implementation(libs.stateHolder)
        }
    }
}
