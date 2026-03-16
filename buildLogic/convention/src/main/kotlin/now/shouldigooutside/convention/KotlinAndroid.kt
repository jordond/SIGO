package now.shouldigooutside.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal fun Project.buildNamespace(name: String): String {
    val appName = libs.findVersion("app-name").get().toString()
    if (name.isBlank()) return appName
    return appName + "." + name.replace("-", ".")
}

fun Project.disableExplicitApi() {
    extensions.configure<KotlinBaseExtension> {
        explicitApi = ExplicitApiMode.Disabled
    }
}

internal fun Project.configureKotlin() {
    extensions.configure<KotlinBaseExtension> {
        explicitApi()
        jvmToolchain(jvmTargetVersion)
    }
}
