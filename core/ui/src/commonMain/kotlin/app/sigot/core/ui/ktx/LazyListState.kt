package app.sigot.core.ui.ktx

import androidx.compose.foundation.lazy.LazyListState

public fun LazyListState.previousItem(): Int =
    if (firstVisibleItemIndex == 0) {
        firstVisibleItemIndex
    } else {
        firstVisibleItemIndex - 1
    }

public fun LazyListState.nextItem(): Int =
    if (firstVisibleItemIndex == layoutInfo.totalItemsCount - 1) {
        firstVisibleItemIndex
    } else {
        firstVisibleItemIndex + 1
    }

public suspend fun LazyListState.scrollToPreviousPage() {
    if (canScrollBackward) {
        animateScrollToItem(previousItem())
    }
}

public suspend fun LazyListState.scrollToNextPage() {
    if (canScrollForward) {
        animateScrollToItem(nextItem())
    }
}

public suspend fun LazyListState.scrollToTop() {
    animateScrollToItem(0)
}

public suspend fun LazyListState.scrollToBottom(animate: Boolean = true) {
    if (animate) {
        animateScrollToItem(layoutInfo.totalItemsCount - 1)
    } else {
        scrollToItem(layoutInfo.totalItemsCount - 1)
    }
}
