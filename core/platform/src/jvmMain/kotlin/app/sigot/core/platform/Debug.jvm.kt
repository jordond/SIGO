package app.sigot.core.platform

private val possibleDebugArgs = listOf(
    "jdwp",
    "-Xdebug",
    "-agentlib:jdwp",
    "--debug",
)

public actual val isDebug: Boolean = runCatching {
    // Check if assertions are enabled, which typically indicates a debug build
    var isAssertOn = false
    assert(
        {
            isAssertOn = true
            true
        }(),
    )

    // Also check if Java Debug Wire Protocol (JDWP) agent is active
    val jvmArgs = System.getProperty("java.vm.name", "") +
        System.getProperty("java.vm.args", "") +
        System.getProperty("java.class.path", "")
    val hasDebugAgent = possibleDebugArgs.any { jvmArgs.contains(it) }

    isAssertOn || hasDebugAgent
}.getOrElse { false }
