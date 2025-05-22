import app.sigot.convention.Platform
import app.sigot.convention.configureMultiplatform
import app.sigot.convention.disableExplicitApi
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Android)

kotlin {
    disableExplicitApi()

    sourceSets {
        androidMain.dependencies {
            implementation(projects.core.app)

            implementation(compose.preview)
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
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

android {
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
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}
