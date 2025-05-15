@file:Suppress("unused")

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
            api(libs.coil)
            implementation(libs.connectivity.core)
            implementation(libs.filekit.core)
            api(libs.kermit)
            api(libs.koin.core)
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kstore)
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.serialization.json)
            implementation(libs.ktor.client.logging)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
        }

        jsMain.dependencies {
            implementation(libs.kstore.storage)
            implementation(libs.ktor.client.js)
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

        val nonJsMain by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
            dependencies {
                implementation(libs.kstore.file)
            }
        }
    }
}

android {
    buildFeatures {
        buildConfig = true
    }
}
