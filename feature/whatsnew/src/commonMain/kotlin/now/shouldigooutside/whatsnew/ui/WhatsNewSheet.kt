package now.shouldigooutside.whatsnew.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.whats_new_got_it
import now.shouldigooutside.core.resources.whats_new_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContentColor
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ModalBottomSheet
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.whatsnew.data.WhatsNewPage
import now.shouldigooutside.whatsnew.data.WhatsNewRegistry
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
public fun WhatsNewSheet() {
    val model: WhatsNewModel = koinViewModel()
    val state by model.collectAsState()

    WhatsNewSheet(
        isVisible = state.isVisible,
        pages = state.entries,
        onDismiss = model::dismiss,
    )
}

@Composable
internal fun WhatsNewSheet(
    isVisible: Boolean,
    pages: PersistentList<WhatsNewPage>,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
    ) {
        WhatsNewSheetContent(
            pages = pages,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun WhatsNewSheetContent(
    pages: PersistentList<WhatsNewPage>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.standard)
            .padding(bottom = AppTheme.spacing.standard),
    ) {
        Text(
            text = Res.string.whats_new_title,
            style = AppTheme.typography.h1,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pages.size > 1) {
            val pagerState = rememberPagerState(pageCount = { pages.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { pageIndex ->
                WhatsNewPageContent(
                    page = pages[pageIndex],
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PageIndicator(pagerState = pagerState)
        } else if (pages.isNotEmpty()) {
            WhatsNewPageContent(
                page = pages.first(),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = Res.string.whats_new_got_it.get(),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun WhatsNewPageContent(
    page: WhatsNewPage,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = AppTheme.spacing.small),
    ) {
        ElevatedCard {
            Image(
                painter = painterResource(page.image),
                contentDescription = page.title.get(),
                contentScale = page.scale,
                modifier = Modifier
                    .clip(AppTheme.shapes.medium)
                    .height(250.dp)
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = page.title,
            style = AppTheme.typography.h2,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = page.description,
            style = AppTheme.typography.body1,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val size by animateDpAsState(if (isSelected) 12.dp else 8.dp)
            val color by animateColorAsState(
                if (isSelected) {
                    LocalContentColor.current
                } else {
                    LocalContentColor.current.copy(alpha = 0.3f)
                },
            )

            Box(
                modifier = Modifier
                    .height(size)
                    .width(30.dp)
                    .clip(RectangleShape)
                    .background(color),
            )
        }
    }
}

private class Params : PreviewParameterProvider<PersistentList<WhatsNewPage>> {
    override val values: Sequence<PersistentList<WhatsNewPage>>
        get() = WhatsNewRegistry.pages.map { persistentListOf(it) }.asSequence()
}

@Preview
@Composable
private fun WhatsNewSheetPreview(
    @PreviewParameter(Params::class) pages: PersistentList<WhatsNewPage>,
) {
    AppPreview {
        WhatsNewSheetContent(
            pages = pages,
            onDismiss = {},
        )
    }
}
