plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(
        libs.versions.jvmTarget
            .get()
            .toInt(),
    )
}

dependencies {
    compileOnly(libs.bundles.logic.plugins)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "convention.multiplatform"
            implementationClass = "now.shouldigooutside.convention.plugin.MultiplatformConventionPlugin"
        }
    }
}
