@file:Suppress("unused")

import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.platform")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)

            api(libs.coil)
            implementation(libs.connectivity.core)
            implementation(libs.filekit.core)
            api(libs.kermit)
            api(libs.koin.core)
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kstore)
            api(libs.ktor.client.core)
            implementation(libs.compass.autocomplete)
            implementation(libs.compass.geolocation)
            implementation(libs.compass.geocoder)

            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.serialization.json)
            implementation(libs.ktor.client.logging)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kstore.file)
        }

        jsMain.dependencies {
            implementation(libs.kstore.storage)
            implementation(libs.ktor.client.js)
            implementation(libs.compass.geolocation.browser)
        }

        val deviceMain by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
            dependencies {
                implementation(libs.connectivity.device)
                implementation(libs.compass.autocomplete.mobile)
                implementation(libs.compass.geocoder.mobile)
                implementation(libs.compass.geolocation.mobile)
                implementation(libs.compass.permissions.mobile)
                implementation(libs.kstore.file)
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
