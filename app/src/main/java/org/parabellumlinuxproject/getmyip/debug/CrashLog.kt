package org.parabellumlinuxproject.getmyip.debug

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import java.io.File
import java.util.Date

/**
 * Debug builds only: saves the last crash to a local file so it can be shown and copied
 * on the next launch. Nothing is ever sent anywhere.
 */
object CrashLog {
    private const val FILE_NAME = "last_crash.txt"

    fun install(context: Context) {
        if (!isDebuggable(context)) return
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching {
                File(app.filesDir, FILE_NAME).writeText(
                    buildString {
                        appendLine("Time: ${Date()}")
                        appendLine("Thread: ${thread.name}")
                        appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                        appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                        appendLine()
                        append(error.stackTraceToString())
                    }
                )
            }
            previous?.uncaughtException(thread, error)
        }
    }

    fun read(context: Context): String? {
        if (!isDebuggable(context)) return null
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) runCatching { file.readText() }.getOrNull() else null
    }

    fun clear(context: Context) {
        File(context.filesDir, FILE_NAME).delete()
    }

    private fun isDebuggable(context: Context): Boolean =
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
