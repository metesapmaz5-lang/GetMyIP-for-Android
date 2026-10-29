package org.parabellumlinuxproject.getmyip

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.parabellumlinuxproject.getmyip.ui.AppRoot
import org.parabellumlinuxproject.getmyip.ui.PlpTheme

/** True while the app is on screen. Used to decide between an in-app banner and a notification. */
object AppVisibility {
    @Volatile var foreground = false
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent { PlpTheme { AppRoot() } }
    }

    override fun onResume() {
        super.onResume()
        AppVisibility.foreground = true
    }

    override fun onPause() {
        AppVisibility.foreground = false
        super.onPause()
    }
}
