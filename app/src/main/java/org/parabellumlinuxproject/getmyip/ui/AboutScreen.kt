package org.parabellumlinuxproject.getmyip.ui

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import org.parabellumlinuxproject.getmyip.R

private const val SOURCE_URL = "https://github.com/metesapmaz5-lang/GetMyIP-for-Android"
private const val CONTACT_EMAIL = "metesapmaz5@gmail.com"
private const val BUILT_BY = "Parabellum Linux Project"
private const val DEVELOPER = "Mete Sapmaz"

private val LIBRARIES = listOf(
    "Jetpack Compose, AndroidX" to "Apache License 2.0",
    "Kotlin" to "Apache License 2.0",
    "OkHttp, Okio" to "Apache License 2.0",
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull() ?: "?"
    }

    SubScreen(stringResource(R.string.about), onBack) {
        InfoCard(stringResource(R.string.app_name)) {
            Text(stringResource(R.string.about_version, version))
            LabeledValue(R.string.about_built_by, BUILT_BY)
            LabeledValue(R.string.about_developer, DEVELOPER)
            SmallNote(stringResource(R.string.about_license))
        }
        InfoCard(stringResource(R.string.about_funding_title)) {
            Text(stringResource(R.string.about_funding))
            Text(stringResource(R.string.about_not_affiliated))
            Text(stringResource(R.string.about_support_tor))
        }
        InfoCard(stringResource(R.string.about_source)) {
            TextButton(onClick = { runCatching { uriHandler.openUri(SOURCE_URL) } }) {
                Text(SOURCE_URL)
            }
        }
        InfoCard(stringResource(R.string.about_contact)) {
            SelectionContainer { Text(CONTACT_EMAIL) }
        }
        InfoCard(stringResource(R.string.about_licenses_title)) {
            LIBRARIES.forEach { (name, license) -> Text("$name — $license") }
        }
    }
}
