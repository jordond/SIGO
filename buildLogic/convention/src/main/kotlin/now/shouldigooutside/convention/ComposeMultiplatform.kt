package now.shouldigooutside.convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.configureComposeOptIn() {
    sourceSets.all {
        languageSettings {
            optIn("androidx.compose.animation.ExperimentalAnimationApi")
            optIn("androidx.compose.ui.ExperimentalComposeUiApi")
            optIn("androidx.compose.animation.ExperimentalSharedTransitionApi")
        }
    }
}
