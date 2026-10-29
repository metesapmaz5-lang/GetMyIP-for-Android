package org.parabellumlinuxproject.getmyip.core

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal object Http {
    private const val USER_AGENT =
        "PLP-GetMyIP/0.1.0-alpha (+https://github.com/metesapmaz5-lang/GetMyIP-for-Android)"

    fun get(url: String, timeoutMs: Int = 10_000): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = timeoutMs
            conn.readTimeout = timeoutMs
            conn.instanceFollowRedirects = false
            conn.setRequestProperty("User-Agent", USER_AGENT)
            val code = conn.responseCode
            if (code !in 200..299) throw IOException("HTTP $code")
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}
