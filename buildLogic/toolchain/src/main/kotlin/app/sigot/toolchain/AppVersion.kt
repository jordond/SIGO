package app.sigot.toolchain

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class AppVersion : DefaultTask() {
    @get:Input
    val gitHash: Provider<String> = project.providers
        .exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val code: Property<Int>

    @get:OutputDirectory
    val outputDir: Provider<Directory> =
        project.layout.buildDirectory.dir("gen")

    @TaskAction
    fun writeVersionFile() {
        val outputDir = outputDir.get().asFile
        val packageFile = File(outputDir, "app/sigot/core").also { it.mkdirs() }
        val versionFile = File(packageFile, "Version.kt")
        println("Writing version file to $versionFile")
        println("Version: ${version.get()}")
        println("Code: ${code.get()}")
        println("Git hash: ${gitHash.get()}")
        versionFile.writeText(
            """
            // Generated file. Do not edit!
            package app.sigot.core
            
            @Suppress("MayBeConstant")
            public object Version {
                public val NAME: String = "${version.get()}"
                public val CODE: Int = ${code.get()}
                public val GIT_SHA: String = "${gitHash.get().trim()}"
            }
            """.trimIndent(),
        )
    }
}
