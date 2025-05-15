plugins {
    application
    alias(libs.plugins.jvm)
}

application {
    applicationName = "sigot"
    mainClass.set("app.sigot.cli.MainKt")
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.foundation)
    implementation(projects.core.model)
    implementation(projects.core.platform)
    implementation(projects.feature.forecast)

    implementation(libs.clikt)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation(libs.mordant)
    implementation(libs.mordant.coroutines)
}
