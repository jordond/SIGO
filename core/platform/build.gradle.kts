
import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.platform")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.coil)
            implementation(libs.filekit.core)
            implementation(libs.koin.core)
            implementation(libs.connectivity.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
        }

        val deviceMain by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)

            dependencies {
                implementation(libs.connectivity.device)
            }
        }

        val nonDeviceMain by creating {
            dependsOn(commonMain.get())
            jvmMain.get().dependsOn(this)
            jsMain.get().dependsOn(this)

            dependencies {
                implementation(libs.connectivity.http)
            }
        }
    }
}

android {
    buildFeatures {
        buildConfig = true
    }
}
