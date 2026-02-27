import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform
import app.sigot.toolchain.AppVersion

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.toolchain.version)
}

configureMultiplatform(Platforms.Compose, name = "settings")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.config)
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.ui)
            implementation(projects.core.uiIcons)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
            implementation(libs.koin.core)
            implementation(libs.kstore)
            implementation(libs.bundles.koin.compose)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.stateHolder)
        }
    }
}

val versionTask = tasks.named<AppVersion>("appVersion") {
    version = libs.versions.app.android.version
        .get()
    code = libs.versions.app.android.code
        .get()
        .toInt()
}

kotlin.sourceSets.commonMain.configure {
    kotlin.srcDir(versionTask)
}
