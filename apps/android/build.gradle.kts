import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = libs.versions.app.name
        .get() + ".android"
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
            .get() + ".android"

        versionCode = libs.versions.app.android.code
            .get()
            .toInt()
        versionName = libs.versions.app.android.version
            .get()
    }

    val secretsFile = File(".app/secrets/secrets.properties")
    val releaseKeyFile = project.rootDir.resolve(".app/secrets/sigot_release.key")

    val secrets = Properties()
    val hasSecrets = secretsFile.exists() && releaseKeyFile.exists()

    if (hasSecrets) {
        secrets.load(secretsFile.inputStream())
    }

    signingConfigs {
        if (hasSecrets) {
            create("release") {
                storeFile = releaseKeyFile
                storePassword = secrets["KEYSTORE_PASSWORD"] as String
                keyAlias = secrets["KEYSTORE_KEY_ALIAS"] as String
                keyPassword = secrets["KEYSTORE_KEY_PASSWORD"] as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = if (hasSecrets) {
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.app)

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
}
