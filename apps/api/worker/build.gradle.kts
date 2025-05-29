plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    js {
        nodejs()
        binaries.executable()

        useEsModules()
        generateTypeScriptDefinitions()
        outputModuleName.set("index")

        compilations["main"].packageJson {
            version = "1.0.0"
            main = "./index.mjs"
            customField("type", "module")
        }
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
            implementation(libs.kermit)
            implementation(libs.kermit.koin)
            implementation(libs.koin.core)
        }
    }
}
