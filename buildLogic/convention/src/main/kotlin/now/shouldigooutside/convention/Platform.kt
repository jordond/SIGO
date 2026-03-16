package now.shouldigooutside.convention

import now.shouldigooutside.convention.Platform.Android
import now.shouldigooutside.convention.Platform.Ios
import now.shouldigooutside.convention.Platform.Js
import now.shouldigooutside.convention.Platform.Jvm
import now.shouldigooutside.convention.Platform.NodeJs

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
