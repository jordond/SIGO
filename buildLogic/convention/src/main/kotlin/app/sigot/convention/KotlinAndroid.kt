package app.sigot.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal fun Project.configureAndroid(
    name: String = this.name,
    compose: Boolean,
    desugar: Boolean,
) {
    setNamespace(name)
    extensions.findByType(LibraryExtension::class.java)?.let { extension ->
        configureKotlinAndroid(extension, desugar)
        if (compose) {
            configureAndroidCompose(extension)
        }
    }
    extensions.findByType(ApplicationExtension::class.java)?.let { extension ->
        configureKotlinAndroid(extension, desugar)
        extension.defaultConfig.apply {
            versionCode = libs
                .findVersion("app-android-code")
                .get()
                .toString()
                .toInt()
            versionName = libs.findVersion("app-android-version").get().toString()
            targetSdk = libs
                .findVersion("sdk-target")
                .get()
                .toString()
                .toInt()
        }
        if (compose) {
            configureAndroidCompose(extension)
        }
    }
}

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    desugar: Boolean,
) {
    if (desugar) {
        dependencies {
            add("coreLibraryDesugaring", libs.findLibrary("desugar").get())
        }
    }

    commonExtension.apply {
        compileSdk = libs
            .findVersion("sdk-compile")
            .get()
            .toString()
            .toInt()

        defaultConfig {
            minSdk = libs
                .findVersion("sdk-min")
                .get()
                .toString()
                .toInt()
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

            isCoreLibraryDesugaringEnabled = desugar
        }

        sourceSets["main"].apply {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

fun configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }
}

internal fun Project.setNamespace(name: String) {
    val packageName = libs.findVersion("app-name").get().toString()
    extensions.findByType(LibraryExtension::class.java)?.apply {
        namespace = "$packageName.${name.replace("-", ".")}"
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    extensions.findByType(ApplicationExtension::class.java)?.apply {
        namespace = "$packageName.android"
        defaultConfig {
            applicationId = "$packageName.android"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
}

fun Project.disableExplicitApi() {
    extensions.configure<KotlinBaseExtension> {
        explicitApi()
        explicitApi = ExplicitApiMode.Disabled
    }
}

internal fun Project.configureKotlin() {
    extensions.configure<KotlinBaseExtension> {
        explicitApi()
        jvmToolchain(jvmTargetVersion)
    }
}
