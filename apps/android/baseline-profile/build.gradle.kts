plugins {
    id("com.android.test")
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "now.shouldigooutside.baselineprofile"
    compileSdk = libs.versions.sdk.compile
        .get()
        .toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min
            .get()
            .toInt()
        targetSdk = libs.versions.sdk.target
            .get()
            .toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":apps:android"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api34"
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.test.espresso.core)
}
