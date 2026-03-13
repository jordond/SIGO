package app.sigot.onboarding.ui.location.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalIsAndroid
import app.sigot.core.ui.colors
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.rememberIcon
import app.sigot.core.ui.rememberText
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.location_permission_cta_open_settings
import now.shouldigooutside.core.resources.location_permission_cta_request
import now.shouldigooutside.core.resources.location_permission_open_settings_rationale
import now.shouldigooutside.core.resources.location_permission_rationale

@Composable
internal fun LocationPermissionStatusCard(
    permissionStatus: LocationPermissionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = permissionStatus.colors()
    LocationCard(
        colors = colors,
        modifier = modifier,
    ) {
        Header(
            icon = permissionStatus.rememberIcon(),
            text = permissionStatus.rememberText(),
            indicatorColor = colors.bright,
        )

        AnimatedVisibility(permissionStatus !is LocationPermissionStatus.Granted) {
            val denied = remember(permissionStatus) {
                permissionStatus as? LocationPermissionStatus.Denied
            }
            PermissionStatusContent(
                isDenied = denied != null,
                isDeniedPermanently = denied?.permanently == true,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun PermissionStatusContent(
    isDenied: Boolean,
    isDeniedPermanently: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = Res.string.location_permission_rationale,
        )

        AnimatedVisibility(isDeniedPermanently) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colors.error,
                ),
            ) {
                Text(
                    text = Res.string.location_permission_open_settings_rationale,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = (!isDeniedPermanently || LocalIsAndroid.current),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Button(
                onClick = onClick,
                variant = ButtonVariant.SecondaryElevated,
            ) {
                val text = remember(isDeniedPermanently) {
                    if (isDeniedPermanently) {
                        Res.string.location_permission_cta_open_settings
                    } else {
                        Res.string.location_permission_cta_request
                    }
                }
                Text(text)
            }
        }
    }
}

@Preview
@Composable
private fun LocationPermissionStatusCardPreview() {
    AppPreview {
        Column(Modifier.padding(16.dp)) {
            Text("Unknown", style = AppTheme.typography.h3)
            LocationPermissionStatusCard(
                permissionStatus = LocationPermissionStatus.Unknown,
                onClick = {},
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text("Granted", style = AppTheme.typography.h3)
            LocationPermissionStatusCard(
                permissionStatus = LocationPermissionStatus.Granted,
                onClick = {},
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text("Denied", style = AppTheme.typography.h3)
            LocationPermissionStatusCard(
                permissionStatus = LocationPermissionStatus.Denied(false),
                onClick = {},
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun LocationPermissionStatusDeniedPreview() {
    AppPreview {
        Column(Modifier.padding(16.dp)) {
            Text("Denied Forever", style = AppTheme.typography.h3)
            CompositionLocalProvider(LocalIsAndroid provides false) {
                LocationPermissionStatusCard(
                    permissionStatus = LocationPermissionStatus.Denied(true),
                    onClick = {},
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            Text("Denied Forever (android)", style = AppTheme.typography.h3)
            CompositionLocalProvider(LocalIsAndroid provides true) {
                LocationPermissionStatusCard(
                    permissionStatus = LocationPermissionStatus.Denied(true),
                    onClick = {},
                )
            }
        }
    }
}
