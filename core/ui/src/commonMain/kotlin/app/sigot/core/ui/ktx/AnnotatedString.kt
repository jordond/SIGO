package app.sigot.core.ui.ktx

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
public fun AnnotatedString.Builder.addTag(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    pushStringAnnotation(tag = text, annotation = text)
    withStyle(SpanStyle(color = color, textDecoration = TextDecoration.Underline)) {
        append(text)
    }
    pop()
}

public fun AnnotatedString.onTagClick(
    text: String,
    offset: Int,
    block: () -> Unit,
) {
    getStringAnnotations(tag = text, start = offset, end = offset)
        .firstOrNull()
        ?.let { block() }
}
