enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SIGOT"

pluginManagement {
    repositories {
        google {
            @Suppress("UnstableApiUsage")
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("buildLogic")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jogamp.org/deployment/maven")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":apps:android")
include(":apps:api:worker")
include(":apps:cli")
include(":apps:desktop")
include(":apps:ios")

include(":core:api:client")
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
