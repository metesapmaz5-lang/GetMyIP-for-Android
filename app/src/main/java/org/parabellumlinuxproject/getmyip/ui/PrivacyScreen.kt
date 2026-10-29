package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.parabellumlinuxproject.getmyip.R

internal const val PRIVACY_CONTACT = "metesapmaz5@gmail.com"

private val SECTIONS = listOf(
    R.string.privacy_s1_title to R.string.privacy_s1_body,
    R.string.privacy_s2_title to R.string.privacy_s2_body,
    R.string.privacy_s3_title to R.string.privacy_s3_body,
    R.string.privacy_s4_title to R.string.privacy_s4_body,
    R.string.privacy_s5_title to R.string.privacy_s5_body,
    R.string.privacy_s6_title to R.string.privacy_s6_body,
)

@Composable
internal fun PrivacyPolicyContent() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SmallNote(stringResource(R.string.privacy_intro))
        SECTIONS.forEach { (title, body) ->
            Text(
                stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                if (body == R.string.privacy_s5_body) stringResource(body, PRIVACY_CONTACT) else stringResource(body),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    SubScreen(stringResource(R.string.privacy_title), onBack) {
        PrivacyPolicyContent()
    }
}
