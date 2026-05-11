plugins {
    id("com.android.library")
}

android {
    namespace = "now.shouldigooutside.auto"
    compileSdk = libs.versions.sdk.compile
        .get()
        .toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min
            .get()
            .toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.foundation)
    implementation(projects.core.platform)
    implementation(projects.core.resources)
    implementation(projects.core.widget)
    implementation(projects.feature.forecast)

    implementation(libs.androidx.car.app)
    implementation(libs.androidx.core)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections)

    testImplementation(libs.androidx.car.app.testing)
    testImplementation(projects.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
