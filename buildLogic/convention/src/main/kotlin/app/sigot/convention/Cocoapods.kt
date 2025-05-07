package app.sigot.convention

import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension.CocoapodsDependency

fun CocoapodsExtension.pod(
    name: String,
    version: String? = null,
    extraOps: Boolean = false,
    configure: CocoapodsDependency.() -> Unit = {},
) {
    pod(name) {
        if (!version.isNullOrBlank()) {
            this.version = version
        }

        if (extraOps) {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        apply(configure)
    }
}
