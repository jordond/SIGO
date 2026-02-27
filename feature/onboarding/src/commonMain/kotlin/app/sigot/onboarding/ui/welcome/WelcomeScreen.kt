package app.sigot.onboarding.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.resources.Res
import app.sigot.core.resources.onboarding_welcome
import app.sigot.core.resources.onboarding_welcome_footer
import app.sigot.core.resources.onboarding_welcome_preamble
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.preview.AppPreview
import app.sigot.onboarding.ui.OnboardingScreenPreview
import app.sigot.onboarding.ui.navigation.OnboardingDestination

@Composable
internal fun WelcomeScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            text = Res.string.onboarding_welcome_preamble,
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
                    text = Res.string.onboarding_welcome,
                    style = AppTheme.typography.h1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 52.sp),
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = Res.string.onboarding_welcome_footer,
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    AppPreview {
        OnboardingScreenPreview(OnboardingDestination.Welcome)
    }
}
