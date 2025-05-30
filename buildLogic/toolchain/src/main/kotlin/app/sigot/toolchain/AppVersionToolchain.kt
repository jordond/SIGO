package app.sigot.toolchain

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class AppVersionToolchain : Plugin<Project> {
    override fun apply(target: Project) {
        val task =
            target.tasks.register<AppVersion>("appVersion") {
                group = "toolchain"
                description = "Generates a file with the current version and git hash"
                version.set("unknown")
                code.set(0)
            }

        // target.tasks.named("preBuild") {
        //     dependsOn(task)
        // }
    }
}
