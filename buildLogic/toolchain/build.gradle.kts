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
        register("appVersionToolchain") {
            id = "toolchain.version"
            implementationClass = "now.shouldigooutside.toolchain.AppVersionToolchain"
        }
    }
}
