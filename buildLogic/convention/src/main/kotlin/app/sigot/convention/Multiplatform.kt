package app.sigot.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

fun Project.configureMultiplatform(
    platform: Platform,
    name: String = this.name,
    compose: Boolean = extensions.findByType(ComposeExtension::class.java) != null,
    log: Boolean = false,
) {
    configureMultiplatform(listOf(platform), name, compose, log)
}

fun Project.configureMultiplatform(
    platforms: List<Platform> = Platforms.Mobile,
    name: String = this.name,
    compose: Boolean = extensions.findByType(ComposeExtension::class.java) != null,
    cocoapods: Boolean = false,
    desugar: Boolean = false,
    log: Boolean = false,
) {
    if (log) {
        println(
            "Configuring multiplatform project: $name with" +
                " platforms: ${platforms.joinToString(", ")} and compose: $compose, " +
                "and cocoapods: $cocoapods.",
        )
    }

    extensions.configure<KotlinMultiplatformExtension> {
        configureKotlin()
        configurePlatforms(platforms, name, cocoapods, log)
        if (platforms.contains(Platform.Ios)) {
            configureNativeOptIn()
        }
    }

    val hasAndroid = platforms.contains(Platform.Android)
    if (hasAndroid) {
        configureAndroid(name, compose, desugar)
    }

    if (compose) {
        composeDependencies(hasAndroid)
    }
}

@OptIn(ExperimentalWasmDsl::class)
internal fun KotlinMultiplatformExtension.configurePlatforms(
    platforms: List<Platform> = Platforms.All,
    name: String,
    cocoapods: Boolean,
    log: Boolean,
) {
    applyDefaultHierarchyTemplate()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    if (platforms.contains(Platform.Android)) {
        androidTarget {
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
            @Suppress("OPT_IN_USAGE")
            instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }

    if (platforms.contains(Platform.Jvm)) {
        jvm()
    }

    if (platforms.contains(Platform.Js)) {
        js {
            browser()

            if (platforms.contains(Platform.NodeJs)) {
                nodejs()
            }
        }
    }

    if (platforms.contains(Platform.Ios)) {
        if (log) {
            println("Configuring iOS...")
        }
        if (!cocoapods) {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64(),
            ).forEach { target ->
                if (log) {
                    println("Configuring iOS target: ${target.name}")
                }
                target.binaries.framework {
                    baseName = name
                    isStatic = true
                }
            }
        } else {
            if (log) {
                println("Cocoapods is enabled, skipping iOS framework generation.")
            }
            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    this.targets.withType(KotlinNativeTarget::class.java) {
        compilations["main"]
            .compileTaskProvider
            .configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexport-kdoc")
                }
            }
    }

    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

internal fun KotlinMultiplatformExtension.configureNativeOptIn() {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.experimental.ExperimentalNativeApi")
        }
    }
}
