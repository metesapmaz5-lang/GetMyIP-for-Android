package org.parabellumlinuxproject.getmyip

import android.app.Application
import org.parabellumlinuxproject.getmyip.debug.CrashLog

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLog.install(this)
    }
}
