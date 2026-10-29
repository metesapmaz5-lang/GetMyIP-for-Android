package org.parabellumlinuxproject.getmyip.ui

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.parabellumlinuxproject.getmyip.R

/** Debug builds only. Shows the last crash so it can be copied and shared by hand. */
@Composable
fun CrashDialog(text: String, onClose: () -> Unit) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(stringResource(R.string.crash_title)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.crash_body), style = MaterialTheme.typography.bodySmall)
                Text(text, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                if (copied) Text(stringResource(R.string.copied), color = MaterialTheme.colorScheme.primary)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                context.getSystemService(ClipboardManager::class.java)
                    ?.setPrimaryClip(ClipData.newPlainText("GetMyIP crash", text))
                copied = true
            }) { Text(stringResource(R.string.copy)) }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text(stringResource(R.string.delete)) }
        },
    )
}
