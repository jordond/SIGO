import app.sigot.convention.Platforms
import app.sigot.convention.configureMultiplatform
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All)

buildkonfig {
    packageName = "app.sigot.build"
    val envProps = Properties()
    val envPropsFile = rootProject.file("app-env.properties")
    if (envPropsFile.exists()) {
        envPropsFile.inputStream().use { envProps.load(it) }
        println("Loaded app-env.properties from ${envPropsFile.absolutePath}")
    } else {
        println("Warning: app-env.properties not found at ${envPropsFile.absolutePath}")
    }

    defaultConfigs {
        val backendUrl = envProps.getProperty("APP_BACKEND_URL", "")
        if (backendUrl.isBlank()) {
            error("APP_BACKEND_URL not set in ${envPropsFile.absolutePath}")
        }
        buildConfigField(STRING, "BACKEND_URL", backendUrl, const = true)

        val apiToken = envProps.getProperty("APP_WEATHER_API_TOKEN", "")
        buildConfigField(STRING, "WEATHER_API_TOKEN", apiToken, const = true)
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
        }
    }
}

android {
    namespace = libs.versions.app.name
        .get() + ".core.model"
}
