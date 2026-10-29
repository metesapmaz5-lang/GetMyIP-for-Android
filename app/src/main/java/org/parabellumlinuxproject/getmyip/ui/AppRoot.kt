package org.parabellumlinuxproject.getmyip.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.parabellumlinuxproject.getmyip.debug.CrashLog
import org.parabellumlinuxproject.getmyip.settings.AppSettings

enum class Screen { HOME, HISTORY, DNS, LEARN, SETTINGS, PRIVACY, ABOUT }

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val settings = remember { AppSettings(context) }
    var onboarded by rememberSaveable { mutableStateOf(settings.onboardingDone) }
    var crash by remember { mutableStateOf(CrashLog.read(context)) }
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    val goHome = { screen = Screen.HOME }

    if (!onboarded) {
        OnboardingScreen(onFinish = {
            settings.onboardingDone = true
            onboarded = true
        })
    } else {
        BackHandler(enabled = screen != Screen.HOME) {
            screen = if (screen == Screen.PRIVACY) Screen.SETTINGS else Screen.HOME
        }
        when (screen) {
            Screen.HOME -> HomeScreen(onOpen = { screen = it })
            Screen.HISTORY -> HistoryScreen(onBack = goHome)
            Screen.DNS -> DnsTestScreen(onBack = goHome)
            Screen.LEARN -> LearnScreen(onBack = goHome)
            Screen.SETTINGS -> SettingsScreen(onBack = goHome, onOpenPrivacy = { screen = Screen.PRIVACY })
            Screen.PRIVACY -> PrivacyScreen(onBack = { screen = Screen.SETTINGS })
            Screen.ABOUT -> AboutScreen(onBack = goHome)
        }
    }

    crash?.let { text ->
        CrashDialog(text, onClose = {
            CrashLog.clear(context)
            crash = null
        })
    }
}
