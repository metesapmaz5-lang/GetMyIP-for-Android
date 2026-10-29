package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.parabellumlinuxproject.getmyip.R
import org.parabellumlinuxproject.getmyip.core.DnsLeakTest
import org.parabellumlinuxproject.getmyip.core.DnsResolver

@Composable
fun DnsTestScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var running by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<List<DnsResolver>?>(null) }

    SubScreen(stringResource(R.string.dns_test), onBack) {
        Text(stringResource(R.string.dns_intro), style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = {
                running = true
                failed = false
                result = null
                scope.launch {
                    val outcome = withContext(Dispatchers.IO) { runCatching { DnsLeakTest.run() } }
                    result = outcome.getOrNull()
                    failed = outcome.isFailure
                    running = false
                }
            },
            enabled = !running,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text(stringResource(if (running) R.string.dns_running else R.string.dns_start))
        }
        if (running) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (failed) Text(stringResource(R.string.dns_failed), color = MaterialTheme.colorScheme.error)
        result?.let { list ->
            if (list.isEmpty()) {
                Text(stringResource(R.string.dns_none))
            } else {
                InfoCard(stringResource(R.string.dns_result_title)) {
                    list.forEach { r ->
                        SelectionContainer {
                            Text(
                                r.ip,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        val details = listOfNotNull(
                            r.info?.isp,
                            r.info?.asn?.let { "AS$it" },
                            r.info?.country,
                        ).joinToString(" · ")
                        if (details.isNotEmpty()) SmallNote(details)
                    }
                }
            }
        }
        SmallNote(stringResource(R.string.dns_hint))
        SmallNote(stringResource(R.string.dns_private_note))
        SmallNote(stringResource(R.string.dns_uses, DnsLeakTest.NAME))
    }
}
