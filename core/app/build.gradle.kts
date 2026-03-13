import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.toolchain.version)
}

configureMultiplatform(Platforms.Compose, name = "app")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.api.client)
            implementation(projects.core.config)
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.ui)

            implementation(projects.feature.forecast)
            implementation(projects.feature.forecast.ui)
            implementation(projects.feature.location)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.settings)
            implementation(projects.feature.webview)

            implementation(libs.compose.windowSizeClass)
            api(libs.kermit)
            implementation(libs.kermit.crashlytics)
            implementation(libs.kermit.koin)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.multiplatformSettings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.room.runtime)
            implementation(libs.materialKolor)
            api(libs.bundles.koin.compose)
            implementation(libs.bundles.stateHolder)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    with(libs.room.compiler) {
        add("kspAndroid", this)
        add("kspJvm", this)
        add("kspIosArm64", this)
        add("kspIosSimulatorArm64", this)
    }
}
