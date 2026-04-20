plugins {
    application
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

application {
    applicationName = "sigo"
    mainClass.set("now.shouldigooutside.cli.MainKt")
}

kotlin {
    jvmToolchain(
        libs.versions.jvmTarget
            .get()
            .toInt(),
    )
}

tasks.withType<Sync>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.foundation)
    implementation(projects.core.model)
    implementation(projects.core.platform)
    implementation(projects.feature.forecast)
    implementation(projects.feature.settings)

    implementation(libs.clikt)
    implementation(libs.filekit.core)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mordant)
    implementation(libs.mordant.coroutines)
    implementation(libs.slf4j.nop)
}
