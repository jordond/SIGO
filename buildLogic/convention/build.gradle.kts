plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.bundles.logic.plugins)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "convention.multiplatform"
            implementationClass = "app.sigot.convention.plugin.MultiplatformConventionPlugin"
        }
    }
}
