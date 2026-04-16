package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

/**
 * An alert issued for the location.
 *
 * @property title Human-readable alert title.
 * @property description Full alert body.
 * @property event Short event keyword, e.g. "rainfall".
 * @property headline One-line summary, e.g. "yellow warning - rainfall - in effect".
 * @property onset Start of the alert's active window.
 * @property ends End of the alert's active window.
 * @property link Source URL for more information.
 * @property id Stable upstream identifier, when present.
 */
@Immutable
public data class Alert(
    val title: String,
    val description: String,
    val event: String? = null,
    val headline: String? = null,
    val onset: Instant? = null,
    val ends: Instant? = null,
    val link: String? = null,
    val id: String? = null,
) {
    public val descriptionParagraphs: List<String>
        get() = description
            .split(PARAGRAPH_SEPARATOR)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    public companion object {
        internal const val PARAGRAPH_SEPARATOR: String = "###"
    }
}
