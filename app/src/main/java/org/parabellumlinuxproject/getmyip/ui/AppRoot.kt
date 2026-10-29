package org.parabellumlinuxproject.getmyip.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

enum class Screen { HOME, HISTORY, DNS, LEARN, SETTINGS, ABOUT }

@Composable
fun AppRoot() {
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    val goHome = { screen = Screen.HOME }
    BackHandler(enabled = screen != Screen.HOME) { goHome() }
    when (screen) {
        Screen.HOME -> HomeScreen(onOpen = { screen = it })
        Screen.HISTORY -> HistoryScreen(onBack = goHome)
        Screen.DNS -> DnsTestScreen(onBack = goHome)
        Screen.LEARN -> LearnScreen(onBack = goHome)
        Screen.SETTINGS -> SettingsScreen(onBack = goHome)
        Screen.ABOUT -> AboutScreen(onBack = goHome)
    }
}
