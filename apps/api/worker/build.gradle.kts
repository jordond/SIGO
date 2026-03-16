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
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kermit)
                implementation(libs.kermit.koin)
                implementation(libs.koin.core)
            }
        }
    }
}

tasks.named("compileKotlinJs") {
    dependsOn(versionTask)
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
