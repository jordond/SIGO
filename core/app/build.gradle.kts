import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform
import org.jetbrains.compose.ExperimentalComposeLibrary

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
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.ui)

            implementation(projects.feature.forecast)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.settings)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.windowSizeClass)
            api(libs.kermit)
            implementation(libs.kermit.koin)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.composee)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.multiplatformSettings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.room.runtime)
            implementation(libs.materialKolor)
            implementation(libs.bundles.koin.compose)
            implementation(libs.bundles.stateHolder)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activity.compose)
        }
    }
}

// https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    with(libs.room.compiler) {
        add("kspAndroid", this)
        add("kspJvm", this)
        add("kspIosX64", this)
        add("kspIosArm64", this)
        add("kspIosSimulatorArm64", this)
    }
}
