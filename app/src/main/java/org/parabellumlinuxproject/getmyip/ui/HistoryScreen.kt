package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.history.HistoryDb
import org.parabellumlinuxproject.getmyip.history.HistoryEntry
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var entries by remember { mutableStateOf<List<HistoryEntry>?>(null) }
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        entries = withContext(Dispatchers.IO) { HistoryDb.get(context).all() }
    }

    SubScreen(stringResource(R.string.history), onBack) {
        SmallNote(stringResource(R.string.history_local_note))
        val list = entries
        when {
            list == null -> Text(stringResource(R.string.loading))
            list.isEmpty() -> Text(stringResource(R.string.history_empty))
            else -> {
                list.forEach { HistoryItem(it) }
                TextButton(onClick = { confirmClear = true }) {
                    Text(stringResource(R.string.history_clear))
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.history_clear_title)) },
            text = { Text(stringResource(R.string.history_clear_text)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    scope.launch {
                        withContext(Dispatchers.IO) { HistoryDb.get(context).clear() }
                        entries = emptyList()
                    }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun HistoryItem(e: HistoryEntry) {
    val time = remember(e.time) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(e.time))
    }
    val vpnText = stringResource(R.string.history_flag_vpn)
    val torText = stringResource(R.string.history_flag_tor)
    val viaText = stringResource(R.string.history_via, e.provider)
    InfoCard("$time · ${familyLabel(e.family)}") {
        SelectionContainer {
            Text(
                e.ip,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        val details = listOfNotNull(
            e.isp,
            e.asn?.let { "AS$it" },
            listOfNotNull(e.city, e.country).joinToString(", ").ifEmpty { null },
        ).joinToString(" · ")
        if (details.isNotEmpty()) SmallNote(details)
        val flags = listOfNotNull(viaText, vpnText.takeIf { e.vpn }, torText.takeIf { e.tor == true })
        SmallNote(flags.joinToString(" · "))
    }
}
