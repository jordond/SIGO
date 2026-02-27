package app.sigot.onboarding.ui.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowBigDown
import app.sigot.onboarding.ui.OnboardingScreenPreview
import app.sigot.onboarding.ui.navigation.OnboardingDestination

@Composable
internal fun SummaryScreen(modifier: Modifier = Modifier) {
    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
        ) {
            Text(
                text = "We're all set",
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
                        text = "Let's get started!",
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
