package app.sigot.convention

import app.sigot.convention.Platform.Android
import app.sigot.convention.Platform.Ios
import app.sigot.convention.Platform.Js
import app.sigot.convention.Platform.Jvm
import app.sigot.convention.Platform.NodeJs

enum class Platform {
    Android,
    Ios,
    Jvm,
    Js,
    NodeJs,
}

object Platforms {
    val All: List<Platform> = listOf(Android, Ios, Jvm, Js, NodeJs)
    val Mobile: List<Platform> = listOf(Android, Ios)
    val Javascript: List<Platform> = listOf(Js, NodeJs)
    val Compose: List<Platform> = listOf(Android, Ios, Jvm)
}
