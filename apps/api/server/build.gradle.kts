@file:Suppress("unused")

import now.shouldigooutside.toolchain.AppVersion

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.toolchain.version)
    application
}

val versionTask = tasks.named<AppVersion>("appVersion") {
    version = libs.versions.api.server.version
        .get()
}

application {
    mainClass.set("now.shouldigooutside.api.server.MainKt")
}

sourceSets {
    main {
        kotlin.srcDir(versionTask)
    }
}

tasks.named("compileKotlin") {
    dependsOn(versionTask)
}

dependencies {
    implementation(projects.core.api.server)
    implementation(projects.core.domain)
    implementation(projects.core.foundation)
    implementation(projects.core.model)
    implementation(projects.core.platform)
    implementation(projects.feature.forecast)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.okhttp)

    implementation(libs.koin.core)
    implementation(libs.kermit)
    implementation(libs.kermit.koin)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
}
