import now.shouldigooutside.convention.Platform
import now.shouldigooutside.convention.configureMultiplatform
import now.shouldigooutside.convention.disableExplicitApi
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Ios, name = "iosApp")

kotlin {
    disableExplicitApi()

    val name = libs.versions.app.name
        .get()
    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            binaryOption("bundleId", name)
            export(projects.core.app)
            export(projects.core.widget)
        }
    }

    sourceSets {
        iosMain.dependencies {
            api(projects.core.app)
            api(projects.core.widget)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.firebase.kmp.config)
        }
    }
}
