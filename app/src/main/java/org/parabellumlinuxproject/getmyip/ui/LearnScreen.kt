package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.parabellumlinuxproject.getmyip.R

private data class Tool(val name: String, val platforms: Int, val description: Int, val url: String)

// Alphabetical order. Plain official addresses only, no referral or tracking parameters.
private val FREE_TOOLS = listOf(
    Tool("Anonsurf", R.string.platform_linux, R.string.tool_anonsurf, "https://docs.parrotsec.org/docs/tools/anonsurf"),
    Tool("Orbot", R.string.platform_android, R.string.tool_orbot, "https://orbot.app"),
    Tool("Proton VPN Free", R.string.platform_multi, R.string.tool_proton_free, "https://protonvpn.com"),
    Tool("RiseupVPN", R.string.platform_multi, R.string.tool_riseup, "https://riseup.net/vpn"),
    Tool("Tor Browser", R.string.platform_multi, R.string.tool_torbrowser, "https://www.torproject.org"),
)

private val PAID_TOOLS = listOf(
    Tool("IVPN", R.string.platform_multi, R.string.tool_ivpn, "https://www.ivpn.net"),
    Tool("Mullvad", R.string.platform_multi, R.string.tool_mullvad, "https://mullvad.net"),
    Tool("Proton VPN Plus", R.string.platform_multi, R.string.tool_proton_plus, "https://protonvpn.com"),
)

@Composable
fun LearnScreen(onBack: () -> Unit) {
    SubScreen(stringResource(R.string.learn), onBack) {
        LearnSection(R.string.learn_ip_title, R.string.learn_ip_body)
        LearnSection(R.string.learn_versions_title, R.string.learn_versions_body)
        LearnSection(R.string.learn_change_title, R.string.learn_change_body)
        InfoCard(stringResource(R.string.learn_tools_title)) {
            SmallNote(stringResource(R.string.learn_tools_intro))
            GroupTitle(stringResource(R.string.learn_free))
            FREE_TOOLS.forEach { ToolItem(it) }
            GroupTitle(stringResource(R.string.learn_paid))
            PAID_TOOLS.forEach { ToolItem(it) }
        }
        LearnSection(R.string.learn_vpn_tor_title, R.string.learn_vpn_tor_body)
    }
}

@Composable
private fun LearnSection(title: Int, body: Int) {
    InfoCard(stringResource(title)) {
        Text(stringResource(body), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun GroupTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ToolItem(tool: Tool) {
    val uriHandler = LocalUriHandler.current
    Column {
        TextButton(
            onClick = { runCatching { uriHandler.openUri(tool.url) } },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(tool.name, fontWeight = FontWeight.Bold)
        }
        SmallNote(stringResource(tool.platforms))
        Text(stringResource(tool.description), style = MaterialTheme.typography.bodySmall)
    }
}
