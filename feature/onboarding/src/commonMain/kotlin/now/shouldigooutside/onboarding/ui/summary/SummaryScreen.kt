package now.shouldigooutside.onboarding.ui.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.onboarding_summary_text
import now.shouldigooutside.core.resources.onboarding_summary_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.ArrowBigDown
import now.shouldigooutside.onboarding.ui.OnboardingScreenPreview
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination

@Composable
internal fun SummaryScreen(modifier: Modifier = Modifier) {
    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
        ) {
            Text(
                text = Res.string.onboarding_summary_title,
                style = AppTheme.typography.h2,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = AppTheme.colors.secondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 300.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = Res.string.onboarding_summary_text,
                        style = AppTheme.typography.h1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = 52.sp),
                    )
                }
            }
        }

        Icon(
            icon = AppIcons.Lucide.ArrowBigDown,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(150.dp),
        )
    }
}

@Preview
@Composable
private fun SummaryScreenPreview() {
    OnboardingScreenPreview(OnboardingDestination.Summary)
}
