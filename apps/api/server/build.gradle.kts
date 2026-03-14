plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
}

application {
    mainClass.set("app.sigot.api.server.MainKt")
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
    implementation(libs.slf4j.nop)
}
