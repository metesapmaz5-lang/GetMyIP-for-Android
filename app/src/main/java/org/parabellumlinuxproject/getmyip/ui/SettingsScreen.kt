package org.parabellumlinuxproject.getmyip.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.core.IfconfigCoProvider
import org.parabellumlinuxproject.getmyip.core.IpLeakProvider
import org.parabellumlinuxproject.getmyip.core.Providers
import org.parabellumlinuxproject.getmyip.notify.Notifier
import org.parabellumlinuxproject.getmyip.settings.AppSettings

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val settings = remember { AppSettings(context) }
    var selected by remember { mutableStateOf(settings.providerId) }
    var url by remember { mutableStateOf(settings.customUrl) }
    var message by remember { mutableStateOf<Int?>(null) }
    var notify by remember { mutableStateOf(settings.notifyOnChange) }
    var permissionTick by remember { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionTick++ }

    SubScreen(stringResource(R.string.settings), onBack) {
        InfoCard(stringResource(R.string.settings_provider_title)) {
            ProviderOption(
                id = AppSettings.PROVIDER_IPLEAK,
                title = IpLeakProvider.displayName,
                description = stringResource(R.string.provider_ipleak_desc),
                selected = selected,
            ) {
                selected = it
                settings.providerId = it
                message = null
            }
            ProviderOption(
                id = AppSettings.PROVIDER_IFCONFIG,
                title = IfconfigCoProvider.displayName,
                description = stringResource(R.string.provider_ifconfig_desc),
                selected = selected,
            ) {
                selected = it
                settings.providerId = it
                message = null
            }
            ProviderOption(
                id = AppSettings.PROVIDER_CUSTOM,
                title = stringResource(R.string.provider_custom),
                description = stringResource(R.string.provider_custom_desc),
                selected = selected,
            ) {
                // Saved only after a valid address is entered.
                selected = it
                message = null
            }
            if (selected == AppSettings.PROVIDER_CUSTOM) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.custom_url_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        if (Providers.custom(url) != null) {
                            settings.customUrl = url.trim()
                            settings.providerId = AppSettings.PROVIDER_CUSTOM
                            message = R.string.saved
                        } else {
                            message = R.string.custom_url_invalid
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(stringResource(R.string.save))
                }
                message?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.primary) }
            }
            SmallNote(stringResource(R.string.settings_provider_note))
        }

        InfoCard(stringResource(R.string.settings_notify_title)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.settings_notify_label),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = notify,
                    onCheckedChange = { on ->
                        notify = on
                        settings.notifyOnChange = on
                        if (on && Build.VERSION.SDK_INT >= 33 && !Notifier.canNotify(context)) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                )
            }
            // permissionTick makes this line re-check after the permission dialog closes.
            if (notify && permissionTick >= 0 && !Notifier.canNotify(context)) {
                SmallNote(stringResource(R.string.settings_notify_denied))
            }
            SmallNote(stringResource(R.string.settings_notify_note))
        }
    }
}

@Composable
private fun ProviderOption(
    id: String,
    title: String,
    description: String,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected == id, onClick = { onSelect(id) }, role = Role.RadioButton)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected == id, onClick = null)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            SmallNote(description)
        }
    }
}
