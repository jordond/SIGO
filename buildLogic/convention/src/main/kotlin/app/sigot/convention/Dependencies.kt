package app.sigot.convention

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin.Dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("DEPRECATION")
fun Project.composeDependencies(hasAndroid: Boolean) {
    extensions.configure<KotlinMultiplatformExtension> {
        compilerOptions {
            optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        }
        sourceSets.commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.runtimeSaveable)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
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

@Suppress("DEPRECATION")
private val KotlinMultiplatformExtension.compose: Dependencies
    get() =
        (this as ExtensionAware).extensions.getByName("compose") as Dependencies
