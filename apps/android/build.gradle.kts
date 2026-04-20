import java.util.Properties
import kotlin.io.encoding.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = libs.versions.app.name
        .get()
    compileSdk = libs.versions.sdk.compile
        .get()
        .toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min
            .get()
            .toInt()
        targetSdk = libs.versions.sdk.target
            .get()
            .toInt()

        applicationId = libs.versions.app.name
            .get()

        versionCode = libs.versions.app.android.code
            .get()
            .toInt()
        versionName = libs.versions.app.android.version
            .get()
    }

    val envProps = Properties().apply {
        val envFile = project.rootDir.resolve("app-env.properties")
        if (envFile.exists()) load(envFile.inputStream())
    }

    fun prop(key: String): String? = envProps[key]?.toString() ?: System.getenv(key)

    val keystorePassword = prop("APP_KEYSTORE_PASSWORD")
    val keystoreKeyAlias = prop("APP_KEYSTORE_KEY_ALIAS")
    val keystoreBase64 = prop("APP_KEYSTORE_BASE64")

    //noinspection WrongGradleMethod
    val hasSigningConfig =
        listOf(keystorePassword, keystoreKeyAlias, keystoreBase64).all { !it.isNullOrBlank() }

    val releaseKeyFile = if (hasSigningConfig) {
        val decoded = Base64.decode(keystoreBase64!!)
        rootProject.layout.projectDirectory.file(".gradle/signing/release.jks").asFile.apply {
            parentFile.mkdirs()
            writeBytes(decoded)
        }
    } else {
        null
    }

    signingConfigs {
        if (hasSigningConfig && releaseKeyFile != null) {
            create("release") {
                storeFile = releaseKeyFile
                storePassword = keystorePassword
                keyAlias = keystoreKeyAlias
                keyPassword = keystorePassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = if (hasSigningConfig) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.app)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.platform)
    implementation(projects.core.ui)
    implementation(projects.core.widget)

    coreLibraryDesugaring(libs.desugar)

    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.glance.preview)
    implementation(libs.kotlinx.collections)
    implementation(libs.work.runtime)
}
