import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "forecast", tests = true)

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(projects.test)
        }

        commonMain.dependencies {
            implementation(projects.core.config)
            implementation(projects.core.model)
            implementation(projects.core.domain)
            implementation(projects.core.widget)
            implementation(projects.core.foundation)
            implementation(projects.core.platform)

            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.stateHolder)
            implementation(libs.bundles.kotlinx)
        }
    }
}
