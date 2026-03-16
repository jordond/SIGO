package now.shouldigooutside.convention

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
            implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
            implementation(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            implementation(libs.findLibrary("compose-material3").get())
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-foundation").get())
            implementation(libs.findLibrary("compose-resources").get())
            implementation(libs.findLibrary("compose-ui").get())
            implementation(libs.findLibrary("compose-ui-tooling-preview").get())
        }

        sourceSets.commonTest.dependencies {
            implementation(libs.findLibrary("compose-ui-test").get())
        }

        if (hasAndroid) {
            sourceSets.androidMain.dependencies {
                implementation(libs.findLibrary("compose-ui-tooling").get())
                implementation(libs.findLibrary("compose-ui-tooling-preview").get())
            }
        }

        configureComposeOptIn()
    }
}

@Suppress("DEPRECATION")
private val KotlinMultiplatformExtension.compose: Dependencies
    get() =
        (this as ExtensionAware).extensions.getByName("compose") as Dependencies
