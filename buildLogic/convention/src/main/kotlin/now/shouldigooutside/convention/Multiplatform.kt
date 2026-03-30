package now.shouldigooutside.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

fun Project.configureMultiplatform(
    platform: Platform,
    name: String = this.name,
    compose: Boolean = extensions.findByType(ComposeExtension::class.java) != null,
    cocoapods: Boolean = false,
    tests: Boolean = false,
    log: Boolean = false,
) {
    configureMultiplatform(listOf(platform), name, compose, cocoapods, tests, log)
}

fun Project.configureMultiplatform(
    platforms: List<Platform> = Platforms.Mobile,
    name: String = this.name,
    compose: Boolean = extensions.findByType(ComposeExtension::class.java) != null,
    cocoapods: Boolean = false,
    tests: Boolean = false,
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
        configurePlatforms(
            project = this@configureMultiplatform,
            platforms = platforms,
            name = name,
            cocoapods = cocoapods,
            tests = tests,
            log = log,
        )
        if (platforms.contains(Platform.Ios)) {
            configureNativeOptIn()
        }
    }

    val hasAndroid = platforms.contains(Platform.Android)
    if (compose) {
        composeDependencies(hasAndroid)
    }
}

@OptIn(ExperimentalWasmDsl::class)
internal fun KotlinMultiplatformExtension.configurePlatforms(
    project: Project,
    platforms: List<Platform> = Platforms.All,
    name: String,
    cocoapods: Boolean,
    tests: Boolean,
    log: Boolean,
) {
    applyDefaultHierarchyTemplate()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }

    if (platforms.contains(Platform.Android)) {
        (this as ExtensionAware)
            .extensions
            .findByType(KotlinMultiplatformAndroidLibraryTarget::class.java)
            ?.apply {
                namespace = project.buildNamespace(name)
                compileSdk = project.libs
                    .findVersion("sdk-compile")
                    .get()
                    .toString()
                    .toInt()

                minSdk = project.libs
                    .findVersion("sdk-min")
                    .get()
                    .toString()
                    .toInt()

                compilerOptions.jvmTarget.set(
                    JvmTarget.fromTarget(project.jvmTargetVersion.toString()),
                )
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

    if (tests) {
        testDependencies()
    }
}

fun KotlinMultiplatformExtension.testDependencies() {
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        implementation(project.libs.findLibrary("kotest-assertions").get())
        implementation(project.libs.findLibrary("kotlinx-coroutines-test").get())
        implementation(project.libs.findLibrary("turbine").get())
    }
}

internal fun KotlinMultiplatformExtension.configureNativeOptIn() {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.experimental.ExperimentalNativeApi")
        }
    }
}
