plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
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
