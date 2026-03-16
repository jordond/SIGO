package now.shouldigooutside.cli.util

import co.touchlab.kermit.Logger
import com.github.ajalt.clikt.command.SuspendingCliktCommand

internal fun SuspendingCliktCommand.prompt(
    message: String,
    required: Boolean = false,
    default: String? = null,
    validate: ((String) -> Unit)? = null,
): String {
    val text = if (default != null) "[$default]" else ""
    echo("$message $text: ", trailingNewline = false)
    val result = readln()
    val error = runCatching { validate?.invoke(result) }.exceptionOrNull()
    if (result != default && error != null) {
        Logger.e(error.message ?: "Invalid input")
        return prompt(message, required, default, validate)
    }

    if (default != null && result.isBlank()) {
        return result
    } else if (required && result.isBlank()) {
        Logger.e("Required field")
        return prompt(message, true, default, validate)
    } else {
        return result
    }
}

private val accepted = listOf('y', 'n')

internal fun SuspendingCliktCommand.confirm(
    message: String,
    default: Boolean = false,
): Boolean {
    val text = if (default) "Y/n" else "y/N"
    echo("$message? ($text) ", trailingNewline = false)
    val result = readln()
    return when {
        result.isBlank() -> default
        result.lowercase().first() in accepted -> result.lowercase() == "y"
        else -> confirm(message, default)
    }
}
