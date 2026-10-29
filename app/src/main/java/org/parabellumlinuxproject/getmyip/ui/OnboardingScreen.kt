package org.parabellumlinuxproject.getmyip.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.notify.Notifier

private const val PAGES = 3

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val context = LocalContext.current.applicationContext
    var page by rememberSaveable { mutableIntStateOf(0) }
    var showPolicy by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onFinish() }
    val needsPermission = Build.VERSION.SDK_INT >= 33 && !Notifier.canNotify(context)

    BackHandler(enabled = page > 0) { page-- }

    Scaffold { padding ->
        PageColumn(padding) {
            when (page) {
                0 -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "PLP",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(stringResource(R.string.subtitle), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.onb_tagline),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.onb_welcome_body), style = MaterialTheme.typography.bodyLarge)
                }
                1 -> {
                    PageTitle(stringResource(R.string.onb_privacy_title))
                    Text(stringResource(R.string.onb_privacy_body), style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = { showPolicy = !showPolicy }) {
                        Text(stringResource(if (showPolicy) R.string.onb_hide_policy else R.string.onb_read_policy))
                    }
                    if (showPolicy) PrivacyPolicyContent()
                }
                else -> {
                    PageTitle(stringResource(R.string.onb_notify_title))
                    Text(stringResource(R.string.onb_notify_body), style = MaterialTheme.typography.bodyLarge)
                    SmallNote(stringResource(R.string.settings_notify_note))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SmallNote(stringResource(R.string.onb_page, page + 1, PAGES))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onFinish) {
                    Text(stringResource(if (page < PAGES - 1) R.string.skip else R.string.not_now))
                }
                Button(
                    onClick = {
                        when {
                            page < PAGES - 1 -> page++
                            needsPermission -> permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            else -> onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(
                        stringResource(
                            when {
                                page < PAGES - 1 -> R.string.next
                                needsPermission -> R.string.allow_notifications
                                else -> R.string.start
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PageTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
}
