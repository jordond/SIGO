enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SIGOT"

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("buildLogic")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        mavenCentral()
    }
}
plugins {
    // https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#set-up-automatic-provisioning-of-the-jetbrains-runtime-jbr-via-gradle
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}

include(":apps:android")
include(":apps:api:worker")
include(":apps:cli")
include(":apps:desktop")
include(":apps:ios")

include(":core:api:server")
include(":core:app")
include(":core:config")
include(":core:domain")
include(":core:foundation")
include(":core:model")
include(":core:platform")
include(":core:resources")
include(":core:ui")
include(":core:ui-icons")

include(":feature:forecast")
include(":feature:forecast:ui")
include(":feature:location")
include(":feature:onboarding")
include(":feature:settings")
include(":feature:webview")
