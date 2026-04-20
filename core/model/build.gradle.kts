import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import now.shouldigooutside.convention.Platforms
import now.shouldigooutside.convention.configureMultiplatform
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.poko)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.All, name = "core.model", tests = true)

buildkonfig {
    packageName = "now.shouldigooutside.build"
    val envProps = Properties()
    val envPropsFile = rootProject.file("app-env.properties")
    if (envPropsFile.exists()) {
        envPropsFile.inputStream().use { envProps.load(it) }
        println("Loaded app-env.properties from ${envPropsFile.absolutePath}")
    } else {
        println("Warning: app-env.properties not found at ${envPropsFile.absolutePath}")
    }

    exposeObjectWithName = "BuildConfig"

    defaultConfigs {
        val useDirectApi = envProps.getProperty("USE_DIRECT_API", "false")
        buildConfigField(BOOLEAN, "USE_DIRECT_API", useDirectApi, const = true)

        val enableInternalSettings = envProps.getProperty("ENABLE_INTERNAL_SETTINGS", "false")
        buildConfigField(BOOLEAN, "ENABLE_INTERNAL_SETTINGS", enableInternalSettings, const = true)

        val apiToken = envProps.getProperty("FORECAST_API_KEY", "")
        buildConfigField(STRING, "FORECAST_API_KEY", apiToken, const = true)

        val backendUrl = envProps
            .getProperty("APP_BACKEND_URL", "")
            .takeIf { it.isNotBlank() } ?: System.getenv("APP_BACKEND_URL") ?: ""
        if (backendUrl.isBlank()) {
            if (!useDirectApi.toBoolean() && apiToken.isNullOrEmpty()) {
                error(
                    """
                    APP_BACKEND_URL not set in ${envPropsFile.absolutePath} or environment variables. 
                    Either set `APP_BACKEND_URL` or set `USE_DIRECT_API` to `true`, 
                    and provide an API key value for `FORECAST_API_KEY`.
                    """.trimIndent(),
                )
            }
        }
        buildConfigField(STRING, "BACKEND_URL", backendUrl, const = true)
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.collections)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
            implementation(libs.compose.runtime.annotation)
        }
    }
}
