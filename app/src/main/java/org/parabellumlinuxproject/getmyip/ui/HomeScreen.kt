package org.parabellumlinuxproject.getmyip.ui

import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.parabellumlinuxproject.getmyip.AppVisibility
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.Snapshot
import org.parabellumlinuxproject.getmyip.core.IpInfo
import org.parabellumlinuxproject.getmyip.core.IpLeakProvider
import org.parabellumlinuxproject.getmyip.core.TorExitList
import org.parabellumlinuxproject.getmyip.history.HistoryDb
import org.parabellumlinuxproject.getmyip.history.IpChange
import org.parabellumlinuxproject.getmyip.net.Transport
import org.parabellumlinuxproject.getmyip.notify.Notifier
import org.parabellumlinuxproject.getmyip.settings.AppSettings
import org.parabellumlinuxproject.getmyip.takeSnapshot

private data class ChangeNotice(val sinceLastTime: Boolean, val changes: List<IpChange>)

@Composable
fun HomeScreen(onOpen: (Screen) -> Unit) {
    val context = LocalContext.current.applicationContext
    val settings = remember { AppSettings(context) }
    var snapshot by remember { mutableStateOf<Snapshot?>(null) }
    var providerName by remember { mutableStateOf(IpLeakProvider.displayName) }
    var notice by remember { mutableStateOf<ChangeNotice?>(null) }
    var loading by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(refreshKey) {
        loading = true
        if (refreshKey > 0) delay(1_000) // let a new network settle
        val provider = settings.provider()
        providerName = provider.displayName
        val snap = takeSnapshot(context, provider)
        snapshot = snap
        val changes = withContext(Dispatchers.IO) {
            HistoryDb.get(context).record(snap, provider.displayName)
        }
        if (changes.isNotEmpty()) {
            notice = ChangeNotice(sinceLastTime = refreshKey == 0, changes = changes)
            if (refreshKey > 0 && !AppVisibility.foreground && settings.notifyOnChange) {
                Notifier.ipChanged(context, changes)
            }
        }
        loading = false
    }

    // Refresh automatically when the network changes (Wi-Fi, mobile data, VPN on/off).
    DisposableEffect(Unit) {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            private var first = true
            override fun onAvailable(network: Network) {
                if (first) first = false else refreshKey++
            }
            override fun onLost(network: Network) {
                refreshKey++
            }
        }
        cm?.registerDefaultNetworkCallback(callback)
        onDispose { cm?.unregisterNetworkCallback(callback) }
    }

    Scaffold { padding ->
        PageColumn(padding) {
            Header()
            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            notice?.let { ChangeBanner(it) { notice = null } }
            val s = snapshot
            IpCard(stringResource(R.string.public_ipv4), s?.v4, done = s != null)
            IpCard(stringResource(R.string.public_ipv6), s?.v6, done = s != null)
            ProviderCard(s?.v4 ?: s?.v6)
            NetworkCard(s)
            Button(
                onClick = { refreshKey++ },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text(stringResource(R.string.refresh))
            }
            SmallNote(stringResource(R.string.data_sources, providerName, TorExitList.NAME))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = { onOpen(Screen.HISTORY) }) { Text(stringResource(R.string.history)) }
                TextButton(onClick = { onOpen(Screen.SETTINGS) }) { Text(stringResource(R.string.settings)) }
                TextButton(onClick = { onOpen(Screen.ABOUT) }) { Text(stringResource(R.string.about)) }
            }
        }
    }
}

@Composable
private fun Header() {
    Column {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            stringResource(R.string.subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChangeBanner(notice: ChangeNotice, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                stringResource(if (notice.sinceLastTime) R.string.changed_since_last else R.string.changed_now),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            notice.changes.forEach { c ->
                Text(
                    "${familyLabel(c.family)}: ${c.previous} → ${c.current}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                )
            }
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
        }
    }
}

@Composable
private fun IpCard(title: String, info: IpInfo?, done: Boolean) {
    InfoCard(title) {
        val text = when {
            info != null -> info.ip
            done -> stringResource(R.string.not_available)
            else -> stringResource(R.string.loading)
        }
        SelectionContainer {
            Text(
                text,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                color = if (info != null) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProviderCard(info: IpInfo?) {
    InfoCard(stringResource(R.string.provider)) {
        LabeledValue(R.string.isp, info?.isp)
        LabeledValue(R.string.asn, info?.asn?.let { "AS$it" })
        LabeledValue(
            R.string.location,
            listOfNotNull(info?.city, info?.country).joinToString(", ").ifEmpty { null },
        )
    }
}

@Composable
private fun NetworkCard(s: Snapshot?) {
    InfoCard(stringResource(R.string.network)) {
        if (s == null) {
            Text(stringResource(R.string.loading))
        } else {
            LabeledValue(R.string.connection, stringResource(transportLabel(s.net.transport)))
            LabeledValue(
                R.string.vpn,
                stringResource(if (s.net.isVpn) R.string.vpn_on else R.string.vpn_off),
            )
            LabeledValue(
                R.string.tor,
                stringResource(
                    when (s.tor) {
                        true -> R.string.tor_yes
                        false -> R.string.tor_no
                        null -> R.string.tor_unknown
                    }
                ),
            )
            LabeledValue(R.string.local_ip, s.net.localAddresses.joinToString("\n").ifEmpty { null })
        }
    }
}

private fun transportLabel(t: Transport): Int = when (t) {
    Transport.WIFI -> R.string.net_wifi
    Transport.CELLULAR -> R.string.net_cellular
    Transport.ETHERNET -> R.string.net_ethernet
    Transport.VPN -> R.string.net_vpn
    Transport.OTHER -> R.string.net_other
    Transport.OFFLINE -> R.string.net_offline
}
