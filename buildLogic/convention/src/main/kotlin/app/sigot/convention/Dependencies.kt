package app.sigot.convention

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin.Dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.composeDependencies(hasAndroid: Boolean) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.runtimeSaveable)
            implementation(compose.foundation)
            // TODO: Temporarily set material3 to a specific version until CMP supports 1.8.0
            // implementation(compose.material3)
            implementation("org.jetbrains.compose.material3:material3:1.8.0-alpha03")
            implementation(compose.components.resources)
        }

        if (hasAndroid) {
            sourceSets.androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.findLibrary("androidx-compose-ui-tooling").get())
                implementation(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            }
        }

        configureComposeOptIn()
    }
}

private val KotlinMultiplatformExtension.compose: Dependencies
    get() =
        (this as ExtensionAware).extensions.getByName("compose") as Dependencies
