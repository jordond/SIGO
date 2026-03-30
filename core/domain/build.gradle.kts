import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.domain", tests = true)

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(projects.test)
        }

        commonMain.dependencies {
            implementation(projects.core.model)

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
