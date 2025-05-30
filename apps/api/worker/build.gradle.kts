import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlinx.serialization)
}

buildkonfig {
    packageName = "app.sigot.build"
    val version = libs.versions.app.android.version
        .get()

    defaultConfigs {
        buildConfigField(STRING, "API_VERSION", version, const = true)
    }
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
            implementation(projects.core.api.server)
            implementation(projects.core.domain)
            implementation(projects.core.foundation)
            implementation(projects.core.model)
            implementation(projects.core.platform)
            implementation(projects.feature.forecast)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
            implementation(libs.kermit.koin)
            implementation(libs.koin.core)
        }
    }
}
