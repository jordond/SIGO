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
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(libs.stateHolder)
    implementation(projects.core.foundation)
    implementation(projects.core.platform)
    implementation(projects.core.resources)
    implementation(projects.core.widget)
    implementation(projects.feature.forecast)

    implementation(libs.androidx.car.app)
    implementation(libs.androidx.core)
    implementation(libs.compose.resources)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections)
    implementation(libs.kotlinx.datetime)
}
