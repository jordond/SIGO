package now.shouldigooutside.core.ui.ktx

import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue

public val PagerState.isFirstPage: Boolean
    get() = currentPage == 0

public val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1

public fun PagerState.previousPage(): Int =
    if (currentPage == 0) {
        currentPage
    } else {
        currentPage - 1
    }

public fun PagerState.nextPage(): Int =
    if (currentPage == pageCount - 1) {
        currentPage
    } else {
        currentPage + 1
    }

public suspend fun PagerState.scrollToPreviousPage() {
    if (canScrollBackward) {
        animateScrollToPage(previousPage())
    }
}

public suspend fun PagerState.scrollToNextPage() {
    if (canScrollForward) {
        animateScrollToPage(nextPage())
    }
}

public fun PagerState.offsetForPage(page: Int): Float = (currentPage - page) + currentPageOffsetFraction

public fun PagerState.startOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtLeast(0f)

public fun PagerState.endOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtMost(0f)

public fun PagerState.indicatorOffsetForPage(page: Int): Float =
    1f - offsetForPage(page).coerceIn(-1f, 1f).absoluteValue

public fun Modifier.pagerCarousel(
    pagerState: PagerState,
    index: Int,
): Modifier =
    graphicsLayer {
        val startOffset = pagerState.startOffsetForPage(index)
        translationX = size.width * (startOffset * .99f)

        alpha = (2f - startOffset) / 2f
        val blur = (startOffset * 20f).coerceAtLeast(0.1f)
        renderEffect = BlurEffect(blur, blur)

        val scale = 1f - (startOffset * .1f)
        scaleX = scale
        scaleY = scale
    }

public fun Modifier.pagerCarouselRight(
    pagerState: PagerState,
    index: Int,
): Modifier =
    this
        .zIndex(1f - pagerState.endOffsetForPage(index).absoluteValue)
        .graphicsLayer {
            val endOffset = pagerState.endOffsetForPage(index)
            translationX = size.width * (endOffset * .99f)

            alpha = (2f + endOffset) / 2f
            val blur = (endOffset * 20f).coerceAtLeast(0.1f)
            renderEffect = BlurEffect(blur, blur)

            val scale = 1f - (endOffset * -.1f)
            scaleX = scale
            scaleY = scale
        }
