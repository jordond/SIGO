plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    js {
        nodejs()
        binaries.executable()

        compilations["main"].packageJson {
            version = "1.0.0"
        }

        outputModuleName.set("index")
    }

    sourceSets {
        jsMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.feature.forecast)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
        }
    }
}
