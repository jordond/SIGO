import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose, name = "core.icons")

@Suppress("DEPRECATION")
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.materialIconsExtended)
            implementation(libs.kermit)
        }
    }
}
