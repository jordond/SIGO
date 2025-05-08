
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.kermit)
            implementation(libs.coil)
            implementation(libs.filekit.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
    }
}

android {
    namespace = libs.versions.app.name
        .get() + ".core.platform"

    buildFeatures {
        buildConfig = true
    }
}
