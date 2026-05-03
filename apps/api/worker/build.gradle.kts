@file:Suppress("unused")

import now.shouldigooutside.toolchain.AppVersion

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.toolchain.version)
}

val versionTask = tasks.named<AppVersion>("appVersion") {
    version = libs.versions.api.server.version
        .get()
}

kotlin {
    js {
        nodejs()
        binaries.executable()

        useEsModules()
        generateTypeScriptDefinitions()
        outputModuleName.set("index")
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir(versionTask)

            dependencies {
                implementation(projects.core.api.server)
                implementation(projects.core.domain)
                implementation(projects.core.foundation)
                implementation(projects.core.model)
                implementation(projects.core.platform)
                implementation(projects.feature.forecast)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kermit)
                implementation(libs.kermit.koin)
                implementation(libs.koin.core)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

tasks.named("compileKotlinJs") {
    dependsOn(versionTask)
}

// Kotlin/JS ES-modules + skiko: the compile-sync task copies compiled .mjs files into the
// flat test-package kotlin/ directory, but the skiko/coil companion files that those .mjs
// files import with a relative `./` path are left behind in packages_imported/.  Copy them
// manually so that `jsNodeTest` can resolve all imports.
abstract class CopySkikoCompanionFiles : DefaultTask() {
    @get:OutputDirectory
    abstract val testKotlinOutput: DirectoryProperty

    @TaskAction
    fun copy() {
        val dst = testKotlinOutput.get().asFile
        if (!dst.exists()) return
        // Write a no-op stub that satisfies the `import { api } from './js-reexport-symbols.mjs'`
        // contract without touching browser-only APIs (window/WebGL/WASM) that don't exist in Node.
        dst.resolve("js-reexport-symbols.mjs").writeText(
            "// Node.js stub — skiko WASM not needed for server-side tests\nexport const api = {};\n",
        )
        // skiko.mjs is imported by skiko-kjs.mjs; provide a minimal stub.
        dst.resolve("skiko.mjs").writeText(
            "// Node.js stub\nexport const skikoApi = {};\n",
        )
    }
}

val copySkikoCompanionFiles by tasks.registering(CopySkikoCompanionFiles::class) {
    dependsOn("jsTestTestDevelopmentExecutableCompileSync")
    testKotlinOutput.set(
        rootProject.layout.buildDirectory.dir("js/packages/index-test/kotlin"),
    )
}

tasks.named("jsNodeTest") {
    dependsOn(copySkikoCompanionFiles)
}

afterEvaluate {
    val apiVersion = libs.versions.api.server.version
        .get()
    kotlin.js().compilations.named("main") {
        packageJson {
            version = apiVersion
            main = "./index.mjs"
            customField("type", "module")
        }
    }
}
