import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.lumo)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Compose, name = "core.ui")

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.core.resources)
            implementation(projects.core.uiIcons)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.navigation.composee)
            api(libs.compass.geolocation)
            api(libs.compose.windowSizeClass)
            api(libs.composables)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.coil)
            implementation(libs.materialKolor)
        }

        val skikoMain by creating {
            dependsOn(commonMain.get())
            nativeMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)

            dependencies {
                implementation(libs.kotlinx.atomicfu)
            }
        }
    }
}
