import now.shouldigooutside.convention.Platform
import now.shouldigooutside.convention.configureMultiplatform
import now.shouldigooutside.convention.disableExplicitApi

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Ios, name = "iosApp")

kotlin {
    disableExplicitApi()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            binaryOption(
                "bundleId",
                libs.versions.app.name
                    .get(),
            )
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(projects.core.app)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.firebase.kmp.config)
        }
    }
}
