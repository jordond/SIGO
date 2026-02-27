import app.sigot.convention.Platform
import app.sigot.convention.configureMultiplatform
import app.sigot.convention.disableExplicitApi
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hotReload)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Jvm, name = "desktop")

kotlin {
    disableExplicitApi()
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.core.app)
            implementation(projects.core.resources)

            implementation(compose.desktop.currentOs)
            implementation(libs.filekit.core)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        val name = libs.versions.app.name
            .get()
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = name
            packageVersion = libs.versions.app.desktop.version
                .get()

            linux {
                iconFile.set(project.file("icons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("icons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("icons/MacosIcon.icns"))
                bundleID = "$name.desktop"
            }
        }
    }
}
